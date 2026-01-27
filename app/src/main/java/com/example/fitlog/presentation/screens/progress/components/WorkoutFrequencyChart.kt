package com.example.fitlog.presentation.screens.progress.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.domain.model.DateRangeFilter
import com.example.fitlog.domain.model.WorkoutCountPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WorkoutFrequencyChart(
    workoutCounts: List<WorkoutCountPoint>,
    dateRangeFilter: DateRangeFilter,
    modifier: Modifier = Modifier
) {
    val isDaily = dateRangeFilter == DateRangeFilter.LAST_7_DAYS ||
            dateRangeFilter == DateRangeFilter.LAST_30_DAYS

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "Workout Frequency",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (workoutCounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No workout data yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Card
            }

            val primaryColor = MaterialTheme.colorScheme.primary
            val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

            val maxCount = remember(workoutCounts) {
                (workoutCounts.maxOfOrNull { it.count } ?: 1).coerceAtLeast(1)
            }

            val dateFormat = remember(isDaily) {
                if (isDaily) {
                    SimpleDateFormat("EEE", Locale.getDefault())
                } else {
                    SimpleDateFormat("MMM", Locale.getDefault())
                }
            }

            // Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val barCount = workoutCounts.size
                    if (barCount == 0) return@Canvas

                    val spacing = 8.dp.toPx()
                    val totalSpacing = spacing * (barCount - 1)
                    val availableWidth = size.width - totalSpacing
                    val barWidth = (availableWidth / barCount).coerceAtMost(40.dp.toPx())
                    val actualTotalWidth = (barWidth * barCount) + totalSpacing
                    val startX = (size.width - actualTotalWidth) / 2

                    workoutCounts.forEachIndexed { index, point ->
                        val barHeight = if (maxCount > 0) {
                            (point.count.toFloat() / maxCount) * (size.height * 0.85f)
                        } else 0f

                        val x = startX + index * (barWidth + spacing)
                        val y = size.height - barHeight

                        // Draw bar with gradient
                        val gradient = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor,
                                primaryColor.copy(alpha = 0.6f)
                            ),
                            startY = y,
                            endY = size.height
                        )

                        drawRoundRect(
                            brush = gradient,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight.coerceAtLeast(4.dp.toPx())),
                            cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val labelsToShow = when {
                    workoutCounts.size <= 7 -> workoutCounts.indices.toList()
                    dateRangeFilter == DateRangeFilter.LAST_30_DAYS -> {
                        // Show every 5th label for 30 days
                        workoutCounts.indices.filter { it % 5 == 0 || it == workoutCounts.lastIndex }
                    }
                    else -> {
                        // For monthly view, show all
                        workoutCounts.indices.toList()
                    }
                }

                workoutCounts.forEachIndexed { index, point ->
                    if (index in labelsToShow) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dateFormat.format(Date(point.periodStart)),
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary stats
            val totalWorkouts = workoutCounts.sumOf { it.count }
            val avgPerPeriod = if (workoutCounts.isNotEmpty()) {
                totalWorkouts.toFloat() / workoutCounts.size
            } else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalWorkouts",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format(Locale.US, "%.1f", avgPerPeriod),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = if (isDaily) "Avg/Day" else "Avg/Month",
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant
                    )
                }
            }
        }
    }
}
