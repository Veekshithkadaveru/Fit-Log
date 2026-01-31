package com.example.fitlog.presentation.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Workout
import com.example.fitlog.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * UI state for the Calendar screen
 */
data class CalendarUiState(
    val isLoading: Boolean = true,
    val currentMonth: YearMonth = YearMonth.now(),
    val workoutDays: Map<LocalDate, DayWorkoutInfo> = emptyMap(),
    val selectedDate: LocalDate? = null,
    val error: String? = null
)

/**
 * Information about workouts on a specific day
 */
data class DayWorkoutInfo(
    val workoutCount: Int,
    val hasStrength: Boolean,
    val hasCardio: Boolean,
    val workouts: List<Workout> = emptyList()
)

/**
 * ViewModel for the Calendar screen
 * Manages month navigation and workout day highlighting
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        loadWorkoutsForMonth(_uiState.value.currentMonth)
    }

    private fun loadWorkoutsForMonth(yearMonth: YearMonth) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Get start and end of month in milliseconds
                val startOfMonth = yearMonth.atDay(1).atStartOfDay()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                val endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                // Collect workouts for the month
                workoutRepository.getWorkoutsInDateRange(startOfMonth, endOfMonth)
                    .collect { workouts ->
                        // Filter out in-progress workouts
                        val completedWorkouts = workouts.filter { !it.isInProgress }

                        // Group workouts by date
                        val workoutDays = completedWorkouts.groupBy { workout ->
                            java.time.Instant.ofEpochMilli(workout.startTime)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        }.mapValues { (_, dayWorkouts) ->
                            DayWorkoutInfo(
                                workoutCount = dayWorkouts.size,
                                hasStrength = dayWorkouts.any { !it.isCardio },
                                hasCardio = dayWorkouts.any { it.isCardio },
                                workouts = dayWorkouts
                            )
                        }

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            workoutDays = workoutDays,
                            error = null
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load workouts"
                )
            }
        }
    }

    /**
     * Navigate to the previous month
     */
    fun previousMonth() {
        val newMonth = _uiState.value.currentMonth.minusMonths(1)
        _uiState.value = _uiState.value.copy(
            currentMonth = newMonth,
            selectedDate = null
        )
        loadWorkoutsForMonth(newMonth)
    }

    /**
     * Navigate to the next month
     */
    fun nextMonth() {
        val newMonth = _uiState.value.currentMonth.plusMonths(1)
        _uiState.value = _uiState.value.copy(
            currentMonth = newMonth,
            selectedDate = null
        )
        loadWorkoutsForMonth(newMonth)
    }

    /**
     * Go to today's month
     */
    fun goToToday() {
        val today = YearMonth.now()
        if (_uiState.value.currentMonth != today) {
            _uiState.value = _uiState.value.copy(
                currentMonth = today,
                selectedDate = LocalDate.now()
            )
            loadWorkoutsForMonth(today)
        } else {
            _uiState.value = _uiState.value.copy(selectedDate = LocalDate.now())
        }
    }

    /**
     * Select a specific date
     */
    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
    }

    /**
     * Clear selected date
     */
    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedDate = null)
    }

    /**
     * Get workouts for the selected date
     */
    fun getWorkoutsForSelectedDate(): List<Workout> {
        val selectedDate = _uiState.value.selectedDate ?: return emptyList()
        return _uiState.value.workoutDays[selectedDate]?.workouts ?: emptyList()
    }
}
