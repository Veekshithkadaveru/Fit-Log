package com.example.fitlog.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Bodyweight
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
import javax.inject.Inject

data class ProgressDashboardUiState(
    val isLoading: Boolean = true,
    val progressSummary: ProgressSummary? = null,
    val selectedDateRange: DateRangeFilter = DateRangeFilter.LAST_7_DAYS,
    val error: String? = null,
    // Chart data
    val workoutCounts: List<WorkoutCountPoint> = emptyList(),
    val isShowingDailyData: Boolean = true,

    val volumeProgress: List<VolumeProgressPoint> = emptyList(),
    val bodyweightEntries: List<Bodyweight> = emptyList(),
    val isChartsLoading: Boolean = true
)

@HiltViewModel
class ProgressDashboardViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val bodyweightRepository: BodyweightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressDashboardUiState())
    val uiState: StateFlow<ProgressDashboardUiState> = _uiState.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, isChartsLoading = true, error = null)
            try {
                val summary = progressRepository.getProgressSummary()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    progressSummary = summary
                )
                loadChartData()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isChartsLoading = false,
                    error = e.message ?: "Failed to load progress data"
                )
            }
        }
    }

    private suspend fun loadChartData() {
        try {
            val dateRange = _uiState.value.selectedDateRange
            val (startDate, endDate) = progressRepository.getDateRangeForFilter(dateRange)

            val isDaily = dateRange == DateRangeFilter.LAST_7_DAYS || dateRange == DateRangeFilter.LAST_30_DAYS

            
            val rawWorkoutCounts = if (isDaily) {
                progressRepository.getWorkoutCountByDay(startDate, endDate)
            } else {
                progressRepository.getWorkoutCountByMonth(startDate, endDate)
            }

            val workoutCounts = if (isDaily) {

                fillMissingDailyPoints(rawWorkoutCounts, startDate, endDate)
            } else {
                fillMissingMonthlyPoints(rawWorkoutCounts, startDate, endDate)
            }

            val volumeProgress = progressRepository.getDailyVolume(startDate, endDate)
            val bodyweightEntries = bodyweightRepository.getEntriesInDateRange(startDate, endDate).first()

            _uiState.value = _uiState.value.copy(
                workoutCounts = workoutCounts,
                isShowingDailyData = isDaily,
                volumeProgress = volumeProgress,
                bodyweightEntries = bodyweightEntries,
                isChartsLoading = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isChartsLoading = false
            )
        }
    }

    private fun fillMissingDailyPoints(
        existingPoints: List<WorkoutCountPoint>,
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint> {
        val filledList = mutableListOf<WorkoutCountPoint>()
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = startDate
        
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)

        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)

        val pointsMap = existingPoints.associateBy { point ->

             val c = java.util.Calendar.getInstance()
             c.timeInMillis = point.periodStart
             c.set(java.util.Calendar.HOUR_OF_DAY, 0)
             c.set(java.util.Calendar.MINUTE, 0)
             c.set(java.util.Calendar.SECOND, 0)
             c.set(java.util.Calendar.MILLISECOND, 0)
             c.timeInMillis
        }

        while (calendar.timeInMillis <= endDate) {
            val dateMillis = calendar.timeInMillis
            val existingPoint = pointsMap[dateMillis]
            
            filledList.add(
                existingPoint ?: WorkoutCountPoint(periodStart = dateMillis, count = 0)
            )
            
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }
        
        return filledList
    }

    private fun fillMissingMonthlyPoints(
        existingPoints: List<WorkoutCountPoint>,
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint> {
        val filledList = mutableListOf<WorkoutCountPoint>()
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = startDate
        
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)

        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM", java.util.Locale.US)

        val pointsMap = existingPoints.associateBy { point ->
             dateFormat.format(java.util.Date(point.periodStart))
        }

        while (calendar.timeInMillis <= endDate) {
            val dateMillis = calendar.timeInMillis
            val dateKey = dateFormat.format(java.util.Date(dateMillis))
            
            filledList.add(
                pointsMap[dateKey] ?: WorkoutCountPoint(periodStart = dateMillis, count = 0)
            )
            
            calendar.add(java.util.Calendar.MONTH, 1)
        }
        
        return filledList
    }

    fun selectDateRange(filter: DateRangeFilter) {
        if (filter != _uiState.value.selectedDateRange) {
            _uiState.value = _uiState.value.copy(
                selectedDateRange = filter,
                isChartsLoading = true
            )
            viewModelScope.launch {
                loadChartData()
            }
        }
    }

    fun refresh() {
        loadAllData()
    }
}
