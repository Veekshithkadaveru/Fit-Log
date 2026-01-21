package com.example.fitlog.presentation.screens.analytics.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.domain.model.WeeklyMuscleFrequency
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.lineComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.entryOf

@Composable
fun MuscleFrequencyChart(
    frequencies: List<WeeklyMuscleFrequency>,
    modifier: Modifier = Modifier
) {
    if (frequencies.isEmpty()) {
        Text(
            text = "No data available",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
        return
    }

    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    // Create chart model producer
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }

    // Update chart data when frequencies change
    LaunchedEffect(frequencies) {
        val entries = frequencies.mapIndexed { index, frequency ->
            entryOf(index.toFloat(), frequency.workoutsPerWeek.toFloat())
        }
        chartEntryModelProducer.setEntries(entries)
    }

    // Create axis value formatter for muscle names
    val bottomAxisValueFormatter = remember(frequencies) {
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            frequencies.getOrNull(value.toInt())?.muscleGroup?.displayName?.take(8) ?: ""
        }
    }

    // Create axis value formatter for frequency values
    val startAxisValueFormatter = remember {
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
            "${value.toInt()}x"
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProvideChartStyle {
            Chart(
                chart = columnChart(
                    columns = listOf(
                        lineComponent(
                            color = primaryColor,
                            thickness = 24.dp,
                            shape = Shapes.roundedCornerShape(topLeftPercent = 25, topRightPercent = 25)
                        )
                    ),
                    spacing = 8.dp,
                    mergeMode = ColumnChart.MergeMode.Grouped
                ),
                chartModelProducer = chartEntryModelProducer,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                startAxis = rememberStartAxis(
                    valueFormatter = startAxisValueFormatter,
                    label = textComponent(
                        color = onSurfaceVariant,
                        textSize = 12.sp
                    )
                ),
                bottomAxis = rememberBottomAxis(
                    valueFormatter = bottomAxisValueFormatter,
                    label = textComponent(
                        color = onSurfaceVariant,
                        textSize = 10.sp
                    ),
                    labelRotationDegrees = 45f
                )
            )
        }

        // Sets summary below chart
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            frequencies.take(4).forEach { frequency ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = frequency.muscleGroup.displayName.take(6),
                        style = MaterialTheme.typography.labelSmall,
                        color = onSurfaceVariant
                    )
                    Text(
                        text = "${frequency.setsPerWeek} sets",
                        style = MaterialTheme.typography.labelMedium,
                        color = primaryColor
                    )
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Optimal frequency: 2-3x per week",
                style = MaterialTheme.typography.bodySmall,
                color = onSurfaceVariant,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
