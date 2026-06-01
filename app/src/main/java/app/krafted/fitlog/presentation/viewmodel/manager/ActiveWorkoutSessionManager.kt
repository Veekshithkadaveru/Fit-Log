package app.krafted.fitlog.presentation.viewmodel.manager

import app.krafted.fitlog.domain.model.Workout
import app.krafted.fitlog.presentation.viewmodel.WorkoutExerciseWithDetails
import app.krafted.fitlog.domain.repository.ExerciseRepository
import app.krafted.fitlog.domain.repository.RoutineRepository
import app.krafted.fitlog.domain.repository.WorkoutRepository
import app.krafted.fitlog.presentation.timer.RestTimerManager
import app.krafted.fitlog.presentation.viewmodel.ActiveWorkoutUiState
import app.krafted.fitlog.presentation.viewmodel.PREvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

/**
 * Manages the lifecycle of an active workout session (start, stop, timer).
 */
@javax.inject.Singleton
class ActiveWorkoutSessionManager @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val routineRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository,
    private val restTimerManager: RestTimerManager
) {
    private val _sessionState = MutableStateFlow(ActiveWorkoutUiState())
    val sessionState: StateFlow<ActiveWorkoutUiState> = _sessionState.asStateFlow()

    private val mutex = Mutex()
    private var workoutTimerJob: Job? = null

    val restTimerState = restTimerManager.restTimerState

    fun loadActiveWorkout(scope: CoroutineScope) {
        scope.launch {
            _sessionState.value = _sessionState.value.copy(isLoading = true)
            try {
                val activeWorkout = workoutRepository.getActiveWorkout()
                if (activeWorkout != null) {
                    val routineName = activeWorkout.routineId?.let { routineId ->
                        routineRepository.getRoutineById(routineId)?.name
                    }
                    
                    _sessionState.value = _sessionState.value.copy(
                        currentWorkout = activeWorkout,
                        isWorkoutActive = true,
                        routineName = routineName
                    )
                    
                    reloadExercises(activeWorkout)
                    
                    _sessionState.value = _sessionState.value.copy(isLoading = false)
                    startWorkoutTimer(scope)
                } else {
                    _sessionState.value = _sessionState.value.copy(
                        isWorkoutActive = false,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _sessionState.value = _sessionState.value.copy(
                    isLoading = false,
                    error = "Failed to load workout: ${e.message}"
                )
            }
        }
    }

    suspend fun startWorkout(routineId: Int?, scope: CoroutineScope) {
        mutex.withLock {
            _sessionState.value = _sessionState.value.copy(isLoading = true)
            try {
                val existingWorkout = workoutRepository.getActiveWorkout()
                if (existingWorkout != null) {
                    _sessionState.value = _sessionState.value.copy(
                        isLoading = false,
                        error = "A workout is already in progress"
                    )
                    return
                }

                val newWorkout = Workout(
                    startTime = System.currentTimeMillis(),
                    endTime = null,
                    routineId = routineId,
                    notes = null,
                    isCardio = false
                )

                val workoutId = workoutRepository.insertWorkout(newWorkout).toInt()
                val createdWorkout = workoutRepository.getWorkoutById(workoutId) ?: newWorkout.copy(id = workoutId)

                val routineName = routineId?.let { id ->
                    routineRepository.getRoutineById(id)?.name
                }
                
                if (routineId != null) {
                    val routineExercises = loadRoutineExercises(routineId)
                    _sessionState.value = _sessionState.value.copy(
                        workoutExercises = routineExercises
                    )
                }

                _sessionState.value = _sessionState.value.copy(
                    currentWorkout = createdWorkout,
                    isWorkoutActive = true,
                    routineName = routineName,
                    isLoading = false
                )

                startWorkoutTimer(scope)
            } catch (e: Exception) {
                _sessionState.value = _sessionState.value.copy(
                    isLoading = false,
                    error = "Failed to start workout: ${e.message}"
                )
            }
        }
    }

    suspend fun endWorkout() {
        mutex.withLock {
            _sessionState.value = _sessionState.value.copy(isLoading = true)
            try {
                stopWorkoutTimer()
                val currentWorkout = _sessionState.value.currentWorkout
                if (currentWorkout != null) {
                    val completedWorkout = currentWorkout.copy(endTime = System.currentTimeMillis())
                    workoutRepository.updateWorkout(completedWorkout)
                }
                _sessionState.value = _sessionState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _sessionState.value = _sessionState.value.copy(
                    isLoading = false,
                    error = "Failed to end workout: ${e.message}"
                )
            }
        }
    }

    suspend fun discardWorkout() {
        mutex.withLock {
            _sessionState.value = _sessionState.value.copy(isLoading = true)
            try {
                stopWorkoutTimer()
                val currentWorkout = _sessionState.value.currentWorkout
                if (currentWorkout != null) {
                    workoutRepository.deleteWorkoutById(currentWorkout.id)
                }
                _sessionState.value = ActiveWorkoutUiState(isLoading = false) // Reset state
            } catch (e: Exception) {
                _sessionState.value = _sessionState.value.copy(
                    isLoading = false,
                    error = "Failed to discard workout: ${e.message}"
                )
            }
        }
    }
    
    suspend fun reloadExercises(workout: Workout? = null) {
        val current = workout ?: _sessionState.value.currentWorkout ?: return
        
        if (workout != null && workout != _sessionState.value.currentWorkout) {
             _sessionState.value = _sessionState.value.copy(currentWorkout = workout)
        }
        
        val exercises = loadExercisesForWorkout(current)
        _sessionState.value = _sessionState.value.copy(
            workoutExercises = exercises,
            currentWorkout = current
        )
    }

    private suspend fun loadExercisesForWorkout(workout: Workout): List<WorkoutExerciseWithDetails> {
        val existingSetsByExercise = workout.sets.groupBy { it.exerciseId }
        val routineId = workout.routineId

        val routineExercises = if (routineId != null) {
            routineRepository.getExercisesForRoutineOnce(routineId)
        } else {
            emptyList()
        }

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

        val routineExerciseIds = routineExercises.map { it.exerciseId }.toSet()
        val extraExerciseIds = existingSetsByExercise.keys - routineExerciseIds
        
        val fromExtras = extraExerciseIds.mapNotNull { exerciseId ->
             val exercise = exerciseRepository.getExerciseById(exerciseId)
             val sets = existingSetsByExercise[exerciseId]?.sortedBy { it.setOrder } ?: emptyList()
             val completedSets = sets.count { it.isCompleted }
             
             exercise?.let {
                 WorkoutExerciseWithDetails(
                    exercise = it,
                    targetSets = 0,
                    targetReps = "",
                    completedSets = completedSets,
                    orderIndex = 999,
                    sets = sets
                 )
             }
        }

        return (fromRoutine + fromExtras).sortedBy { it.orderIndex }
    }
    
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

    private fun startWorkoutTimer(scope: CoroutineScope) {
        stopWorkoutTimer()
        workoutTimerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                val workout = _sessionState.value.currentWorkout ?: continue
                val duration = System.currentTimeMillis() - workout.startTime
                _sessionState.value = _sessionState.value.copy(workoutDuration = duration)
            }
        }
    }

    fun stopWorkoutTimer() {
        workoutTimerJob?.cancel()
        workoutTimerJob = null
    }

    fun startRestTimer(durationSeconds: Int) = restTimerManager.startTimer(durationSeconds)
    fun stopRestTimer() = restTimerManager.stopTimer()
    fun pauseResumeRestTimer() = restTimerManager.pauseResumeTimer()
    fun addRestTime(seconds: Int) = restTimerManager.addTime(seconds)
    
    fun clearError() {
        _sessionState.value = _sessionState.value.copy(error = null)
    }
    
    fun setError(message: String) {
        _sessionState.value = _sessionState.value.copy(error = message)
    }

    fun addSessionPR(event: PREvent) {
        val currentList = _sessionState.value.sessionPRs
        val updatedList = currentList.filterNot { it.setId == event.setId } + event
        _sessionState.value = _sessionState.value.copy(
            sessionPRs = updatedList,
            sessionPRCount = updatedList.size
        )
    }

    fun removeSessionPR(setId: Int) {
        val currentList = _sessionState.value.sessionPRs
        val updatedList = currentList.filterNot { it.setId == setId }
        _sessionState.value = _sessionState.value.copy(
            sessionPRs = updatedList,
            sessionPRCount = updatedList.size
        )
    }

    fun setShowWorkoutSummary(show: Boolean) {
        _sessionState.value = _sessionState.value.copy(showWorkoutSummary = show)
    }
}
