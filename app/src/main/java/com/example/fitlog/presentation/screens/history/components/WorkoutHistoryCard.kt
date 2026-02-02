package com.example.fitlog.presentation.screens.history.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.fitlog.domain.model.Workout
import com.example.fitlog.ui.theme.CardDimens
import com.example.fitlog.ui.theme.FitLogCardDefaults
import com.example.fitlog.ui.theme.FitLogChipDefaults
import com.example.fitlog.ui.theme.FitLogShapes
import com.example.fitlog.ui.theme.IconSize
import com.example.fitlog.ui.theme.Spacing
import java.text.SimpleDateFormat
import java.util.*

/**
 * Card component displaying a workout summary in the history list
 */
@Composable
fun WorkoutHistoryCard(
    workout: Workout,
    routineName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = FitLogShapes.card,
        colors = FitLogCardDefaults.colors(),
        elevation = FitLogCardDefaults.elevation()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardDimens.paddingStandard)
        ) {
            // Top row: Date and workout type indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Date
                Text(
                    text = formatWorkoutDate(workout.startTime),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Workout type indicator (Cardio vs Strength)
                WorkoutTypeChip(isCardio = workout.isCardio)
            }

            Spacer(modifier = Modifier.height(Spacing.sm))

            // Routine name
            Text(
                text = routineName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
            ) {
                // Duration
                workout.durationMinutes?.let { duration ->
                    StatItem(
                        icon = Icons.Default.Timer,
                        value = "${duration}m",
                        label = "Duration"
                    )
                }

                // Volume (only for strength workouts)
                if (!workout.isCardio && workout.totalVolume > 0) {
                    StatItem(
                        icon = Icons.Default.FitnessCenter,
                        value = formatVolume(workout.totalVolume),
                        label = "Volume"
                    )
                }

                // Set count (only for strength workouts)
                if (!workout.isCardio && workout.completedSets > 0) {
                    StatItem(
                        icon = Icons.Default.CheckCircle,
                        value = "${workout.completedSets}",
                        label = "Sets"
                    )
                }

                // PR count (if any)
                if (workout.prCount > 0) {
                    StatItem(
                        icon = Icons.Default.EmojiEvents,
                        value = "${workout.prCount}",
                        label = "PRs",
                        highlight = true
                    )
                }
            }

            // Notes preview (if any)
            workout.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                Spacer(modifier = Modifier.height(Spacing.sm))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun WorkoutTypeChip(
    isCardio: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isCardio) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    val contentColor = if (isCardio) {
        MaterialTheme.colorScheme.onTertiaryContainer
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    val icon = if (isCardio) Icons.AutoMirrored.Filled.DirectionsRun else Icons.Default.FitnessCenter
    val label = if (isCardio) "Cardio" else "Strength"

    Surface(
        modifier = modifier,
        shape = FitLogShapes.chip,
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(FitLogChipDefaults.padding),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(IconSize.tiny),
                tint = contentColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    highlight: Boolean = false,
    modifier: Modifier = Modifier
) {
    val iconColor = if (highlight) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(IconSize.small),
            tint = iconColor
        )
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (highlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Format workout date for display
 * Shows "Today", "Yesterday", or day name for recent dates
 * Shows full date for older workouts
 */
private fun formatWorkoutDate(timestamp: Long): String {
    val workoutDate = Calendar.getInstance().apply { timeInMillis = timestamp }
    val today = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

    return when {
        isSameDay(workoutDate, today) -> "Today"
        isSameDay(workoutDate, yesterday) -> "Yesterday"
        isWithinLastWeek(workoutDate, today) -> {
            SimpleDateFormat("EEEE", Locale.getDefault()).format(Date(timestamp))
        }
        else -> {
            SimpleDateFormat("EEE, MMM d", Locale.getDefault()).format(Date(timestamp))
        }
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

private fun isWithinLastWeek(date: Calendar, today: Calendar): Boolean {
    val weekAgo = Calendar.getInstance().apply {
        timeInMillis = today.timeInMillis
        add(Calendar.DAY_OF_YEAR, -7)
    }
    return date.after(weekAgo)
}

/**
 * Format volume for display (e.g., "12,500 kg" or "1.2k kg")
 */
private fun formatVolume(volume: Float): String {
    return when {
        volume >= 10000 -> String.format("%.1fk", volume / 1000)
        volume >= 1000 -> String.format("%,.0f", volume)
        else -> String.format("%.0f", volume)
    }
}
