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
    private var restTimerJob: Job? = null

    // Rest Timer State
    private val _restTimerState = MutableStateFlow(RestTimerState())
    val restTimerState: StateFlow<RestTimerState> = _restTimerState.asStateFlow()

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
     * Add a new set to an exercise with optional auto-fill from previous session
     */
    fun addSet(exerciseId: Int, autoFill: Boolean = false) {
        viewModelScope.launch {
            val workout = _uiState.value.currentWorkout ?: return@launch

            try {
                // Find the next set order for this exercise
                val existingSets = workout.sets.filter { it.exerciseId == exerciseId }
                val nextSetOrder = (existingSets.maxOfOrNull { it.setOrder } ?: -1) + 1

                // Get auto-fill data if requested
                var weight = 0f
                var reps = 0

                if (autoFill) {
                    val recentSets = workoutRepository.getRecentSetsForExercise(exerciseId, 5)
                    if (recentSets.isNotEmpty()) {
                        // Use average of recent sets
                        weight = recentSets.map { it.weight }.average().toFloat()
                        reps = recentSets.map { it.reps }.average().toInt()
                    }
                }

                // Create new set
                val newSet = WorkoutSet(
                    workoutId = workout.id,
                    exerciseId = exerciseId,
                    weight = weight,
                    reps = reps,
                    isCompleted = false,
                    isPR = false,
                    setOrder = nextSetOrder
                )

                val setId = workoutRepository.insertSet(newSet).toInt()
                val createdSet = newSet.copy(id = setId)

                // Update local state
                val updatedWorkout = workout.copy(
                    sets = workout.sets + createdSet
                )
                _uiState.value = _uiState.value.copy(currentWorkout = updatedWorkout)

                // Reload exercises to update counts
                reloadExercises()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to add set: ${e.message}")
            }
        }
    }

    /**
     * Duplicate a set (copy weight, reps, notes)
     */
    fun duplicateSet(setId: Int) {
        viewModelScope.launch {
            val workout = _uiState.value.currentWorkout ?: return@launch

            try {
                val setToDuplicate = workout.sets.find { it.id == setId } ?: return@launch

                // Find the next set order for this exercise
                val exerciseSets = workout.sets.filter { it.exerciseId == setToDuplicate.exerciseId }
                val nextSetOrder = (exerciseSets.maxOfOrNull { it.setOrder } ?: -1) + 1

                // Create duplicate set
                val newSet = WorkoutSet(
                    workoutId = workout.id,
                    exerciseId = setToDuplicate.exerciseId,
                    weight = setToDuplicate.weight,
                    reps = setToDuplicate.reps,
                    isCompleted = false, // New set starts incomplete
                    isPR = false,
                    setOrder = nextSetOrder
                )

                val newSetId = workoutRepository.insertSet(newSet).toInt()
                val createdSet = newSet.copy(id = newSetId)

                // Update local state
                val updatedWorkout = workout.copy(
                    sets = workout.sets + createdSet
                )
                _uiState.value = _uiState.value.copy(currentWorkout = updatedWorkout)

                // Reload exercises
                reloadExercises()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to duplicate set: ${e.message}")
            }
        }
    }


    /**
     * Update set details (weight/reps)
     */
    fun updateSet(setId: Int, weight: Float? = null, reps: Int? = null) {
        viewModelScope.launch {
            val workout = _uiState.value.currentWorkout ?: return@launch

            try {
                val setToUpdate = workout.sets.find { it.id == setId } ?: return@launch
                
                val updatedSet = setToUpdate.copy(
                    weight = weight ?: setToUpdate.weight,
                    reps = reps ?: setToUpdate.reps
                )

                workoutRepository.updateSet(updatedSet)

                // Update local state
                val updatedSets = workout.sets.map { if (it.id == setId) updatedSet else it }
                _uiState.value = _uiState.value.copy(
                    currentWorkout = workout.copy(sets = updatedSets)
                )

                // Reload exercises to update UI
                reloadExercises()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to update set: ${e.message}")
            }
        }
    }

    /**
     * Toggle set completion status and auto-start rest timer
     */
    fun toggleSetCompleted(setId: Int) {
        viewModelScope.launch {
            val workout = _uiState.value.currentWorkout ?: return@launch

            try {
                val setToUpdate = workout.sets.find { it.id == setId } ?: return@launch
                val updatedSet = setToUpdate.copy(isCompleted = !setToUpdate.isCompleted)

                workoutRepository.updateSet(updatedSet)

                // Update local state
                val updatedSets = workout.sets.map { if (it.id == setId) updatedSet else it }
                _uiState.value = _uiState.value.copy(
                    currentWorkout = workout.copy(sets = updatedSets)
                )

                // Reload exercises to update completion counts
                reloadExercises()

                // Auto-start rest timer when completing a set (not when uncompleting)
                if (updatedSet.isCompleted) {
                    startRestTimer(90) // Default 90 seconds
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to toggle set: ${e.message}")
            }
        }
    }

    /**
     * Delete a set
     */
    fun deleteSet(setId: Int) {
        viewModelScope.launch {
            val workout = _uiState.value.currentWorkout ?: return@launch

            try {
                workoutRepository.deleteSetById(setId)

                // Update local state
                val updatedSets = workout.sets.filter { it.id != setId }
                _uiState.value = _uiState.value.copy(
                    currentWorkout = workout.copy(sets = updatedSets)
                )

                // Reload exercises to update counts
                reloadExercises()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to delete set: ${e.message}")
            }
        }
    }

    /**
     * Add an exercise to the active workout
     * Creates an initial empty set for the exercise
     */
    fun addExerciseToWorkout(exerciseId: Int) {
        viewModelScope.launch {
            val workout = _uiState.value.currentWorkout ?: return@launch

            try {
                // Check if exercise already exists in workout
                val exerciseAlreadyAdded = _uiState.value.workoutExercises
                    .any { it.exercise.id == exerciseId }

                if (exerciseAlreadyAdded) {
                    _uiState.value = _uiState.value.copy(
                        error = "Exercise already added to this workout"
                    )
                    return@launch
                }

                // Add an initial set for this exercise
                // This will make it appear in the workout
                addSet(exerciseId, autoFill = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Failed to add exercise: ${e.message}"
                )
            }
        }
    }

    /**
     * Reload exercises with updated set counts
     */
    private suspend fun reloadExercises() {
        val workout = _uiState.value.currentWorkout ?: return
        val exercises = loadExercisesForWorkout(workout)
        _uiState.value = _uiState.value.copy(workoutExercises = exercises)
    }

    /**
     * Clear error message
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Navigate to exercise detail screen
     */
    fun navigateToExerciseDetail(exerciseId: Int) {
        viewModelScope.launch {
            _navigationEvent.send(WorkoutNavigationEvent.NavigateToExerciseDetail(exerciseId))
        }
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
    /**
     * Load exercises for an existing workout
     */
    private suspend fun loadExercisesForWorkout(workout: Workout): List<WorkoutExerciseWithDetails> {
        val existingSetsByExercise = workout.sets.groupBy { it.exerciseId }
        val routineId = workout.routineId

        val routineExercises = if (routineId != null) {
            routineRepository.getExercisesForRoutineOnce(routineId)
        } else {
            emptyList()
        }

        // Map routine exercises to details
        val fromRoutine = routineExercises.mapNotNull { routineExercise ->
            val exercise = exerciseRepository.getExerciseById(routineExercise.exerciseId)
            val sets = existingSetsByExercise[routineExercise.exerciseId]?.sortedBy { it.setOrder } ?: emptyList()
            val completedSets = sets.count { it.isCompleted }

            exercise?.let {
                WorkoutExerciseWithDetails(
                    exercise = it,
                    targetSets = routineExercise.targetSets,
                    targetReps = routineExercise.targetReps,
                    completedSets = completedSets,
                    orderIndex = routineExercise.orderIndex,
                    sets = sets
                )
            }
        }

        // Handle exercises that are in sets but NOT in routine (e.g. ad-hoc additions)
        val routineExerciseIds = routineExercises.map { it.exerciseId }.toSet()
        val extraExerciseIds = existingSetsByExercise.keys - routineExerciseIds
        
        val fromExtras = extraExerciseIds.mapNotNull { exerciseId ->
             val exercise = exerciseRepository.getExerciseById(exerciseId)
             val sets = existingSetsByExercise[exerciseId]?.sortedBy { it.setOrder } ?: emptyList()
             val completedSets = sets.count { it.isCompleted }
             
             exercise?.let {
                 WorkoutExerciseWithDetails(
                    exercise = it,
                    targetSets = 0, // No target for ad-hoc
                    targetReps = "",
                    completedSets = completedSets,
                    orderIndex = 999, // Put at end
                    sets = sets
                 )
             }
        }

        return (fromRoutine + fromExtras).sortedBy { it.orderIndex }
    }











    /**
     * Start rest timer with given duration
     */
    fun startRestTimer(durationSeconds: Int = 90) {
        restTimerJob?.cancel()
        _restTimerState.value = RestTimerState(
            isActive = true,
            remainingSeconds = durationSeconds,
            totalSeconds = durationSeconds,
            isPaused = false
        )

        restTimerJob = viewModelScope.launch {
            while (isActive && _restTimerState.value.remainingSeconds > 0) {
                delay(1000L)
                if (!_restTimerState.value.isPaused) {
                    val newRemaining = _restTimerState.value.remainingSeconds - 1
                    _restTimerState.value = _restTimerState.value.copy(
                        remainingSeconds = newRemaining
                    )

                    // Timer complete
                    if (newRemaining == 0) {
                        _restTimerState.value = _restTimerState.value.copy(
                            isComplete = true
                        )
                    }
                }
            }
        }
    }

    /**
     * Pause or resume rest timer
     */
    fun pauseResumeRestTimer() {
        _restTimerState.value = _restTimerState.value.copy(
            isPaused = !_restTimerState.value.isPaused
        )
    }

    /**
     * Skip/stop rest timer
     */
    fun skipRestTimer() {
        restTimerJob?.cancel()
        _restTimerState.value = RestTimerState()
    }

    /**
     * Add time to rest timer
     */
    fun addRestTime(seconds: Int) {
        val current = _restTimerState.value
        _restTimerState.value = current.copy(
            remainingSeconds = current.remainingSeconds + seconds,
            totalSeconds = current.totalSeconds + seconds
        )
    }

    /**
     * Close rest timer overlay
     */
    fun closeRestTimer() {
        skipRestTimer()
    }

    override fun onCleared() {
        super.onCleared()
        stopWorkoutTimer()
        restTimerJob?.cancel()
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
        get() = workoutExercises.sumOf { kotlin.math.max(it.targetSets, it.sets.size) }
}

/**
 * Exercise details for display in workout
 */
data class WorkoutExerciseWithDetails(
    val exercise: Exercise,
    val targetSets: Int,
    val targetReps: String,
    val completedSets: Int,
    val orderIndex: Int,
    val sets: List<WorkoutSet> = emptyList()
)

/**
 * Navigation events for workout screen
 */
sealed class WorkoutNavigationEvent {
    object NavigateToHistory : WorkoutNavigationEvent()
    object NavigateBack : WorkoutNavigationEvent()
    data class NavigateToExerciseDetail(val exerciseId: Int) : WorkoutNavigationEvent()
}

/**
 * Rest timer state
 */
data class RestTimerState(
    val isActive: Boolean = false,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val isPaused: Boolean = false,
    val isComplete: Boolean = false
)
