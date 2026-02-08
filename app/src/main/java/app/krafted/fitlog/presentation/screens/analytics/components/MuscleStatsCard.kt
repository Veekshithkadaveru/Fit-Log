package app.krafted.fitlog.presentation.screens.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.krafted.fitlog.domain.model.MuscleGroupStats
import app.krafted.fitlog.domain.model.WeightUnit

@Composable
fun MuscleStatsCard(
    stats: MuscleGroupStats,
    weightUnit: WeightUnit = WeightUnit.KG,
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Muscle Group Name
            Text(
                text = stats.muscleGroup.displayName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )

            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Weekly Freq",
                    value = "${stats.weeklyFrequency}x",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Total Volume",
                    value = "${weightUnit.fromKg(stats.totalVolume).toInt()} ${weightUnit.label}",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Total Sets",
                    value = "${stats.totalSets}",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Exercises",
                    value = "${stats.exerciseCount}",
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Avg Volume/Workout",
                    value = "${weightUnit.fromKg(stats.averageVolumePerWorkout).toInt()} ${weightUnit.label}",
                    modifier = Modifier.weight(1f)
                )
                StatItem(
                    label = "Last Trained",
                    value = stats.daysSinceLastWorked?.let { "$it days ago" } ?: "Never",
                    modifier = Modifier.weight(1f)
                )
            }

            // Volume per Set indicator
            LinearProgressIndicator(
                progress = { (stats.volumePerSet / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                text = "Avg Volume/Set: ${weightUnit.fromKg(stats.volumePerSet).toInt()} ${weightUnit.label}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
