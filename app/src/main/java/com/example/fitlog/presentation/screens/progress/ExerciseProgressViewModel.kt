package com.example.fitlog.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.DateRangeFilter
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.model.RepRangeRecord
import com.example.fitlog.domain.model.WeightProgressPoint
import com.example.fitlog.domain.model.VolumeProgressPoint
import com.example.fitlog.domain.repository.ExerciseRepository
import com.example.fitlog.domain.repository.ProgressRepository
import com.example.fitlog.domain.repository.RepRangeRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseProgressUiState(
    val isLoading: Boolean = true,
    val exercises: List<Exercise> = emptyList(),
    val selectedExercise: Exercise? = null,
    val selectedDateRange: DateRangeFilter = DateRangeFilter.LAST_90_DAYS,
    val weightProgress: List<WeightProgressPoint> = emptyList(),
    val volumeProgress: List<VolumeProgressPoint> = emptyList(),
    val repRangeRecords: List<RepRangeRecord> = emptyList(),
    val bestWeight: Float? = null,
    val bestVolume: Float? = null,
    val totalWorkouts: Int = 0,
    val isExercisePickerVisible: Boolean = false,
    val searchQuery: String = "",
    val error: String? = null
)

@HiltViewModel
class ExerciseProgressViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val progressRepository: ProgressRepository,
    private val repRangeRecordRepository: RepRangeRecordRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseProgressUiState())
    val uiState: StateFlow<ExerciseProgressUiState> = _uiState.asStateFlow()

    init {
        loadExercises()
    }

    private fun loadExercises() {
        viewModelScope.launch {
            try {
                val exercises = exerciseRepository.getAllExercises().first()
                _uiState.value = _uiState.value.copy(
                    exercises = exercises,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load exercises"
                )
            }
        }
    }

    fun selectExercise(exercise: Exercise) {
        _uiState.value = _uiState.value.copy(
            selectedExercise = exercise,
            isExercisePickerVisible = false,
            isLoading = true
        )
        loadExerciseProgress(exercise.id)
    }

    fun selectExerciseById(exerciseId: Int) {
        viewModelScope.launch {
            try {
                val exercise = exerciseRepository.getExerciseById(exerciseId)
                if (exercise != null) {
                    selectExercise(exercise)
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message ?: "Failed to load exercise"
                )
            }
        }
    }

    private fun loadExerciseProgress(exerciseId: Int) {
        viewModelScope.launch {
            try {
                val dateRange = _uiState.value.selectedDateRange
                val (startDate, endDate) = progressRepository.getDateRangeForFilter(dateRange)

                val weightProgress = progressRepository.getWeightProgressForExercise(
                    exerciseId, startDate, endDate
                )
                val volumeProgress = progressRepository.getVolumeProgressForExercise(
                    exerciseId, startDate, endDate
                )
                val repRangeRecords = repRangeRecordRepository.getByExerciseId(exerciseId)

                val bestWeight = weightProgress.maxOfOrNull { it.maxWeight }
                val bestVolume = volumeProgress.maxOfOrNull { it.totalVolume }

                _uiState.value = _uiState.value.copy(
                    weightProgress = weightProgress,
                    volumeProgress = volumeProgress,
                    repRangeRecords = repRangeRecords,
                    bestWeight = bestWeight,
                    bestVolume = bestVolume,
                    totalWorkouts = weightProgress.size,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load exercise progress"
                )
            }
        }
    }

    fun selectDateRange(filter: DateRangeFilter) {
        if (filter != _uiState.value.selectedDateRange) {
            _uiState.value = _uiState.value.copy(
                selectedDateRange = filter,
                isLoading = true
            )
            _uiState.value.selectedExercise?.let { exercise ->
                loadExerciseProgress(exercise.id)
            }
        }
    }

    fun showExercisePicker() {
        _uiState.value = _uiState.value.copy(isExercisePickerVisible = true)
    }

    fun hideExercisePicker() {
        _uiState.value = _uiState.value.copy(isExercisePickerVisible = false)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun getFilteredExercises(): List<Exercise> {
        val query = _uiState.value.searchQuery.lowercase()
        return if (query.isEmpty()) {
            _uiState.value.exercises
        } else {
            _uiState.value.exercises.filter { exercise ->
                exercise.name.lowercase().contains(query) ||
                exercise.primaryMuscle.displayName.lowercase().contains(query)
            }
        }
    }
}
