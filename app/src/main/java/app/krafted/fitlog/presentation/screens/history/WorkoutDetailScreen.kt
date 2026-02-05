package app.krafted.fitlog.presentation.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.krafted.fitlog.domain.model.Workout
import app.krafted.fitlog.presentation.screens.history.components.ExerciseSetsCard
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: WorkoutDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is WorkoutDetailEvent.WorkoutDeleted -> onNavigateBack()
                is WorkoutDetailEvent.SaveSuccess -> { /* Show success toast if needed */ }
                is WorkoutDetailEvent.SaveError -> { /* Show error toast if needed */ }
            }
        }
    }

    // Delete confirmation dialog
    if (uiState.showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = { viewModel.deleteWorkout() },
            onDismiss = { viewModel.hideDeleteConfirmation() }
        )
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            WorkoutDetailTopBar(
                isEditMode = uiState.isEditMode,
                isSaving = uiState.isSaving,
                onNavigateBack = {
                    if (uiState.isEditMode) {
                        viewModel.cancelEditMode()
                    } else {
                        onNavigateBack()
                    }
                },
                onEditClick = { viewModel.enterEditMode() },
                onSaveClick = { viewModel.saveChanges() },
                onDeleteClick = { viewModel.showDeleteConfirmation() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading || uiState.isDeleting -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.error != null -> {
                    ErrorState(
                        message = uiState.error!!,
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.workout != null -> {
                    WorkoutDetailContent(
                        uiState = uiState,
                        viewModel = viewModel,
                        workoutTypeName = viewModel.getWorkoutTypeName()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkoutDetailTopBar(
    isEditMode: Boolean,
    isSaving: Boolean,
    onNavigateBack: () -> Unit,
    onEditClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (isEditMode) "Edit Workout" else "Workout Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = if (isEditMode) "Cancel" else "Back"
                )
            }
        },
        actions = {
            if (isEditMode) {
                // Save button in edit mode
                IconButton(
                    onClick = onSaveClick,
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Changes",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            } else {
                // Edit button
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Workout"
                    )
                }
                // Delete button
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Workout",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Delete Workout?",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "This will permanently delete this workout and all its data. This action cannot be undone.",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun WorkoutDetailContent(
    uiState: WorkoutDetailUiState,
    viewModel: WorkoutDetailViewModel,
    workoutTypeName: String,
    modifier: Modifier = Modifier
) {
    val workout = uiState.workout ?: return

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            WorkoutSummaryCard(
                workout = workout,
                workoutTypeName = workoutTypeName
            )
        }

        item {
            WorkoutStatsCard(workout = workout)
        }

        if (uiState.exerciseGroups.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Exercises",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    if (uiState.isEditMode) {
                        Text(
                            text = "Tap values to edit",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }

            items(
                items = uiState.exerciseGroups,
                key = { it.exercise.id }
            ) { exerciseWithSets ->
                ExerciseSetsCard(
                    exerciseWithSets = exerciseWithSets,
                    isEditMode = uiState.isEditMode,
                    getWeight = { setId -> viewModel.getSetWeight(setId) },
                    getReps = { setId -> viewModel.getSetReps(setId) },
                    onWeightChange = { setId, weight -> viewModel.updateSetWeight(setId, weight) },
                    onRepsChange = { setId, reps -> viewModel.updateSetReps(setId, reps) }
                )
            }
        }

        if (!workout.notes.isNullOrBlank() && !uiState.isEditMode) {
            item {
                NotesCard(notes = workout.notes!!)
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun WorkoutSummaryCard(
    workout: Workout,
    workoutTypeName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (workout.isCardio) {
                MaterialTheme.colorScheme.tertiaryContainer
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (workout.isCardio) {
                        Icons.AutoMirrored.Filled.DirectionsRun
                    } else {
                        Icons.Default.FitnessCenter
                    },
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = if (workout.isCardio) {
                        MaterialTheme.colorScheme.onTertiaryContainer
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    }
                )

                Column {
                    Text(
                        text = workoutTypeName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (workout.isCardio) {
                            MaterialTheme.colorScheme.onTertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        }
                    )

                    Text(
                        text = formatFullDate(workout.startTime),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (workout.isCardio) {
                            MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (workout.isCardio) {
                        MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    }
                )
                Text(
                    text = formatTimeRange(workout.startTime, workout.endTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (workout.isCardio) {
                        MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    }
                )
            }
        }
    }
}

@Composable
private fun WorkoutStatsCard(
    workout: Workout,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            workout.durationMinutes?.let { duration ->
                StatColumn(
                    icon = Icons.Default.Timer,
                    value = "${duration}m",
                    label = "Duration"
                )
            }

            if (!workout.isCardio && workout.totalVolume > 0) {
                StatColumn(
                    icon = Icons.Default.FitnessCenter,
                    value = formatVolume(workout.totalVolume),
                    label = "Volume"
                )
            }

            if (!workout.isCardio && workout.completedSets > 0) {
                StatColumn(
                    icon = Icons.Default.CheckCircle,
                    value = "${workout.completedSets}",
                    label = "Sets"
                )
            }

            if (workout.exerciseCount > 0) {
                StatColumn(
                    icon = Icons.AutoMirrored.Filled.List,
                    value = "${workout.exerciseCount}",
                    label = "Exercises"
                )
            }

            if (workout.prCount > 0) {
                StatColumn(
                    icon = Icons.Default.EmojiEvents,
                    value = "${workout.prCount}",
                    label = "PRs",
                    highlight = true
                )
            }
        }
    }
}

@Composable
private fun StatColumn(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (highlight) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (highlight) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun NotesCard(
    notes: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Notes",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(onClick = onRetry) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Retry")
        }
    }
}

// Utility functions

private fun formatFullDate(timestamp: Long): String {
    val format = SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault())
    return format.format(Date(timestamp))
}

private fun formatTimeRange(startTime: Long, endTime: Long?): String {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val start = timeFormat.format(Date(startTime))
    val end = endTime?.let { timeFormat.format(Date(it)) } ?: "In Progress"
    return "$start - $end"
}

private fun formatVolume(volume: Float): String {
    return when {
        volume >= 10000 -> String.format("%.1fk", volume / 1000)
        volume >= 1000 -> String.format("%,.0f", volume)
        else -> String.format("%.0f", volume)
    }
}
