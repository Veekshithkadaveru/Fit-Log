package app.krafted.fitlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fitlog.domain.model.Workout
import app.krafted.fitlog.domain.repository.WorkoutRepository
import app.krafted.fitlog.presentation.viewmodel.manager.ActiveWorkoutSessionManager
import app.krafted.fitlog.presentation.viewmodel.manager.ActiveWorkoutSetManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActiveWorkoutViewModel @Inject constructor(
    private val sessionManager: ActiveWorkoutSessionManager,
    private val setManager: ActiveWorkoutSetManager,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    val uiState: StateFlow<ActiveWorkoutUiState> = sessionManager.sessionState

    private val _navigationEvent = Channel<WorkoutNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private val _prEvent = Channel<PREvent>()
    val prEvent = _prEvent.receiveAsFlow()

    val restTimerState: StateFlow<RestTimerState> = sessionManager.restTimerState

    private val weightUpdates = MutableSharedFlow<Triple<Int, Float, Int>>()
    private val repsUpdates = MutableSharedFlow<Triple<Int, Float, Int>>()
    
    private val setUpdates = MutableSharedFlow<SetUpdateData>()

    data class SetUpdateData(
        val setId: Int,
        val weight: Float? = null,
        val reps: Int? = null
    )

    init {
        sessionManager.loadActiveWorkout(viewModelScope)
        setupDebouncing()
        setupEventCollection()
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
                reloadContent(currentWorkout.id)
            }
            .launchIn(viewModelScope)
    }

    private fun setupEventCollection() {
        viewModelScope.launch {
            setManager.prEvents.collect { event ->
                _prEvent.send(event)
                sessionManager.updateUiState { state ->
                    state.copy(
                        sessionPRCount = state.sessionPRCount + 1,
                        sessionPRs = state.sessionPRs + event
                    )
                }
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
                sessionManager.updateUiState { it.copy(showWorkoutSummary = true) }
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

    fun toggleSetCompleted(setId: Int) {
        viewModelScope.launch {
            val workout = uiState.value.currentWorkout ?: return@launch
            val set = workout.sets.find { it.id == setId } ?: return@launch
            
            val isPR = setManager.toggleSetCompleted(set)
            
            reloadContent(workout.id)
            
            if (!set.isCompleted) { 
                sessionManager.startRestTimer(90)
            }
        }
    }

    fun deleteSet(setId: Int) {
        viewModelScope.launch {
            setManager.deleteSet(setId)
            reloadContent()
        }
    }

    fun addExerciseToWorkout(exerciseId: Int) {
        viewModelScope.launch {
            val workout = uiState.value.currentWorkout ?: return@launch
             if (uiState.value.workoutExercises.any { it.exercise.id == exerciseId }) {
                 sessionManager.updateUiState { it.copy(error = "Exercise already added") }
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
