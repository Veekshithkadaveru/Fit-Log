package com.example.fitlog.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Workout
import com.example.fitlog.domain.repository.RoutineRepository
import com.example.fitlog.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the History screen
 */
data class HistoryUiState(
    val isLoading: Boolean = true,
    val workouts: List<Workout> = emptyList(),
    val routineNames: Map<Int, String> = emptyMap(),
    val error: String? = null
)

/**
 * Represents a workout item with its associated routine name for display
 */
data class WorkoutHistoryItem(
    val workout: Workout,
    val routineName: String?
)

/**
 * ViewModel for the History screen
 * Manages the list of completed workouts and their associated routine names
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            workoutRepository.getAllWorkouts()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load workouts"
                    )
                }
                .collect { workouts ->
                    // Filter out in-progress workouts (only show completed ones)
                    val completedWorkouts = workouts.filter { !it.isInProgress }

                    // Get unique routine IDs and fetch their names
                    val routineIds = completedWorkouts.mapNotNull { it.routineId }.distinct()
                    val routineNames = mutableMapOf<Int, String>()

                    routineIds.forEach { routineId ->
                        try {
                            val routine = routineRepository.getRoutineById(routineId)
                            routine?.let {
                                routineNames[routineId] = it.name
                            }
                        } catch (e: Exception) {
                            // If we can't get the routine name, just skip it
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        workouts = completedWorkouts,
                        routineNames = routineNames,
                        error = null
                    )
                }
        }
    }

    /**
     * Get the routine name for a workout, or a default label
     */
    fun getRoutineNameForWorkout(workout: Workout): String {
        return when {
            workout.isCardio -> "Cardio Session"
            workout.routineId != null -> _uiState.value.routineNames[workout.routineId] ?: "Custom Workout"
            else -> "Quick Workout"
        }
    }

    /**
     * Refresh the workout list
     */
    fun refresh() {
        loadWorkouts()
    }
}
