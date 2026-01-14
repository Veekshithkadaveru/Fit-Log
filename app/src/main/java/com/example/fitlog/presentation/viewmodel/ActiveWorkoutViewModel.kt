package com.example.fitlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.model.Workout
import com.example.fitlog.domain.model.WorkoutSet
import com.example.fitlog.domain.repository.ExerciseRepository
import com.example.fitlog.domain.repository.RoutineRepository
import com.example.fitlog.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val routineRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActiveWorkoutUiState())
    val uiState: StateFlow<ActiveWorkoutUiState> = _uiState.asStateFlow()

    private val _navigationEvent = Channel<WorkoutNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private var timerJob: Job? = null

    init {
        loadActiveWorkout()
    }

    /**
     * Load existing active workout from database
     */
    fun loadActiveWorkout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val activeWorkout = workoutRepository.getActiveWorkout()
                if (activeWorkout != null) {
                    // Load routine name if applicable
                    val routineName = activeWorkout.routineId?.let { routineId ->
                        routineRepository.getRoutineById(routineId)?.name
                    }

                    // Load exercises for this workout
                    val workoutExercises = loadExercisesForWorkout(activeWorkout)

                    _uiState.value = _uiState.value.copy(
                        currentWorkout = activeWorkout,
                        isWorkoutActive = true,
                        routineName = routineName,
                        workoutExercises = workoutExercises,
                        isLoading = false
                    )
                    startWorkoutTimer()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isWorkoutActive = false,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load workout: ${e.message}"
                )
            }
        }
    }

    /**
     * Start a new workout session
     * @param routineId Optional routine to base workout on
     */
    fun startWorkout(routineId: Int? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Check if there's already an active workout
                val existingWorkout = workoutRepository.getActiveWorkout()
                if (existingWorkout != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "A workout is already in progress"
                    )
                    return@launch
                }

                // Create new workout
                val newWorkout = Workout(
                    startTime = System.currentTimeMillis(),
                    endTime = null,
                    routineId = routineId,
                    notes = null,
                    isCardio = false
                )

                val workoutId = workoutRepository.insertWorkout(newWorkout).toInt()
                val createdWorkout = newWorkout.copy(id = workoutId)

                // Load routine name and exercises if applicable
                val routineName = routineId?.let { id ->
                    routineRepository.getRoutineById(id)?.name
                }

                val workoutExercises = if (routineId != null) {
                    loadRoutineExercises(routineId)
                } else {
                    emptyList()
                }

                _uiState.value = _uiState.value.copy(
                    currentWorkout = createdWorkout,
                    isWorkoutActive = true,
                    routineName = routineName,
                    workoutExercises = workoutExercises,
                    isLoading = false
                )

                startWorkoutTimer()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to start workout: ${e.message}"
                )
            }
        }
    }

    /**
     * End the current workout session
     */
    fun endWorkout() {
        viewModelScope.launch {
            val currentWorkout = _uiState.value.currentWorkout ?: return@launch

            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Stop timer first
                stopWorkoutTimer()

                // Update workout with end time
                val completedWorkout = currentWorkout.copy(
                    endTime = System.currentTimeMillis()
                )
                workoutRepository.updateWorkout(completedWorkout)

                // Reset state
                _uiState.value = ActiveWorkoutUiState(isLoading = false)

                // Navigate to history or back
                _navigationEvent.send(WorkoutNavigationEvent.NavigateToHistory)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to end workout: ${e.message}"
                )
            }
        }
    }

    /**
     * Discard the current workout without saving
     */
    fun discardWorkout() {
        viewModelScope.launch {
            val currentWorkout = _uiState.value.currentWorkout ?: return@launch

            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                stopWorkoutTimer()
                workoutRepository.deleteWorkoutById(currentWorkout.id)

                _uiState.value = ActiveWorkoutUiState(isLoading = false)
                _navigationEvent.send(WorkoutNavigationEvent.NavigateBack)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to discard workout: ${e.message}"
                )
            }
        }
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Start the workout timer coroutine
     */
    private fun startWorkoutTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000L) // Update every second
                val workout = _uiState.value.currentWorkout ?: continue
                val duration = System.currentTimeMillis() - workout.startTime
                _uiState.value = _uiState.value.copy(workoutDuration = duration)
            }
        }
    }

    /**
     * Stop the workout timer coroutine
     */
    private fun stopWorkoutTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    /**
     * Load exercises from a routine template
     */
    private suspend fun loadRoutineExercises(routineId: Int): List<WorkoutExerciseWithDetails> {
        val routine = routineRepository.getRoutineById(routineId) ?: return emptyList()
        val routineExercises = routineRepository.getExercisesForRoutineOnce(routineId)

        return routineExercises.mapNotNull { routineExercise ->
            val exercise = exerciseRepository.getExerciseById(routineExercise.exerciseId)
            exercise?.let {
                WorkoutExerciseWithDetails(
                    exercise = it,
                    targetSets = routineExercise.targetSets,
                    targetReps = routineExercise.targetReps,
                    completedSets = 0,
                    orderIndex = routineExercise.orderIndex
                )
            }
        }.sortedBy { it.orderIndex }
    }

    /**
     * Load exercises for an existing workout
     */
    private suspend fun loadExercisesForWorkout(workout: Workout): List<WorkoutExerciseWithDetails> {
        // Group sets by exercise
        val setsByExercise = workout.sets.groupBy { it.exerciseId }

        return setsByExercise.map { (exerciseId, sets) ->
            val exercise = exerciseRepository.getExerciseById(exerciseId)
            val completedSets = sets.count { it.isCompleted }

            WorkoutExerciseWithDetails(
                exercise = exercise ?: Exercise(
                    id = exerciseId,
                    name = "Unknown Exercise",
                    primaryMuscle = com.example.fitlog.domain.model.MuscleGroup.CHEST,
                    secondaryMuscles = emptyList(),
                    category = com.example.fitlog.domain.model.ExerciseCategory.COMPOUND,
                    equipment = com.example.fitlog.domain.model.Equipment.BARBELL
                ),
                targetSets = sets.size,
                targetReps = "",
                completedSets = completedSets,
                orderIndex = 0
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopWorkoutTimer()
    }
}

/**
 * UI state for active workout screen
 */
data class ActiveWorkoutUiState(
    val isLoading: Boolean = false,
    val currentWorkout: Workout? = null,
    val workoutDuration: Long = 0L,
    val isWorkoutActive: Boolean = false,
    val routineName: String? = null,
    val workoutExercises: List<WorkoutExerciseWithDetails> = emptyList(),
    val error: String? = null
) {
    val exerciseCount: Int
        get() = workoutExercises.size

    val completedSetsCount: Int
        get() = workoutExercises.sumOf { it.completedSets }

    val totalSetsCount: Int
        get() = workoutExercises.sumOf { it.targetSets }
}

/**
 * Exercise details for display in workout
 */
data class WorkoutExerciseWithDetails(
    val exercise: Exercise,
    val targetSets: Int,
    val targetReps: String,
    val completedSets: Int,
    val orderIndex: Int
)

/**
 * Navigation events for workout screen
 */
sealed class WorkoutNavigationEvent {
    object NavigateToHistory : WorkoutNavigationEvent()
    object NavigateBack : WorkoutNavigationEvent()
    data class NavigateToExerciseDetail(val exerciseId: Int) : WorkoutNavigationEvent()
}
