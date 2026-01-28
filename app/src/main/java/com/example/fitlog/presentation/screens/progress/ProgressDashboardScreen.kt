package com.example.fitlog.presentation.screens.progress

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.fitlog.presentation.screens.progress.components.BodyweightTrendChart
import com.example.fitlog.presentation.screens.progress.components.DateRangeSelector
import com.example.fitlog.presentation.screens.progress.components.MetricCard
import com.example.fitlog.presentation.screens.progress.components.VolumeTrendChart
import com.example.fitlog.presentation.screens.progress.components.WorkoutFrequencyChart
import com.example.fitlog.domain.model.DateRangeFilter
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressDashboardScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh data when screen becomes visible (e.g., returning from workout)
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refresh()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = "Progress",
                        fontWeight = FontWeight.Bold
                    )
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> {
                ProgressDashboardContent(
                    uiState = uiState,
                    onDateRangeSelected = viewModel::selectDateRange,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun ProgressDashboardContent(
    uiState: ProgressDashboardUiState,
    onDateRangeSelected: (com.example.fitlog.domain.model.DateRangeFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val summary = uiState.progressSummary
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .animateContentSize()
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        DateRangeSelector(
            selectedFilter = uiState.selectedDateRange,
            onFilterSelected = onDateRangeSelected
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic Sections based on selection
        when (uiState.selectedDateRange) {
            DateRangeFilter.LAST_7_DAYS -> {
                // Section: This Week
                SectionHeader(title = "This Week")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Workouts",
                        value = "${summary?.workoutsThisWeek ?: 0}",
                        subtitle = "${summary?.totalWorkouts ?: 0} all-time",
                        icon = Icons.Default.FitnessCenter,
                        iconTint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Current Streak",
                        value = "${summary?.currentStreak ?: 0}",
                        subtitle = "days",
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            DateRangeFilter.LAST_YEAR -> {
                // Section: This Year
                SectionHeader(title = "This Year")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Workouts",
                        value = "${summary?.workoutsThisYear ?: 0}",
                        subtitle = "${summary?.totalWorkouts ?: 0} all-time",
                        icon = Icons.Default.FitnessCenter,
                        iconTint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "New PRs",
                        value = "${summary?.prsThisYear ?: 0}",
                        icon = Icons.Default.EmojiEvents,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            else -> {
                // Section: This Month (Default for 30 days, 3 months, 6 months)
                SectionHeader(title = "This Month")

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricCard(
                        title = "Workouts",
                        value = "${summary?.workoutsThisMonth ?: 0}",
                        icon = Icons.Default.FitnessCenter,
                        iconTint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "New PRs",
                        value = "${summary?.prsThisMonth ?: 0}",
                        icon = Icons.Default.EmojiEvents,
                        iconTint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dynamic Volume & Duration Cards
        val (volumeValue, volumeSubtitle, durationValue) = when (uiState.selectedDateRange) {
            DateRangeFilter.LAST_7_DAYS -> Triple(
                summary?.totalVolumeThisWeek ?: 0f,
                "this week",
                summary?.averageDurationThisWeek ?: 0L
            )
            DateRangeFilter.LAST_YEAR -> Triple(
                summary?.totalVolumeThisYear ?: 0f,
                "this year",
                summary?.averageDurationThisYear ?: 0L
            )
            else -> Triple(
                summary?.totalVolumeThisMonth ?: 0f,
                "this month",
                summary?.averageDurationThisMonth ?: 0L
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Volume",
                value = formatVolume(volumeValue),
                subtitle = volumeSubtitle,
                icon = Icons.Default.FitnessCenter,
                iconTint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Avg Duration",
                value = "$durationValue",
                subtitle = "minutes",
                icon = Icons.Default.Timer,
                iconTint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (uiState.isChartsLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                    contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val chartTitle = when (uiState.selectedDateRange) {
                DateRangeFilter.LAST_7_DAYS, DateRangeFilter.LAST_30_DAYS -> "Workouts Per Day"
                else -> "Workouts Per Month"
            }
            SectionHeader(title = chartTitle)

            Spacer(modifier = Modifier.height(12.dp))

            WorkoutFrequencyChart(
                workoutCounts = uiState.workoutCounts,
                dateRangeFilter = uiState.selectedDateRange,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Volume Trend")

            Spacer(modifier = Modifier.height(12.dp))

            VolumeTrendChart(
                volumeData = uiState.volumeProgress,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Bodyweight Trend")

            Spacer(modifier = Modifier.height(12.dp))

            BodyweightTrendChart(
                entries = uiState.bodyweightEntries,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .height(16.dp)
                .width(3.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(2.dp)
                )
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun formatVolume(volume: Float): String {
    return when {
        volume >= 1_000_000 -> {
            val formatted = volume / 1_000_000
            String.format(Locale.US, "%.1fM", formatted)
        }
        volume >= 1_000 -> {
            val formatted = volume / 1_000
            String.format(Locale.US, "%.1fK", formatted)
        }
        else -> {
            NumberFormat.getNumberInstance(Locale.US).format(volume.toInt())
        }
    }
}
