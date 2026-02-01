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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.domain.model.Bodyweight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BodyweightTrendChart(
    entries: List<Bodyweight>,
    modifier: Modifier = Modifier
) {
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
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No bodyweight data yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Card
            }

            val tertiaryColor = MaterialTheme.colorScheme.tertiary
            val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

            val sortedEntries = remember(entries) {
                entries.sortedBy { it.date }
            }

            val minWeight = remember(sortedEntries) {
                (sortedEntries.minOfOrNull { it.weight } ?: 0f) - 2f
            }
            val maxWeight = remember(sortedEntries) {
                (sortedEntries.maxOfOrNull { it.weight } ?: 1f) + 2f
            }
            val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)

            val dateFormat = remember { SimpleDateFormat("MM/dd", Locale.getDefault()) }


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val chartWidth = size.width
                    val chartHeight = size.height - 8.dp.toPx()
                    val paddingTop = 8.dp.toPx()

                    // Handle single entry case - show a single point in the center
                    if (sortedEntries.size == 1) {
                        val centerX = chartWidth / 2
                        val centerY = chartHeight / 2
                        drawCircle(
                            color = Color.White,
                            radius = 8.dp.toPx(),
                            center = Offset(centerX, centerY)
                        )
                        drawCircle(
                            color = tertiaryColor,
                            radius = 6.dp.toPx(),
                            center = Offset(centerX, centerY)
                        )
                        return@Canvas
                    }

                    if (sortedEntries.size < 2) return@Canvas

                    val points = sortedEntries.mapIndexed { index, entry ->
                        val x = (index.toFloat() / (sortedEntries.size - 1)) * chartWidth
                        val normalizedY = (entry.weight - minWeight) / weightRange
                        val y = paddingTop + chartHeight * (1 - normalizedY) * 0.9f
                        Offset(x, y)
                    }


                    val fillPath = Path().apply {
                        moveTo(points.first().x, chartHeight)
                        points.forEach { point ->
                            lineTo(point.x, point.y)
                        }
                        lineTo(points.last().x, chartHeight)
                        close()
                    }

                    val gradient = Brush.verticalGradient(
                        colors = listOf(
                            tertiaryColor.copy(alpha = 0.3f),
                            tertiaryColor.copy(alpha = 0.05f)
                        ),
                        startY = 0f,
                        endY = chartHeight
                    )
                    drawPath(fillPath, gradient)


                    val linePath = Path().apply {
                        points.forEachIndexed { index, point ->
                            if (index == 0) {
                                moveTo(point.x, point.y)
                            } else {
                                lineTo(point.x, point.y)
                            }
                        }
                    }
                    drawPath(
                        linePath,
                        color = tertiaryColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )


                    points.forEach { point ->
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = tertiaryColor,
                            radius = 4.dp.toPx(),
                            center = point
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Date labels (show first, middle, last)
            if (sortedEntries.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dateFormat.format(Date(sortedEntries.first().date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    if (sortedEntries.size > 2) {
                        Text(
                            text = dateFormat.format(Date(sortedEntries[sortedEntries.size / 2].date)),
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = dateFormat.format(Date(sortedEntries.last().date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            val latestWeight = sortedEntries.lastOrNull()?.weight ?: 0f
            val firstWeight = sortedEntries.firstOrNull()?.weight ?: 0f
            val change = latestWeight - firstWeight

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Current",
                    value = String.format(Locale.US, "%.1f", latestWeight),
                    color = tertiaryColor
                )
                StatItem(
                    label = "Change",
                    value = String.format(Locale.US, "%+.1f", change),
                    color = if (change >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                StatItem(
                    label = "Lowest",
                    value = String.format(Locale.US, "%.1f", sortedEntries.minOfOrNull { it.weight } ?: 0f),
                    color = tertiaryColor
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
