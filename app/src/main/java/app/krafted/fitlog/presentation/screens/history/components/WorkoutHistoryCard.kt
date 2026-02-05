package app.krafted.fitlog.presentation.screens.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.krafted.fitlog.domain.model.Workout
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
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

            Spacer(modifier = Modifier.height(8.dp))

            // Routine name
            Text(
                text = routineName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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
                Spacer(modifier = Modifier.height(8.dp))
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
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
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
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
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
