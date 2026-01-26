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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
            if (workoutCounts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
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
            val primaryLight = primaryColor.copy(alpha = 0.6f)
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

            // Chart area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val barCount = workoutCounts.size
                    val chartWidth = size.width
                    val chartHeight = size.height - 24.dp.toPx()

                    val barWidthRatio = if (isDaily) 0.7f else 0.6f
                    val totalBarSpace = chartWidth / barCount
                    val barWidth = totalBarSpace * barWidthRatio
                    val barSpacing = (totalBarSpace - barWidth) / 2

                    workoutCounts.forEachIndexed { index, point ->
                        val barHeight = if (point.count > 0) {
                            (point.count.toFloat() / maxCount) * chartHeight * 0.85f
                        } else {
                            4.dp.toPx() // Minimum bar height for days with 0 workouts
                        }
                        val x = index * totalBarSpace + barSpacing
                        val y = chartHeight - barHeight

                        val gradient = if (point.count > 0) {
                            Brush.verticalGradient(
                                colors = listOf(primaryColor, primaryLight),
                                startY = y,
                                endY = chartHeight
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(
                                    primaryColor.copy(alpha = 0.2f),
                                    primaryColor.copy(alpha = 0.1f)
                                ),
                                startY = y,
                                endY = chartHeight
                            )
                        }

                        drawRoundRect(
                            brush = gradient,
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
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
                val step = when {
                    workoutCounts.size > 20 -> 5
                    workoutCounts.size > 10 -> 2
                    else -> 1
                }

                workoutCounts.forEachIndexed { index, point ->
                     Box(
                         modifier = Modifier.weight(1f),
                         contentAlignment = Alignment.Center
                     ) {
                         val showLabel = when {
                             workoutCounts.size > 20 -> index % 5 == 0 || index == workoutCounts.lastIndex
                             workoutCounts.size > 10 -> index % 2 == 0
                             else -> true
                         }
                         
                         if (showLabel) {
                             Text(
                                text = dateFormat.format(Date(point.periodStart)),
                                style = MaterialTheme.typography.labelSmall,
                                color = onSurfaceVariant,
                                fontSize = if (isDaily && workoutCounts.size > 7) 9.sp else 11.sp,
                                textAlign = TextAlign.Center,
                                maxLines = 1
                            )
                         }
                     }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Summary row
            val totalWorkouts = workoutCounts.sumOf { it.count }
            val daysWithWorkouts = workoutCounts.count { it.count > 0 }
            val avgPerPeriod = if (workoutCounts.isNotEmpty()) totalWorkouts.toFloat() / workoutCounts.size else 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Total",
                    value = "$totalWorkouts",
                    color = primaryColor
                )
                StatItem(
                    label = if (isDaily) "Active Days" else "Avg/Month",
                    value = if (isDaily) "$daysWithWorkouts" else String.format(Locale.US, "%.1f", avgPerPeriod),
                    color = primaryColor
                )
                StatItem(
                    label = "Best",
                    value = "${workoutCounts.maxOfOrNull { it.count } ?: 0}",
                    color = primaryColor
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
