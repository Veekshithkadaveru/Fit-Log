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

    val uiState: StateFlow<ExercisePickerUiState> = combine(
        _searchQuery,
        _selectedMuscle,
        _isLoading,
        exerciseRepository.getAllExercises()
    ) { query, muscle, loading, allExercises ->
             val filtered = allExercises.filter { exercise ->
                 val matchesQuery = exercise.name.contains(query, ignoreCase = true)
                 val matchesMuscle = muscle == null || when (muscle) {
                     "Arms" -> exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.BICEPS ||
                               exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.TRICEPS ||
                               exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.FOREARMS
                     "Legs" -> exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.LEGS ||
                               exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.QUADRICEPS ||
                               exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.HAMSTRINGS ||
                               exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.GLUTES ||
                               exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.CALVES
                     "Abs" -> exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.CORE
                     "Back" -> exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.BACK ||
                               exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.LATS ||
                               exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.TRAPS
                     "Shoulders" -> exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.SHOULDERS ||
                                    exercise.primaryMuscle == com.example.fitlog.domain.model.MuscleGroup.REAR_DELTS
                     else -> exercise.primaryMuscle.displayName.equals(muscle, ignoreCase = true)
                 }
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
                val count = routineRepository.getExerciseCountForRoutine(routineId)
                val newOrder = count + 1
                
                val joinEntity = com.example.fitlog.domain.model.RoutineExercise(
                    id = 0,
                    routineId = routineId,
                    exerciseId = exerciseId,
                    orderIndex = newOrder,
                    targetSets = 3,
                    targetReps = "8-12"
                )
                routineRepository.insertRoutineExercise(joinEntity)
                
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
