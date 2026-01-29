package com.example.fitlog.presentation.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.model.Workout
import com.example.fitlog.domain.model.WorkoutSet
import com.example.fitlog.domain.repository.ExerciseRepository
import com.example.fitlog.domain.repository.RoutineRepository
import com.example.fitlog.domain.repository.WorkoutRepository
import com.example.fitlog.presentation.navigation.FitLogDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Workout Detail screen
 */
data class WorkoutDetailUiState(
    val isLoading: Boolean = true,
    val workout: Workout? = null,
    val routineName: String? = null,
    val exerciseGroups: List<ExerciseWithSets> = emptyList(),
    val error: String? = null
)

/**
 * Represents an exercise with its sets grouped together for display
 */
data class ExerciseWithSets(
    val exercise: Exercise,
    val sets: List<WorkoutSet>
) {
    val completedSets: Int
        get() = sets.count { it.isCompleted }

    val totalVolume: Float
        get() = sets.filter { it.isCompleted }.sumOf { (it.weight * it.reps).toDouble() }.toFloat()

    val hasPR: Boolean
        get() = sets.any { it.isPR }

    val prCount: Int
        get() = sets.count { it.isPR }
}

/**
 * ViewModel for the Workout Detail screen
 * Displays detailed breakdown of a completed workout
 */
@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val workoutId: Int = savedStateHandle.get<Int>(FitLogDestinations.Args.WORKOUT_ID) ?: 0

    private val _uiState = MutableStateFlow(WorkoutDetailUiState())
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    init {
        loadWorkoutDetails()
    }

    private fun loadWorkoutDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val workout = workoutRepository.getWorkoutById(workoutId)

                if (workout == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Workout not found"
                    )
                    return@launch
                }


                val routineName = workout.routineId?.let { routineId ->
                    routineRepository.getRoutineById(routineId)?.name
                }


                val exerciseGroups = groupSetsByExercise(workout.sets)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workout = workout,
                    routineName = routineName,
                    exerciseGroups = exerciseGroups,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load workout details"
                )
            }
        }
    }

    private suspend fun groupSetsByExercise(sets: List<WorkoutSet>): List<ExerciseWithSets> {

        val groupedSets = sets.groupBy { it.exerciseId }


        return groupedSets.mapNotNull { (exerciseId, exerciseSets) ->
            val exercise = exerciseRepository.getExerciseById(exerciseId)
            if (exercise != null) {
                ExerciseWithSets(
                    exercise = exercise,
                    sets = exerciseSets.sortedBy { it.setOrder }
                )
            } else {
                null
            }
        }
    }

    /**
     * Get display name for the workout type
     */
    fun getWorkoutTypeName(): String {
        val workout = _uiState.value.workout ?: return "Workout"
        return when {
            workout.isCardio -> "Cardio Session"
            _uiState.value.routineName != null -> _uiState.value.routineName!!
            else -> "Quick Workout"
        }
    }

    /**
     * Refresh workout details
     */
    fun refresh() {
        loadWorkoutDetails()
    }
}
