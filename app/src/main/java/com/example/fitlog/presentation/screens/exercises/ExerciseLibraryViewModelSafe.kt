package com.example.fitlog.presentation.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Equipment
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.model.ExerciseCategory
import com.example.fitlog.domain.model.MuscleGroup
import com.example.fitlog.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseLibraryViewModelSafe @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseLibraryUiState())
    val uiState: StateFlow<ExerciseLibraryUiState> = _uiState.asStateFlow()

    init {
        loadExercisesSafely()
        loadAvailableFiltersSafely()
    }

    private fun loadExercisesSafely() {
        viewModelScope.launch {
            try {
                exerciseRepository.getAllExercises()
                    .catch { e ->
                        // Handle database errors gracefully
                        _uiState.value = _uiState.value.copy(
                            exercises = getDefaultExercises(),
                            isLoading = false
                        )
                    }
                    .collect { exercises ->
                        _uiState.value = _uiState.value.copy(
                            exercises = if (exercises.isEmpty()) getDefaultExercises() else exercises,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                // Fallback to default exercises if database fails
                _uiState.value = _uiState.value.copy(
                    exercises = getDefaultExercises(),
                    isLoading = false
                )
            }
        }
    }

    private fun loadAvailableFiltersSafely() {
        viewModelScope.launch {
            try {
                exerciseRepository.getAllMuscleGroups()
                    .catch { /* Ignore filter errors */ }
                    .collect { muscleGroups ->
                        _uiState.value = _uiState.value.copy(
                            availableMuscleGroups = muscleGroups
                        )
                    }
            } catch (e: Exception) {
                // Ignore filter loading errors
            }
        }

        viewModelScope.launch {
            try {
                exerciseRepository.getAllEquipmentTypes()
                    .catch { /* Ignore filter errors */ }
                    .collect { equipment ->
                        _uiState.value = _uiState.value.copy(
                            availableEquipment = equipment
                        )
                    }
            } catch (e: Exception) {
                // Ignore filter loading errors
            }
        }
    }

    private fun getDefaultExercises(): List<Exercise> {
        return listOf(
            Exercise(
                id = 1,
                name = "Bench Press",
                primaryMuscle = MuscleGroup.CHEST,
                secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL
            ),
            Exercise(
                id = 2,
                name = "Squat",
                primaryMuscle = MuscleGroup.LEGS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL
            ),
            Exercise(
                id = 3,
                name = "Deadlift",
                primaryMuscle = MuscleGroup.BACK,
                secondaryMuscles = listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL
            ),
            Exercise(
                id = 4,
                name = "Pull-ups",
                primaryMuscle = MuscleGroup.BACK,
                secondaryMuscles = listOf(MuscleGroup.BICEPS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BODYWEIGHT
            ),
            Exercise(
                id = 5,
                name = "Overhead Press",
                primaryMuscle = MuscleGroup.SHOULDERS,
                secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.CORE),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL
            ),
            Exercise(
                id = 6,
                name = "Bicep Curls",
                primaryMuscle = MuscleGroup.BICEPS,
                secondaryMuscles = emptyList(),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.DUMBBELL
            )
        )
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            isSearchActive = query.isNotEmpty()
        )
        
        if (query.isEmpty()) {
            loadExercisesSafely()
        } else {
            searchExercises(query)
        }
    }

    private fun searchExercises(query: String) {
        viewModelScope.launch {
            try {
                exerciseRepository.searchExercises(query)
                    .catch { 
                        // Fallback to filtering default exercises
                        val filtered = getDefaultExercises().filter { 
                            it.name.contains(query, ignoreCase = true) 
                        }
                        _uiState.value = _uiState.value.copy(exercises = filtered, isLoading = false)
                    }
                    .collect { exercises ->
                        _uiState.value = _uiState.value.copy(exercises = exercises, isLoading = false)
                    }
            } catch (e: Exception) {
                // Fallback search
                val filtered = getDefaultExercises().filter { 
                    it.name.contains(query, ignoreCase = true) 
                }
                _uiState.value = _uiState.value.copy(exercises = filtered, isLoading = false)
            }
        }
    }

    fun selectMuscleGroup(muscleGroup: MuscleGroup?) {
        _uiState.value = _uiState.value.copy(selectedMuscleGroup = muscleGroup)
        loadFilteredExercises()
    }

    fun selectEquipment(equipment: Equipment?) {
        _uiState.value = _uiState.value.copy(selectedEquipment = equipment)
        loadFilteredExercises()
    }

    fun selectCategory(category: ExerciseCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        loadFilteredExercises()
    }

    private fun loadFilteredExercises() {
        if (_uiState.value.searchQuery.isNotEmpty()) return // Don't filter while searching
        
        viewModelScope.launch {
            try {
                exerciseRepository.getFilteredExercises(
                    muscle = _uiState.value.selectedMuscleGroup,
                    equipment = _uiState.value.selectedEquipment,
                    category = _uiState.value.selectedCategory
                )
                .catch { 
                    // Fallback to manual filtering
                    val filtered = filterDefaultExercises()
                    _uiState.value = _uiState.value.copy(exercises = filtered, isLoading = false)
                }
                .collect { exercises ->
                    _uiState.value = _uiState.value.copy(exercises = exercises, isLoading = false)
                }
            } catch (e: Exception) {
                val filtered = filterDefaultExercises()
                _uiState.value = _uiState.value.copy(exercises = filtered, isLoading = false)
            }
        }
    }

    private fun filterDefaultExercises(): List<Exercise> {
        val state = _uiState.value
        return getDefaultExercises().filter { exercise ->
            (state.selectedMuscleGroup == null || exercise.primaryMuscle == state.selectedMuscleGroup) &&
            (state.selectedEquipment == null || exercise.equipment == state.selectedEquipment) &&
            (state.selectedCategory == null || exercise.category == state.selectedCategory)
        }
    }

    fun clearAllFilters() {
        _uiState.value = _uiState.value.copy(
            searchQuery = "",
            selectedMuscleGroup = null,
            selectedEquipment = null,
            selectedCategory = null,
            isSearchActive = false
        )
        loadExercisesSafely()
    }

    fun toggleSearch() {
        val newSearchActive = !_uiState.value.isSearchActive
        _uiState.value = _uiState.value.copy(isSearchActive = newSearchActive)
        if (!newSearchActive) {
            _uiState.value = _uiState.value.copy(searchQuery = "")
            loadExercisesSafely()
        }
    }
}