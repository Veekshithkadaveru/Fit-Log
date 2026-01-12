package com.example.fitlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.repository.ExerciseRepository
import com.example.fitlog.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val routineRepository: RoutineRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedMuscle = MutableStateFlow<String?>(null)
    private val _isLoading = MutableStateFlow(false)

    // Combined state for UI
    val uiState: StateFlow<ExercisePickerUiState> = combine(
        _searchQuery,
        _selectedMuscle,
        _isLoading,
        exerciseRepository.getAllExercises()
    ) { query, muscle, loading, allExercises ->
         val filtered = allExercises.filter { exercise ->
             val matchesQuery = exercise.name.contains(query, ignoreCase = true)
             val matchesMuscle = muscle == null || exercise.primaryMuscle.displayName.equals(muscle, ignoreCase = true)
             matchesQuery && matchesMuscle
         }
         ExercisePickerUiState(
             exercises = filtered,
             searchQuery = query,
             selectedMuscle = muscle,
             isLoading = loading
         )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ExercisePickerUiState(isLoading = true)
    )

    private val _navigationEvent = Channel<Unit>()
    val navigationEvent = _navigationEvent.receiveAsFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateMuscleFilter(muscle: String?) {
        _selectedMuscle.value = muscle
    }

    fun addExerciseToRoutine(routineId: Int, exerciseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Add exercise logic
                // 1. Get current max order
                val count = routineRepository.getExerciseCountForRoutine(routineId)
                val newOrder = count + 1
                
                // 2. Insert RoutineExercise
                val joinEntity = com.example.fitlog.domain.model.RoutineExercise(
                    id = 0,
                    routineId = routineId,
                    exerciseId = exerciseId,
                    orderIndex = newOrder,
                    targetSets = 3,
                    targetReps = "8-12"
                )
                routineRepository.insertRoutineExercise(joinEntity)
                
                // 3. Navigate back
                _navigationEvent.send(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

data class ExercisePickerUiState(
    val exercises: List<Exercise> = emptyList(),
    val searchQuery: String = "",
    val selectedMuscle: String? = null,
    val isLoading: Boolean = false
)
