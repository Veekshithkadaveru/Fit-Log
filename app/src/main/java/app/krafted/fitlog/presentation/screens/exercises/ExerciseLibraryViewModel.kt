package app.krafted.fitlog.presentation.screens.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fitlog.domain.model.Equipment
import app.krafted.fitlog.domain.model.Exercise
import app.krafted.fitlog.domain.model.ExerciseCategory
import app.krafted.fitlog.domain.model.MuscleGroup
import app.krafted.fitlog.domain.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseLibraryUiState(
    val exercises: List<Exercise> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
    val selectedMuscleGroup: MuscleGroup? = null,
    val selectedEquipment: Equipment? = null,
    val selectedCategory: ExerciseCategory? = null,
    val availableMuscleGroups: List<MuscleGroup> = emptyList(),
    val availableEquipment: List<Equipment> = emptyList(),
    val isSearchActive: Boolean = false
)

@HiltViewModel
class ExerciseLibraryViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseLibraryUiState())
    val uiState: StateFlow<ExerciseLibraryUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    private val _selectedMuscleGroup = MutableStateFlow<MuscleGroup?>(null)
    private val _selectedEquipment = MutableStateFlow<Equipment?>(null)
    private val _selectedCategory = MutableStateFlow<ExerciseCategory?>(null)

    init {
        observeExercises()
        loadAvailableFilters()
    }

    private fun observeExercises() {
        // Start with loading all exercises
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                _uiState.value = _uiState.value.copy(
                    exercises = exercises,
                    isLoading = false
                )
            }
        }

        // Observe search query changes
        viewModelScope.launch {
            _searchQuery.collect { query ->
                _uiState.value = _uiState.value.copy(
                    searchQuery = query,
                    isSearchActive = query.isNotEmpty()
                )
                if (query.isNotEmpty()) {
                    searchExercises(query)
                } else {
                    loadFilteredExercises()
                }
            }
        }

        // Observe filter changes
        viewModelScope.launch {
            combine(
                _selectedMuscleGroup,
                _selectedEquipment,
                _selectedCategory
            ) { muscle, equipment, category ->
                Triple(muscle, equipment, category)
            }.collect { (muscle, equipment, category) ->
                _uiState.value = _uiState.value.copy(
                    selectedMuscleGroup = muscle,
                    selectedEquipment = equipment,
                    selectedCategory = category
                )
                if (_uiState.value.searchQuery.isEmpty()) {
                    loadFilteredExercises()
                }
            }
        }
    }

    private fun searchExercises(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            exerciseRepository.searchExercises(query).collect { exercises ->
                _uiState.value = _uiState.value.copy(
                    exercises = exercises,
                    isLoading = false
                )
            }
        }
    }

    private fun loadFilteredExercises() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            exerciseRepository.getFilteredExercises(
                muscle = _uiState.value.selectedMuscleGroup,
                equipment = _uiState.value.selectedEquipment,
                category = _uiState.value.selectedCategory
            ).collect { exercises ->
                _uiState.value = _uiState.value.copy(
                    exercises = exercises,
                    isLoading = false
                )
            }
        }
    }

    private fun loadAvailableFilters() {
        viewModelScope.launch {
            combine(
                exerciseRepository.getAllMuscleGroups(),
                exerciseRepository.getAllEquipmentTypes()
            ) { muscleGroups, equipment ->
                muscleGroups to equipment
            }.collect { (muscleGroups, equipment) ->
                _uiState.value = _uiState.value.copy(
                    availableMuscleGroups = muscleGroups,
                    availableEquipment = equipment
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectMuscleGroup(muscleGroup: MuscleGroup?) {
        _selectedMuscleGroup.value = muscleGroup
    }

    fun selectEquipment(equipment: Equipment?) {
        _selectedEquipment.value = equipment
    }

    fun selectCategory(category: ExerciseCategory?) {
        _selectedCategory.value = category
    }

    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedMuscleGroup.value = null
        _selectedEquipment.value = null
        _selectedCategory.value = null
    }

    fun toggleSearch() {
        val newSearchActive = !_uiState.value.isSearchActive
        _uiState.value = _uiState.value.copy(isSearchActive = newSearchActive)
        if (!newSearchActive) {
            _searchQuery.value = ""
        }
    }

    private data class FilterParams(
        val searchQuery: String,
        val muscleGroup: MuscleGroup?,
        val equipment: Equipment?,
        val category: ExerciseCategory?
    )
}