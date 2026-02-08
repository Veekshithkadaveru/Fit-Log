package app.krafted.fitlog.presentation.screens.workout.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.krafted.fitlog.domain.model.PRCheckResult
import app.krafted.fitlog.presentation.viewmodel.PREvent
import kotlin.random.Random

// PR Gold color palette
private val PRGold = Color(0xFFFFD700)
private val PRGoldDark = Color(0xFFB8860B)
private val PRGoldLight = Color(0xFFFFF4CC)

/**
 * Full-screen PR celebration dialog with animations
 */
@Composable
fun PRCelebrationDialog(
    prEvent: PREvent,
    onDismiss: () -> Unit,
    weightUnitLabel: String = "kg"
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            // Floating stars/confetti effect
            ConfettiEffect()

            // Main celebration card
            PRCelebrationCard(
                prEvent = prEvent,
                onDismiss = onDismiss,
                weightUnitLabel = weightUnitLabel
            )
        }
    }
}

@Composable
private fun ConfettiEffect() {
    val infiniteTransition = rememberInfiniteTransition(label = "confetti")

    // Create multiple floating stars
    repeat(8) { index ->
        val offsetX = remember { Random.nextFloat() * 300 - 150 }
        val initialY = remember { Random.nextFloat() * 200 - 400 }
        val delay = remember { index * 100 }
        val size = remember { 12 + Random.nextInt(12) }

        val yOffset by infiniteTransition.animateFloat(
            initialValue = initialY,
            targetValue = initialY + 600,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = 2000 + Random.nextInt(1000),
                    delayMillis = delay,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            ),
            label = "star_y_$index"
        )

        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1500),
                repeatMode = RepeatMode.Restart
            ),
            label = "star_rotation_$index"
        )

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier
                .offset(x = offsetX.dp, y = yOffset.dp)
                .size(size.dp)
                .rotate(rotation),
            tint = if (index % 2 == 0) PRGold else PRGoldLight
        )
    }
}

@Composable
private fun PRCelebrationCard(
    prEvent: PREvent,
    onDismiss: () -> Unit,
    weightUnitLabel: String = "kg"
) {
    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .scale(scale.value),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(PRGold, PRGoldDark)
                        )
                    )
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Animated trophy icon
                    val infiniteTransition = rememberInfiniteTransition(label = "trophy")
                    val trophyScale by infiniteTransition.animateFloat(
                        initialValue = 1f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(500),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "trophy_scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .scale(trophyScale)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Trophy",
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "NEW PERSONAL RECORD!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = prEvent.exerciseName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatBox(
                        value = "${prEvent.weight.toInt()}",
                        label = weightUnitLabel,
                        icon = Icons.Default.FitnessCenter
                    )

                    Text(
                        text = "×",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    StatBox(
                        value = "${prEvent.reps}",
                        label = "reps",
                        icon = Icons.AutoMirrored.Filled.TrendingUp
                    )
                }

                PRTypeBadges(prResult = prEvent.prResult)

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PRGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "KEEP CRUSHING IT!",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatBox(
    value: String,
    label: String,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = PRGold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = PRGold
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PRTypeBadges(prResult: PRCheckResult) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (prResult.isNewWeightPR) {
            PRTypeBadge(text = "Max Weight")
        }
        if (prResult.isNewRepsPR) {
            if (prResult.isNewWeightPR) Spacer(modifier = Modifier.width(8.dp))
            PRTypeBadge(text = "Max Reps")
        }
        if (prResult.isNewVolumePR) {
            if (prResult.isNewWeightPR || prResult.isNewRepsPR) Spacer(modifier = Modifier.width(8.dp))
            PRTypeBadge(text = "Max Volume")
        }
    }
}

@Composable
private fun PRTypeBadge(text: String) {
    Surface(
        color = PRGoldLight,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = PRGoldDark,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

/**
 * Compact PR indicator shown in header
 */
@Composable
fun PRCountBadge(
    count: Int,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = count > 0,
        enter = fadeIn() + scaleIn(),
        exit = fadeOut() + scaleOut()
    ) {
        Surface(
            modifier = modifier,
            color = PRGold,
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "PR Count",
                    modifier = Modifier.size(16.dp),
                    tint = Color.White
                )
                Text(
                    text = "$count PR${if (count > 1) "s" else ""}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Toast-style PR notification shown during workout
 */
@Composable
fun PRToastNotification(
    prEvent: PREvent,
    onDismiss: () -> Unit,
    weightUnitLabel: String = "kg",
    modifier: Modifier = Modifier
) {
    // Auto-dismiss after 3 seconds
    LaunchedEffect(prEvent) {
        kotlinx.coroutines.delay(3000)
        onDismiss()
    }

    val scale = remember { Animatable(0.8f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .scale(scale.value),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = PRGold
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Trophy icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "PR",
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            // PR Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "NEW ${prEvent.repRangeDisplayName} PR!",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    // Show 1RM badge if it's also a new estimated 1RM
                    if (prEvent.isNew1RMPR && prEvent.repRangeDisplayName != "1RM") {
                        Surface(
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "NEW 1RM",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "${prEvent.exerciseName}: ${prEvent.weight.toInt()} $weightUnitLabel × ${prEvent.reps}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                // Show estimated 1RM if available
                if (prEvent.estimated1RM > 0) {
                    Text(
                        text = "Est. 1RM: ${prEvent.estimated1RM.toInt()} $weightUnitLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Close button
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = Color.White
                )
            }
        }
    }
}

/**
 * Workout summary dialog shown after workout ends with all PRs
 */
@Composable
fun WorkoutSummaryDialog(
    sessionPRs: List<PREvent>,
    workoutDuration: Long,
    totalSets: Int,
    onDismiss: () -> Unit,
    weightUnitLabel: String = "kg"
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            // Confetti for PRs
            if (sessionPRs.isNotEmpty()) {
                ConfettiEffect()
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 700.dp), // Limit height to ensure scrolling checks in on smaller screens
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (sessionPRs.isNotEmpty()) {
                                    Brush.verticalGradient(colors = listOf(PRGold, PRGoldDark))
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            MaterialTheme.colorScheme.primary,
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                                        )
                                    )
                                }
                            )
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = if (sessionPRs.isNotEmpty())
                                    Icons.Default.EmojiEvents
                                else
                                    Icons.Default.FitnessCenter,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color.White
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (sessionPRs.isNotEmpty())
                                    "WORKOUT COMPLETE!"
                                else
                                    "GREAT WORKOUT!",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            if (sessionPRs.isNotEmpty()) {
                                Text(
                                    text = "${sessionPRs.size} Personal Record${if (sessionPRs.size > 1) "s" else ""}!",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                        }
                    }

// Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Workout stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            WorkoutStatItem(
                                value = formatDuration(workoutDuration),
                                label = "Duration"
                            )
                            WorkoutStatItem(
                                value = "$totalSets",
                                label = "Sets"
                            )
                            WorkoutStatItem(
                                value = "${sessionPRs.size}",
                                label = "PRs"
                            )
                        }

                        // PR List (if any)
                        if (sessionPRs.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Your New Records",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sessionPRs.forEach { pr ->
                                    PRSummaryItem(prEvent = pr, weightUnitLabel = weightUnitLabel)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Dismiss button
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (sessionPRs.isNotEmpty()) PRGold else MaterialTheme.colorScheme.primary,
                                contentColor = if (sessionPRs.isNotEmpty()) Color.Black else Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "DONE",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutStatItem(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PRSummaryItem(prEvent: PREvent, weightUnitLabel: String = "kg") {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = PRGoldLight,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = PRGoldDark
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = prEvent.exerciseName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = PRGoldDark
                    )
                    // Rep range badge
                    Surface(
                        color = PRGoldDark.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = prEvent.repRangeDisplayName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PRGoldDark,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Text(
                    text = "${prEvent.weight.toInt()} $weightUnitLabel × ${prEvent.reps} reps",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PRGoldDark.copy(alpha = 0.8f)
                )
                // Show estimated 1RM if it's also a new 1RM PR
                if (prEvent.isNew1RMPR && prEvent.estimated1RM > 0) {
                    Text(
                        text = "New Est. 1RM: ${prEvent.estimated1RM.toInt()} $weightUnitLabel",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = PRGoldDark.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val minutes = (millis / 1000 / 60).toInt()
    val seconds = ((millis / 1000) % 60).toInt()
    return if (minutes > 0) "${minutes}m ${seconds}s" else "${seconds}s"
}
