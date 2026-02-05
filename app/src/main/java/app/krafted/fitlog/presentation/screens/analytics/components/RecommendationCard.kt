package app.krafted.fitlog.presentation.screens.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.krafted.fitlog.domain.model.RecommendationPriority
import app.krafted.fitlog.domain.model.RecommendationType
import app.krafted.fitlog.domain.model.TrainingRecommendation

@Composable
fun RecommendationCard(
    recommendation: TrainingRecommendation,
    modifier: Modifier = Modifier
) {
    val priorityColor = when (recommendation.priority) {
        RecommendationPriority.HIGH -> MaterialTheme.colorScheme.error
        RecommendationPriority.MEDIUM -> MaterialTheme.colorScheme.tertiary
        RecommendationPriority.LOW -> MaterialTheme.colorScheme.primary
    }

    val icon = when (recommendation.type) {
        RecommendationType.INCREASE_FREQUENCY -> Icons.AutoMirrored.Filled.TrendingUp
        RecommendationType.INCREASE_VOLUME -> Icons.Default.AddCircle
        RecommendationType.ALLOW_RECOVERY -> Icons.Default.HourglassEmpty
        RecommendationType.MAINTAIN -> Icons.Default.CheckCircle
        RecommendationType.ADDRESS_IMBALANCE -> Icons.Default.Balance
    }

    val priorityText = when (recommendation.priority) {
        RecommendationPriority.HIGH -> "HIGH PRIORITY"
        RecommendationPriority.MEDIUM -> "MEDIUM"
        RecommendationPriority.LOW -> "LOW"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = priorityColor,
                modifier = Modifier.size(28.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Priority and Muscle Group
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = priorityColor.copy(alpha = 0.2f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = priorityText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = priorityColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Text(
                        text = recommendation.muscleGroup.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Recommendation Type
                Text(
                    text = recommendation.type.name.replace("_", " ").lowercase()
                        .split(" ")
                        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Reason
                Text(
                    text = recommendation.reason,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
