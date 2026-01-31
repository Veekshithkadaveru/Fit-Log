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
                name = "Barbell Bench Press",
                primaryMuscle = MuscleGroup.CHEST,
                secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_barbell_bench_press"
            ),
            Exercise(
                id = 2,
                name = "Squats",
                primaryMuscle = MuscleGroup.LEGS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_squats"
            ),
            Exercise(
                id = 3,
                name = "Deadlift",
                primaryMuscle = MuscleGroup.BACK,
                secondaryMuscles = listOf(MuscleGroup.LEGS, MuscleGroup.GLUTES),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_deadlift"
            ),
            Exercise(
                id = 4,
                name = "Pull-Ups",
                primaryMuscle = MuscleGroup.BACK,
                secondaryMuscles = listOf(MuscleGroup.BICEPS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BODYWEIGHT,
                thumbnailRes = "ic_pull_ups"
            ),
            Exercise(
                id = 5,
                name = "Overhead Press",
                primaryMuscle = MuscleGroup.SHOULDERS,
                secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.CORE),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_overhead_press"
            ),
            Exercise(
                id = 6,
                name = "Bicep Curls",
                primaryMuscle = MuscleGroup.BICEPS,
                secondaryMuscles = emptyList(),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.DUMBBELL,
                thumbnailRes = "ic_dumbbell_curls"
            ),
            Exercise(
                id = 55,
                name = "Front Squat",
                primaryMuscle = MuscleGroup.QUADRICEPS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_front_squat"
            ),
            Exercise(
                id = 56,
                name = "Skull Crushers",
                primaryMuscle = MuscleGroup.TRICEPS,
                secondaryMuscles = emptyList(),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.EZ_BAR,
                thumbnailRes = "ic_skull_crushers"
            ),
            Exercise(
                id = 57,
                name = "Concentration Curls",
                primaryMuscle = MuscleGroup.BICEPS,
                secondaryMuscles = emptyList(),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.DUMBBELL,
                thumbnailRes = "ic_concentration_curls"
            ),
            Exercise(
                id = 58,
                name = "Kettlebell Swings",
                primaryMuscle = MuscleGroup.HAMSTRINGS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE, MuscleGroup.SHOULDERS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.KETTLEBELL,
                thumbnailRes = "ic_kettlebell_swings"
            ),
            Exercise(
                id = 59,
                name = "Box Jumps",
                primaryMuscle = MuscleGroup.QUADRICEPS,
                secondaryMuscles = listOf(MuscleGroup.CALVES, MuscleGroup.GLUTES),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.OTHER,
                thumbnailRes = "ic_box_jumps"
            ),
            Exercise(
                id = 60,
                name = "Burpees",
                primaryMuscle = MuscleGroup.CORE,
                secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.QUADRICEPS),
                category = ExerciseCategory.CARDIO,
                equipment = Equipment.BODYWEIGHT,
                thumbnailRes = "ic_burpees"
            ),
            Exercise(
                id = 61,
                name = "Farmer's Carry",
                primaryMuscle = MuscleGroup.FOREARMS,
                secondaryMuscles = listOf(MuscleGroup.TRAPS, MuscleGroup.CORE),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.DUMBBELL,
                thumbnailRes = "ic_farmers_carry"
            ),
            Exercise(
                id = 62,
                name = "Good Mornings",
                primaryMuscle = MuscleGroup.HAMSTRINGS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.BACK),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_good_mornings"
            ),
            Exercise(
                id = 63,
                name = "Dumbbell Pullover",
                primaryMuscle = MuscleGroup.LATS,
                secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.DUMBBELL,
                thumbnailRes = "ic_dumbbell_pullover"
            ),
            Exercise(
                id = 64,
                name = "Reverse Curls",
                primaryMuscle = MuscleGroup.FOREARMS,
                secondaryMuscles = listOf(MuscleGroup.BICEPS),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.EZ_BAR,
                thumbnailRes = "ic_reverse_curls"
            ),
            Exercise(
                id = 65,
                name = "Hack Squat",
                primaryMuscle = MuscleGroup.QUADRICEPS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.MACHINE,
                thumbnailRes = "ic_hack_squat"
            ),
            Exercise(
                id = 66,
                name = "Cable Crunches",
                primaryMuscle = MuscleGroup.CORE,
                secondaryMuscles = emptyList(),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.CABLE,
                thumbnailRes = "ic_cable_crunches"
            ),
            Exercise(
                id = 67,
                name = "Landmine Press",
                primaryMuscle = MuscleGroup.SHOULDERS,
                secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.CHEST),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_landmine_press"
            ),
            Exercise(
                id = 68,
                name = "Pec Deck Fly",
                primaryMuscle = MuscleGroup.CHEST,
                secondaryMuscles = listOf(MuscleGroup.SHOULDERS),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.MACHINE,
                thumbnailRes = "ic_pec_deck_fly"
            ),
            Exercise(
                id = 69,
                name = "Clean and Press",
                primaryMuscle = MuscleGroup.SHOULDERS,
                secondaryMuscles = listOf(MuscleGroup.LEGS, MuscleGroup.BACK, MuscleGroup.CORE),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_clean_and_press"
            ),
            Exercise(
                id = 70,
                name = "Battle Ropes",
                primaryMuscle = MuscleGroup.SHOULDERS,
                secondaryMuscles = listOf(MuscleGroup.CORE),
                category = ExerciseCategory.CARDIO,
                equipment = Equipment.OTHER,
                thumbnailRes = "ic_battle_ropes"
            ),
            Exercise(
                id = 71,
                name = "Trap Bar Deadlift",
                primaryMuscle = MuscleGroup.LEGS,
                secondaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.GLUTES, MuscleGroup.TRAPS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.OTHER,
                thumbnailRes = "ic_trap_bar_deadlift"
            ),
            Exercise(
                id = 72,
                name = "Wall Sit",
                primaryMuscle = MuscleGroup.QUADRICEPS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CORE),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.BODYWEIGHT,
                thumbnailRes = "ic_wall_sit"
            ),
            Exercise(
                id = 73,
                name = "Nordic Hamstring Curl",
                primaryMuscle = MuscleGroup.HAMSTRINGS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.BODYWEIGHT,
                thumbnailRes = "ic_nordic_hamstring_curl"
            ),
            Exercise(
                id = 74,
                name = "Jump Squats",
                primaryMuscle = MuscleGroup.QUADRICEPS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.CALVES),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BODYWEIGHT,
                thumbnailRes = "ic_jump_squats"
            ),

            Exercise(
                id = 8,
                name = "Decline Bench Press",
                primaryMuscle = MuscleGroup.CHEST,
                secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_decline_bench_press"
            ),
            Exercise(
                id = 16,
                name = "Face Pulls",
                primaryMuscle = MuscleGroup.REAR_DELTS,
                secondaryMuscles = listOf(MuscleGroup.BACK, MuscleGroup.TRAPS),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.CABLE,
                thumbnailRes = "ic_face_pulls"
            ),
            Exercise(
                id = 29,
                name = "Bulgarian Split Squats",
                primaryMuscle = MuscleGroup.QUADRICEPS,
                secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.DUMBBELL,
                thumbnailRes = "ic_bulgarian_split_squats"
            ),
            Exercise(
                id = 41,
                name = "Hammer Curls",
                primaryMuscle = MuscleGroup.BICEPS,
                secondaryMuscles = listOf(MuscleGroup.FOREARMS),
                category = ExerciseCategory.ISOLATION,
                equipment = Equipment.DUMBBELL,
                thumbnailRes = "ic_hammer_curls"
            ),
            Exercise(
                id = 42,
                name = "Close-Grip Bench Press",
                primaryMuscle = MuscleGroup.TRICEPS,
                secondaryMuscles = listOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL,
                thumbnailRes = "ic_close_grip_bench"
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