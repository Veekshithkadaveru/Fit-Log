package com.example.fitlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Routine
import com.example.fitlog.domain.model.RoutineExercise
import com.example.fitlog.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RoutineUiState())
    val uiState: StateFlow<RoutineUiState> = _uiState.asStateFlow()

    private val _navigationEvent = Channel<RoutineNavigationEvent>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    val userRoutines = routineRepository.getUserRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val templates = routineRepository.getTemplateRoutines()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val groupedTemplates = routineRepository.getTemplateRoutines()
        .map { routines ->
            routines.groupBy { it.name.substringBefore(" - ") }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun createRoutineFromTemplate(template: Routine) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // 1. Create new routine from template
                val newRoutine = template.copy(
                    id = 0, // Reset ID for auto-generation
                    isTemplate = false,
                    dayOrder = 0
                )
                val newRoutineId = routineRepository.insertRoutine(newRoutine)

                // 2. Copy exercises
                if (template.exercises.isNotEmpty()) {
                    val newExercises = template.exercises.map { 
                        it.copy(
                            id = 0,
                            routineId = newRoutineId.toInt(),
                            exercise = null // Don't duplicate the full exercise object in the relationship, just IDs
                        )
                    }
                    routineRepository.insertRoutineExercises(newExercises)
                }

                // 3. Navigate to editor
                _navigationEvent.send(RoutineNavigationEvent.NavigateToEditor(newRoutineId.toInt()))
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    // Editor State
    private val _editorUiState = MutableStateFlow(RoutineEditorUiState())
    val editorUiState: StateFlow<RoutineEditorUiState> = _editorUiState.asStateFlow()

    fun loadRoutine(routineId: Int) {
        if (routineId == 0) {
             // New routine from scratch
            _editorUiState.value = RoutineEditorUiState(
                routine = Routine(
                    id = 0,
                    name = "",
                    isTemplate = false,
                    dayOrder = 0,
                    exercises = emptyList()
                )
            )
            return
        }

        viewModelScope.launch {
            _editorUiState.value = _editorUiState.value.copy(isLoading = true)
            try {
                val routine = routineRepository.getRoutineById(routineId)
                if (routine != null) {
                    // Fetch exercises for this routine to ensure we have them
                    val exercises = routineRepository.getExercisesForRoutineOnce(routineId)
                    val fullRoutine = routine.copy(exercises = exercises)
                    _editorUiState.value = RoutineEditorUiState(routine = fullRoutine)
                }
            } catch (e: Exception) {
                // error
            } finally {
                _editorUiState.value = _editorUiState.value.copy(isLoading = false)
            }
        }
    }

    fun updateRoutineName(name: String) {
        val current = _editorUiState.value.routine ?: return
        _editorUiState.value = _editorUiState.value.copy(
            routine = current.copy(name = name)
        )
    }

    fun updateRoutineDayOrder(dayOrder: Int) {
        val current = _editorUiState.value.routine ?: return
        _editorUiState.value = _editorUiState.value.copy(
            routine = current.copy(dayOrder = dayOrder)
        )
    }

    fun updateExerciseTarget(routineExerciseId: Int, sets: Int?, reps: String?) {
        val currentRoutine = _editorUiState.value.routine ?: return
        val updatedExercises = currentRoutine.exercises.map {
            if (it.id == routineExerciseId) {
                it.copy(
                    targetSets = sets ?: it.targetSets,
                    targetReps = reps ?: it.targetReps
                )
            } else {
                it
            }
        }
        _editorUiState.value = _editorUiState.value.copy(
            routine = currentRoutine.copy(exercises = updatedExercises)
        )
    }

    fun saveRoutine() {
        viewModelScope.launch {
            val currentRoutine = _editorUiState.value.routine ?: return@launch
            _editorUiState.value = _editorUiState.value.copy(isSaving = true)
            try {
                if (currentRoutine.id == 0) {
                    // Insert New
                    val newId = routineRepository.insertRoutine(currentRoutine)
                    _navigationEvent.send(RoutineNavigationEvent.NavigateBack)
                } else {
                    // Update Existing
                    routineRepository.updateRoutine(currentRoutine)
                    // Update all exercises
                    currentRoutine.exercises.forEach {
                        routineRepository.updateRoutineExercise(it)
                    }
                    _navigationEvent.send(RoutineNavigationEvent.NavigateBack)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _editorUiState.value = _editorUiState.value.copy(isSaving = false)
            }
        }
    }

    fun deleteRoutine() {
        viewModelScope.launch {
            val current = _editorUiState.value.routine ?: return@launch
            _editorUiState.value = _editorUiState.value.copy(isSaving = true)
            try {
                if (current.id != 0) {
                    routineRepository.deleteRoutine(current)
                }
                _navigationEvent.send(RoutineNavigationEvent.NavigateBack)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _editorUiState.value = _editorUiState.value.copy(isSaving = false)
            }
        }
    }

    fun removeExercise(routineExercise: RoutineExercise) {
        viewModelScope.launch {
            try {
                // Delete from DB which handles list removal
                routineRepository.deleteRoutineExercise(routineExercise)
                // Reload to refresh list and order
                val routineId = _editorUiState.value.routine?.id ?: return@launch
                loadRoutine(routineId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun moveExercise(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            val currentRoutine = _editorUiState.value.routine ?: return@launch
            val exercises = currentRoutine.exercises.toMutableList()
            if (fromIndex !in exercises.indices || toIndex !in exercises.indices) return@launch

            val item = exercises.removeAt(fromIndex)
            exercises.add(toIndex, item)
            
            // Optimistic Update UI
            _editorUiState.value = _editorUiState.value.copy(
                routine = currentRoutine.copy(exercises = exercises)
            )

            try {
                // Persist new order
                val orderedIds = exercises.map { it.id }
                routineRepository.reorderExercises(currentRoutine.id, orderedIds)
            } catch (e: Exception) {
                e.printStackTrace()
                // Revert or reload on error
                loadRoutine(currentRoutine.id)
            }
        }
    }
}

data class RoutineUiState(
    val isLoading: Boolean = false,
    val selectedRoutine: Routine? = null
)

data class RoutineEditorUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val nameError: String? = null,
    val routine: Routine? = null
)

sealed class RoutineNavigationEvent {
    data class NavigateToEditor(val routineId: Int) : RoutineNavigationEvent()
    object NavigateBack : RoutineNavigationEvent()
}
