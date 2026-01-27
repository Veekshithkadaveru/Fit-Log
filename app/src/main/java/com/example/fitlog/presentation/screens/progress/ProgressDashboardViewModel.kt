package com.example.fitlog.presentation.screens.progress

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.DateRangeFilter
import com.example.fitlog.domain.model.ProgressSummary
import com.example.fitlog.domain.model.VolumeProgressPoint
import com.example.fitlog.domain.model.WorkoutCountPoint
import com.example.fitlog.domain.repository.BodyweightRepository
import com.example.fitlog.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class ProgressDashboardUiState(
    val isLoading: Boolean = true,
    val progressSummary: ProgressSummary? = null,
    val selectedDateRange: DateRangeFilter = DateRangeFilter.LAST_7_DAYS,
    val workoutCounts: List<WorkoutCountPoint> = emptyList(),
    val volumeData: List<VolumeProgressPoint> = emptyList(),
    val bodyweightData: List<Pair<Long, Float>> = emptyList(),
    val isShowingDailyData: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ProgressDashboardViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val bodyweightRepository: BodyweightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressDashboardUiState())
    val uiState: StateFlow<ProgressDashboardUiState> = _uiState.asStateFlow()

    init {
        loadProgressSummary()
        loadChartData()
    }

    fun loadProgressSummary() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val summary = progressRepository.getProgressSummary()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    progressSummary = summary
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load progress data"
                )
            }
        }
    }

    private fun loadChartData() {
        viewModelScope.launch {
            try {
                val filter = _uiState.value.selectedDateRange
                val (startDate, endDate) = progressRepository.getDateRangeForFilter(filter)
                val isDaily = filter == DateRangeFilter.LAST_7_DAYS || filter == DateRangeFilter.LAST_30_DAYS

                Log.d("ProgressVM", "Loading chart data for filter: $filter, startDate: $startDate, endDate: $endDate")

                // Load workout counts
                val workoutCounts = if (isDaily) {
                    val dailyCounts = progressRepository.getWorkoutCountByDay(startDate, endDate)
                    Log.d("ProgressVM", "Daily counts from repo: ${dailyCounts.size}")
                    fillMissingDailyPoints(dailyCounts, startDate, endDate)
                } else {
                    val monthlyCounts = progressRepository.getWorkoutCountByMonth(startDate, endDate)
                    Log.d("ProgressVM", "Monthly counts from repo: ${monthlyCounts.size}")
                    fillMissingMonthlyPoints(monthlyCounts, startDate, endDate)
                }

                Log.d("ProgressVM", "Final workout counts: ${workoutCounts.size}")

                // Load volume data
                val volumeData = progressRepository.getDailyVolume(startDate, endDate)
                Log.d("ProgressVM", "Volume data points: ${volumeData.size}")

                // Load bodyweight data
                val bodyweightEntries = bodyweightRepository.getAllEntries().first()
                val bodyweightData = bodyweightEntries
                    .filter { it.date in startDate..endDate }
                    .map { entry -> entry.date to entry.weight }
                    .sortedBy { pair -> pair.first }

                Log.d("ProgressVM", "Bodyweight data points: ${bodyweightData.size}")

                _uiState.value = _uiState.value.copy(
                    workoutCounts = workoutCounts,
                    volumeData = volumeData,
                    bodyweightData = bodyweightData,
                    isShowingDailyData = isDaily
                )
            } catch (e: Exception) {
                Log.e("ProgressVM", "Error loading chart data", e)
            }
        }
    }

    private fun fillMissingDailyPoints(
        existingPoints: List<WorkoutCountPoint>,
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint> {
        val existingMap = existingPoints.associateBy { point ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = point.periodStart
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }

        val result = mutableListOf<WorkoutCountPoint>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startDate
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val endCal = Calendar.getInstance()
        endCal.timeInMillis = endDate

        while (!calendar.after(endCal)) {
            val dayStart = calendar.timeInMillis
            val count = existingMap[dayStart]?.count ?: 0
            result.add(WorkoutCountPoint(periodStart = dayStart, count = count))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return result.sortedBy { it.periodStart }
    }

    private fun fillMissingMonthlyPoints(
        existingPoints: List<WorkoutCountPoint>,
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint> {
        val existingMap = existingPoints.associateBy { point ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = point.periodStart
            cal.set(Calendar.DAY_OF_MONTH, 1)
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }

        val result = mutableListOf<WorkoutCountPoint>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = startDate
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val endCal = Calendar.getInstance()
        endCal.timeInMillis = endDate

        while (!calendar.after(endCal)) {
            val monthStart = calendar.timeInMillis
            val count = existingMap[monthStart]?.count ?: 0
            result.add(WorkoutCountPoint(periodStart = monthStart, count = count))
            calendar.add(Calendar.MONTH, 1)
        }

        return result.sortedBy { it.periodStart }
    }

    fun selectDateRange(filter: DateRangeFilter) {
        Log.d("ProgressVM", "selectDateRange called with: $filter, current: ${_uiState.value.selectedDateRange}")
        if (filter != _uiState.value.selectedDateRange) {
            _uiState.value = _uiState.value.copy(selectedDateRange = filter)
            loadChartData()
        }
    }

    fun refresh() {
        loadProgressSummary()
        loadChartData()
    }
}
