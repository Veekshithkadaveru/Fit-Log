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
import com.example.fitlog.domain.model.WeightProgressPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExerciseWeightChart(
    weightProgress: List<WeightProgressPoint>,
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
            if (weightProgress.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No weight data yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                return@Card
            }

            val primaryColor = MaterialTheme.colorScheme.primary
            val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

            val sortedData = remember(weightProgress) {
                weightProgress.sortedBy { it.date }
            }

            val minWeight = remember(sortedData) {
                (sortedData.minOfOrNull { it.maxWeight } ?: 0f) * 0.9f
            }
            val maxWeight = remember(sortedData) {
                (sortedData.maxOfOrNull { it.maxWeight } ?: 1f) * 1.1f
            }
            val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)

            val dateFormat = remember { SimpleDateFormat("MM/dd", Locale.getDefault()) }



            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (sortedData.size < 2) {


                        if (sortedData.size == 1) {
                            val point = sortedData.first()
                            val x = size.width / 2
                            val y = size.height / 2
                            drawCircle(
                                color = Color.White,
                                radius = 8.dp.toPx(),
                                center = Offset(x, y)
                            )
                            drawCircle(
                                color = primaryColor,
                                radius = 6.dp.toPx(),
                                center = Offset(x, y)
                            )
                        }
                        return@Canvas
                    }

                    val chartWidth = size.width
                    val chartHeight = size.height - 8.dp.toPx()
                    val paddingTop = 8.dp.toPx()

                    val points = sortedData.mapIndexed { index, point ->
                        val x = (index.toFloat() / (sortedData.size - 1)) * chartWidth
                        val normalizedY = (point.maxWeight - minWeight) / weightRange
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
                            primaryColor.copy(alpha = 0.3f),
                            primaryColor.copy(alpha = 0.05f)
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
                        color = primaryColor,
                        style = Stroke(
                            width = 3.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    points.forEach { point ->
                        drawCircle(
                            color = Color.White,
                            radius = 6.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = primaryColor,
                            radius = 4.dp.toPx(),
                            center = point
                        )
                    }
                }
            }



            if (sortedData.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = dateFormat.format(Date(sortedData.first().date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant,
                        fontSize = 10.sp
                    )
                    if (sortedData.size > 2) {
                        Text(
                            text = dateFormat.format(Date(sortedData[sortedData.size / 2].date)),
                            style = MaterialTheme.typography.labelSmall,
                            color = onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = dateFormat.format(Date(sortedData.last().date)),
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            val latestWeight = sortedData.lastOrNull()?.maxWeight ?: 0f
            val firstWeight = sortedData.firstOrNull()?.maxWeight ?: 0f
            val change = latestWeight - firstWeight
            val peakWeight = sortedData.maxOfOrNull { it.maxWeight } ?: 0f

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    label = "Current",
                    value = "${latestWeight.toInt()} lbs",
                    color = primaryColor
                )
                StatItem(
                    label = "Change",
                    value = String.format(Locale.US, "%+.0f lbs", change),
                    color = if (change >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
                StatItem(
                    label = "Peak",
                    value = "${peakWeight.toInt()} lbs",
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
