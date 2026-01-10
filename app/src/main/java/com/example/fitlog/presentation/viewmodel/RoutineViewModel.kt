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
}

data class RoutineUiState(
    val isLoading: Boolean = false,
    val selectedRoutine: Routine? = null
)

sealed class RoutineNavigationEvent {
    data class NavigateToEditor(val routineId: Int) : RoutineNavigationEvent()
}
