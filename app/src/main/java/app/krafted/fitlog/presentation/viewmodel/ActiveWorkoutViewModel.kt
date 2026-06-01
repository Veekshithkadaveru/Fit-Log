package app.krafted.fitlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fitlog.domain.model.Workout
import app.krafted.fitlog.domain.model.WeightUnit
import app.krafted.fitlog.domain.repository.UserPreferencesRepository
import app.krafted.fitlog.domain.repository.WorkoutRepository
import app.krafted.fitlog.presentation.viewmodel.manager.ActiveWorkoutSessionManager
import app.krafted.fitlog.presentation.viewmodel.manager.ActiveWorkoutSetManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val sessionManager: ActiveWorkoutSessionManager,
    private val setManager: ActiveWorkoutSetManager,
    private val workoutRepository: WorkoutRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<ActiveWorkoutUiState> = sessionManager.sessionState

    private val _navigationEvent = Channel<WorkoutNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _prEvent = Channel<PREvent>()
    val prEvent = _prEvent.receiveAsFlow()

    val restTimerState: StateFlow<RestTimerState> = sessionManager.restTimerState

    private val _weightUnit = MutableStateFlow(WeightUnit.KG)
    val weightUnit: StateFlow<WeightUnit> = _weightUnit

    private var restTimerDurationSeconds = 90

    private val weightUpdates = MutableSharedFlow<Triple<Int, Float, Int>>()
    private val repsUpdates = MutableSharedFlow<Triple<Int, Float, Int>>()

    private val setUpdates = MutableSharedFlow<SetUpdateData>()

    // Channel to process set completion toggles sequentially
    private val setCompletionEvents = Channel<Int>(Channel.UNLIMITED)

    // Map of setId -> last click timestamp to prevent rapid double-clicks on the same set
    private val lastToggleTimestamps = mutableMapOf<Int, Long>()

    data class SetUpdateData(
        val setId: Int,
        val weight: Float? = null,
        val reps: Int? = null
    )

    init {
        sessionManager.loadActiveWorkout(viewModelScope)
        setupDebouncing()
        setupEventCollection()
        setupSetCompletionDebouncing()
        observePreferences()
    }
    
    @OptIn(FlowPreview::class)
    private fun setupDebouncing() {
        setUpdates
            .debounce(500L)
            .onEach { update ->
                val currentWorkout = uiState.value.currentWorkout ?: return@onEach
                val currentSet = currentWorkout.sets.find { it.id == update.setId } ?: return@onEach
                
                setManager.updateSet(
                    setId = update.setId,
                    weight = update.weight,
                    reps = update.reps,
                    currentSet = currentSet
                )

                // If the set was updated and is no longer marked as a PR, remove it from session PRs
                val updatedSetInDb = workoutRepository.getSetsForWorkoutOnce(currentWorkout.id).find { it.id == update.setId }
                if (updatedSetInDb != null && !updatedSetInDb.isPR) {
                    sessionManager.removeSessionPR(update.setId)
                }

                reloadContent(currentWorkout.id)
            }
            .launchIn(viewModelScope)
    }

    private fun setupEventCollection() {
        viewModelScope.launch {
            setManager.prEvents.collect { event ->
                _prEvent.send(event)
                sessionManager.addSessionPR(event)
            }
        }
    }
    
    private suspend fun reloadContent(workoutId: Int? = null) {
        val id = workoutId ?: uiState.value.currentWorkout?.id ?: return
        val updatedWorkout = workoutRepository.getWorkoutById(id) ?: return
        sessionManager.reloadExercises(updatedWorkout)
    }

    fun startWorkout(routineId: Int? = null) {
        viewModelScope.launch {
            sessionManager.startWorkout(routineId, viewModelScope)
        }
    }

    fun endWorkout() {
        viewModelScope.launch {
            val sessionPRs = uiState.value.sessionPRs
            sessionManager.endWorkout()
            
            if (sessionPRs.isNotEmpty()) {
                sessionManager.setShowWorkoutSummary(true)
            } else {
                _navigationEvent.send(WorkoutNavigationEvent.NavigateToHistory)
            }
        }
    }

    fun dismissWorkoutSummary() {
         viewModelScope.launch {
             _navigationEvent.send(WorkoutNavigationEvent.NavigateToHistory)
         }
    }

    fun discardWorkout() {
        viewModelScope.launch {
            sessionManager.discardWorkout()
            _navigationEvent.send(WorkoutNavigationEvent.NavigateBack)
        }
    }

    fun addSet(exerciseId: Int, autoFill: Boolean = false) {
        viewModelScope.launch {
            val workout = uiState.value.currentWorkout ?: return@launch
            val newSet = setManager.addSet(
                workoutId = workout.id,
                exerciseId = exerciseId,
                currentSets = workout.sets,
                autoFill = autoFill
            )
            reloadContent(workout.id)
        }
    }

    fun duplicateSet(setId: Int) {
        viewModelScope.launch {
             val workout = uiState.value.currentWorkout ?: return@launch
             val set = workout.sets.find { it.id == setId } ?: return@launch
             setManager.duplicateSet(workout.id, set, workout.sets)
             reloadContent(workout.id)
        }
    }
    
    fun updateSet(setId: Int, weight: Float? = null, reps: Int? = null) {
        viewModelScope.launch {
            setUpdates.emit(SetUpdateData(setId, weight, reps))
        }
    }





    private fun observePreferences() {
        viewModelScope.launch {
            userPreferencesRepository.restTimerDuration
                .catch { /* keep default on error */ }
                .collect { seconds -> restTimerDurationSeconds = seconds }
        }
        // Snapshot weight unit once at workout start — changing the setting
        // mid-workout should NOT alter the units shown on the active screen.
        viewModelScope.launch {
            try {
                _weightUnit.value = userPreferencesRepository.weightUnit.first()
            } catch (_: Exception) {
                /* keep default KG on error */
            }
        }
    }

    private fun setupSetCompletionDebouncing() {
        viewModelScope.launch {
            for (setId in setCompletionEvents) {
                val workout = uiState.value.currentWorkout ?: continue
                val set = workout.sets.find { it.id == setId } ?: continue

                val isPR = setManager.toggleSetCompleted(set)

                if (!isPR) {
                    sessionManager.removeSessionPR(setId)
                }

                reloadContent(workout.id)

                if (!set.isCompleted) {
                    sessionManager.startRestTimer(restTimerDurationSeconds)
                }
            }
        }
    }

    fun toggleSetCompleted(setId: Int) {
        val now = System.currentTimeMillis()
        val lastClick = lastToggleTimestamps[setId] ?: 0L
        if (now - lastClick > 300L) {
            lastToggleTimestamps[setId] = now
            viewModelScope.launch {
                setCompletionEvents.send(setId)
            }
        }
    }

    fun deleteSet(setId: Int) {
        viewModelScope.launch {
            setManager.deleteSet(setId)
            sessionManager.removeSessionPR(setId)
            reloadContent()
        }
    }

    fun addExerciseToWorkout(exerciseId: Int) {
        viewModelScope.launch {
            val workout = uiState.value.currentWorkout ?: return@launch
             if (uiState.value.workoutExercises.any { it.exercise.id == exerciseId }) {
                 sessionManager.setError("Exercise already added")
                 return@launch
             }
             addSet(exerciseId, autoFill = false)
        }
    }

    fun clearError() {
        sessionManager.clearError()
    }

    fun navigateToExerciseDetail(exerciseId: Int) {
        viewModelScope.launch {
            _navigationEvent.send(WorkoutNavigationEvent.NavigateToExerciseDetail(exerciseId))
        }
    }
    
    fun startRestTimer(durationSeconds: Int) = sessionManager.startRestTimer(durationSeconds)
    fun pauseResumeRestTimer() = sessionManager.pauseResumeRestTimer()
    fun skipRestTimer() = sessionManager.stopRestTimer()
    fun addRestTime(seconds: Int) = sessionManager.addRestTime(seconds)
    fun closeRestTimer() = sessionManager.stopRestTimer()

    override fun onCleared() {
        super.onCleared()
        sessionManager.stopWorkoutTimer()
    }
}
