package app.krafted.fitlog.presentation.screens.workout.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.krafted.fitlog.domain.model.MuscleGroup
import app.krafted.fitlog.presentation.viewmodel.WorkoutExerciseWithDetails

/**
 * Panel showing a summary of muscles trained in the current workout
 * Displays primary muscles by default with expandable secondary muscles
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MusclesTrainedPanel(
    exercises: List<WorkoutExerciseWithDetails>,
    modifier: Modifier = Modifier
) {
    val muscleStats = remember(exercises) {
        calculateMuscleStats(exercises)
    }

    val primaryMuscles = muscleStats.filter { it.isPrimary }
    val secondaryMuscles = muscleStats.filter { !it.isPrimary }

    var showSecondary by remember { mutableStateOf(false) }

    if (muscleStats.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header row with icon and title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Muscles Trained",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${muscleStats.sumOf { it.setCount }} sets total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Primary muscles section
                if (primaryMuscles.isNotEmpty()) {
                    Text(
                        text = "Primary",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        primaryMuscles.forEach { stats ->
                            MuscleTag(
                                muscleName = stats.muscle.displayName,
                                setCount = stats.setCount,
                                isPrimary = true
                            )
                        }
                    }
                }

                // Secondary muscles section (expandable)
                if (secondaryMuscles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showSecondary = !showSecondary }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Secondary",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = " (${secondaryMuscles.size})",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = if (showSecondary)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = if (showSecondary) "Collapse" else "Expand",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }

                    AnimatedVisibility(
                        visible = showSecondary,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                secondaryMuscles.forEach { stats ->
                                    MuscleTag(
                                        muscleName = stats.muscle.displayName,
                                        setCount = stats.setCount,
                                        isPrimary = false
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MuscleTag(
    muscleName: String,
    setCount: Int,
    isPrimary: Boolean
) {
    val backgroundColor = if (isPrimary)
        MaterialTheme.colorScheme.primaryContainer
    else
        MaterialTheme.colorScheme.secondaryContainer

    val contentColor = if (isPrimary)
        MaterialTheme.colorScheme.onPrimaryContainer
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    val accentColor = if (isPrimary)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.secondary

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Text(
                text = muscleName,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor,
                fontWeight = if (isPrimary) FontWeight.SemiBold else FontWeight.Medium
            )

            Spacer(modifier = Modifier.width(6.dp))

            // Set count badge
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$setCount",
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

private data class MuscleStats(
    val muscle: MuscleGroup,
    val score: Float,
    val setCount: Int
) {
    val isPrimary: Boolean
        get() = score >= 1.0f
}

private fun calculateMuscleStats(exercises: List<WorkoutExerciseWithDetails>): List<MuscleStats> {
    val scores = mutableMapOf<MuscleGroup, Float>()
    val setCounts = mutableMapOf<MuscleGroup, Int>()

    exercises.forEach { details ->

        val completedSets = details.sets.count { it.isCompleted }
        val setsToCount =
            if (completedSets > 0) completedSets else details.sets.size.coerceAtLeast(1)

        val primary = details.exercise.primaryMuscle
        scores[primary] = (scores[primary] ?: 0f) + 1.0f
        setCounts[primary] = (setCounts[primary] ?: 0) + setsToCount

        details.exercise.secondaryMuscles.forEach { secondary ->
            scores[secondary] = (scores[secondary] ?: 0f) + 0.5f
            setCounts[secondary] = (setCounts[secondary] ?: 0) + setsToCount
        }
    }

    // Sort by score descending
    return scores.entries
        .sortedByDescending { it.value }
        .map { MuscleStats(it.key, it.value, setCounts[it.key] ?: 0) }
}
