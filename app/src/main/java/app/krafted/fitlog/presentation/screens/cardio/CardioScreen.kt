package app.krafted.fitlog.presentation.screens.cardio

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.krafted.fitlog.domain.model.CardioType
import kotlinx.coroutines.flow.collectLatest
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardioScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHistory: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CardioViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle one-time events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CardioEvent.SaveSuccess -> {
                    onNavigateToHistory()
                }
                is CardioEvent.SaveError -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Log Cardio",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cardio Type Selection
            item {
                CardioTypeSection(
                    selectedType = uiState.selectedCardioType,
                    onTypeSelected = viewModel::onCardioTypeSelected
                )
            }

            // Duration Input
            item {
                DurationSection(
                    durationMinutes = uiState.durationMinutes,
                    onDurationChanged = viewModel::onDurationChanged
                )
            }

            // Distance Input (Optional)
            item {
                DistanceSection(
                    distance = uiState.distance,
                    onDistanceChanged = viewModel::onDistanceChanged
                )
            }

            // RPE Selector
            item {
                RpeSection(
                    rpe = uiState.rpe,
                    onRpeChanged = viewModel::onRpeChanged
                )
            }

            // Notes Input
            item {
                NotesSection(
                    notes = uiState.notes,
                    onNotesChanged = viewModel::onNotesChanged
                )
            }

            // Save Button
            item {
                Button(
                    onClick = viewModel::saveCardioSession,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Log Cardio Session",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Recent Sessions Section
            if (uiState.recentSessions.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Recent Sessions",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(uiState.recentSessions) { sessionWithDate ->
                    RecentSessionCard(
                        sessionWithDate = sessionWithDate,
                        onClick = { viewModel.quickLogSession(sessionWithDate.session) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CardioTypeSection(
    selectedType: CardioType,
    onTypeSelected: (CardioType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Activity Type",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(CardioType.entries.toList()) { type ->
                CardioTypeChip(
                    type = type,
                    isSelected = type == selectedType,
                    onClick = { onTypeSelected(type) }
                )
            }
        }
    }
}

@Composable
private fun CardioTypeChip(
    type: CardioType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = getCardioTypeIcon(type),
            contentDescription = type.displayName,
            tint = contentColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun getCardioTypeIcon(type: CardioType): ImageVector {
    return when (type) {
        CardioType.RUNNING -> Icons.AutoMirrored.Filled.DirectionsRun
        CardioType.WALKING -> Icons.AutoMirrored.Filled.DirectionsWalk
        CardioType.CYCLING -> Icons.AutoMirrored.Filled.DirectionsBike
        CardioType.SWIMMING -> Icons.Default.Pool
        CardioType.ROWING -> Icons.Default.Rowing
        CardioType.ELLIPTICAL -> Icons.Default.FitnessCenter
        CardioType.STAIR_CLIMBER -> Icons.Default.Stairs
        CardioType.TREADMILL -> Icons.AutoMirrored.Filled.DirectionsRun
        CardioType.JUMP_ROPE -> Icons.Default.SportsGymnastics
        CardioType.OTHER -> Icons.Default.SportsScore
    }
}

@Composable
private fun DurationSection(
    durationMinutes: Int,
    onDurationChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Duration",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Decrease button
                FilledIconButton(
                    onClick = { onDurationChanged(durationMinutes - 5) },
                    enabled = durationMinutes > 5,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease duration"
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Duration display
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "$durationMinutes",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "minutes",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(24.dp))

                // Increase button
                FilledIconButton(
                    onClick = { onDurationChanged(durationMinutes + 5) },
                    enabled = durationMinutes < 300,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase duration"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick duration chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(15, 30, 45, 60).forEach { minutes ->
                    FilterChip(
                        selected = durationMinutes == minutes,
                        onClick = { onDurationChanged(minutes) },
                        label = { Text("${minutes}m") }
                    )
                }
            }
        }
    }
}

@Composable
private fun DistanceSection(
    distance: String,
    onDistanceChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Distance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "(Optional)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = distance,
                onValueChange = onDistanceChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Enter distance") },
                suffix = { Text("km") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
private fun RpeSection(
    rpe: Int,
    onRpeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RPE (Rate of Perceived Exertion)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = getRpeDescription(rpe),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            // RPE Slider
            Slider(
                value = rpe.toFloat(),
                onValueChange = { onRpeChanged(it.toInt()) },
                valueRange = 1f..10f,
                steps = 8,
                modifier = Modifier.fillMaxWidth()
            )

            // RPE Numbers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..10).forEach { value ->
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (value == rpe) {
                                    getRpeColor(value)
                                } else {
                                    MaterialTheme.colorScheme.surface
                                }
                            )
                            .clickable { onRpeChanged(value) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = value.toString(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (value == rpe) FontWeight.Bold else FontWeight.Normal,
                            color = if (value == rpe) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun getRpeColor(rpe: Int): androidx.compose.ui.graphics.Color {
    return when {
        rpe <= 3 -> MaterialTheme.colorScheme.tertiary
        rpe <= 6 -> MaterialTheme.colorScheme.secondary
        rpe <= 8 -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.error
    }
}

private fun getRpeDescription(rpe: Int): String {
    return when (rpe) {
        1, 2 -> "Very light - Could do this all day"
        3, 4 -> "Light - Comfortable pace"
        5, 6 -> "Moderate - Challenging but sustainable"
        7, 8 -> "Hard - Difficult to maintain"
        9, 10 -> "Maximum effort - All out"
        else -> ""
    }
}

@Composable
private fun NotesSection(
    notes: String,
    onNotesChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Notes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = notes,
            onValueChange = onNotesChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Add any notes about your session...") },
            minLines = 2,
            maxLines = 4,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun RecentSessionCard(
    sessionWithDate: CardioSessionWithDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val session = sessionWithDate.session
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM d, yyyy") }
    val formattedDate = remember(sessionWithDate.workoutDate) {
        Instant.ofEpochMilli(sessionWithDate.workoutDate)
            .atZone(ZoneId.systemDefault())
            .format(dateFormatter)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = getCardioTypeIcon(session.type),
                    contentDescription = session.type.displayName,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = session.type.displayName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${session.durationMinutes} min",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                session.distance?.let { distance ->
                    Text(
                        text = "${"%.1f".format(distance)} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
