package com.example.fitlog.presentation.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.model.Workout
import com.example.fitlog.domain.model.WorkoutSet
import com.example.fitlog.domain.repository.ExerciseRepository
import com.example.fitlog.domain.repository.RoutineRepository
import com.example.fitlog.domain.repository.WorkoutRepository
import com.example.fitlog.presentation.navigation.FitLogDestinations
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Workout Detail screen
 */
data class WorkoutDetailUiState(
    val isLoading: Boolean = true,
    val workout: Workout? = null,
    val routineName: String? = null,
    val exerciseGroups: List<ExerciseWithSets> = emptyList(),
    val error: String? = null,
    val isEditMode: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    // Track edited sets: Map<setId, EditedSetData>
    val editedSets: Map<Int, EditedSetData> = emptyMap()
)

/**
 * Data class to track edited set values
 */
data class EditedSetData(
    val weight: Float,
    val reps: Int
)

/**
 * Represents an exercise with its sets grouped together for display
 */
data class ExerciseWithSets(
    val exercise: Exercise,
    val sets: List<WorkoutSet>
) {
    val completedSets: Int
        get() = sets.count { it.isCompleted }

    val totalVolume: Float
        get() = sets.filter { it.isCompleted }.sumOf { (it.weight * it.reps).toDouble() }.toFloat()

    val hasPR: Boolean
        get() = sets.any { it.isPR }

    val prCount: Int
        get() = sets.count { it.isPR }
}

/**
 * One-time events for navigation
 */
sealed class WorkoutDetailEvent {
    data object WorkoutDeleted : WorkoutDetailEvent()
    data object SaveSuccess : WorkoutDetailEvent()
    data class SaveError(val message: String) : WorkoutDetailEvent()
}

/**
 * ViewModel for the Workout Detail screen
 * Displays detailed breakdown of a completed workout with edit/delete capabilities
 */
@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val workoutId: Int = savedStateHandle.get<Int>(FitLogDestinations.Args.WORKOUT_ID) ?: 0

    private val _uiState = MutableStateFlow(WorkoutDetailUiState())
    val uiState: StateFlow<WorkoutDetailUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<WorkoutDetailEvent>()
    val events: SharedFlow<WorkoutDetailEvent> = _events.asSharedFlow()

    init {
        loadWorkoutDetails()
    }

    private fun loadWorkoutDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val workout = workoutRepository.getWorkoutById(workoutId)

                if (workout == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Workout not found"
                    )
                    return@launch
                }

                val routineName = workout.routineId?.let { routineId ->
                    routineRepository.getRoutineById(routineId)?.name
                }

                val exerciseGroups = groupSetsByExercise(workout.sets)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    workout = workout,
                    routineName = routineName,
                    exerciseGroups = exerciseGroups,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load workout details"
                )
            }
        }
    }

    private suspend fun groupSetsByExercise(sets: List<WorkoutSet>): List<ExerciseWithSets> {
        val groupedSets = sets.groupBy { it.exerciseId }

        return groupedSets.mapNotNull { (exerciseId, exerciseSets) ->
            val exercise = exerciseRepository.getExerciseById(exerciseId)
            if (exercise != null) {
                ExerciseWithSets(
                    exercise = exercise,
                    sets = exerciseSets.sortedBy { it.setOrder }
                )
            } else {
                null
            }
        }
    }

    /**
     * Get display name for the workout type
     */
    fun getWorkoutTypeName(): String {
        val workout = _uiState.value.workout ?: return "Workout"
        return when {
            workout.isCardio -> "Cardio Session"
            _uiState.value.routineName != null -> _uiState.value.routineName!!
            else -> "Quick Workout"
        }
    }

    /**
     * Refresh workout details
     */
    fun refresh() {
        loadWorkoutDetails()
    }

    // ==================== Edit Mode ====================

    /**
     * Enter edit mode
     */
    fun enterEditMode() {
        _uiState.value = _uiState.value.copy(
            isEditMode = true,
            editedSets = emptyMap()
        )
    }

    /**
     * Cancel edit mode and discard changes
     */
    fun cancelEditMode() {
        _uiState.value = _uiState.value.copy(
            isEditMode = false,
            editedSets = emptyMap()
        )
    }

    /**
     * Update a set's weight value (while editing)
     */
    fun updateSetWeight(setId: Int, weight: Float) {
        val currentSet = findSetById(setId) ?: return
        val currentEdit = _uiState.value.editedSets[setId] ?: EditedSetData(
            weight = currentSet.weight,
            reps = currentSet.reps
        )

        _uiState.value = _uiState.value.copy(
            editedSets = _uiState.value.editedSets + (setId to currentEdit.copy(weight = weight))
        )
    }

    /**
     * Update a set's reps value (while editing)
     */
    fun updateSetReps(setId: Int, reps: Int) {
        val currentSet = findSetById(setId) ?: return
        val currentEdit = _uiState.value.editedSets[setId] ?: EditedSetData(
            weight = currentSet.weight,
            reps = currentSet.reps
        )

        _uiState.value = _uiState.value.copy(
            editedSets = _uiState.value.editedSets + (setId to currentEdit.copy(reps = reps))
        )
    }

    /**
     * Get the current (possibly edited) weight for a set
     */
    fun getSetWeight(setId: Int): Float {
        return _uiState.value.editedSets[setId]?.weight
            ?: findSetById(setId)?.weight
            ?: 0f
    }

    /**
     * Get the current (possibly edited) reps for a set
     */
    fun getSetReps(setId: Int): Int {
        return _uiState.value.editedSets[setId]?.reps
            ?: findSetById(setId)?.reps
            ?: 0
    }

    private fun findSetById(setId: Int): WorkoutSet? {
        return _uiState.value.exerciseGroups
            .flatMap { it.sets }
            .find { it.id == setId }
    }

    /**
     * Save all edited sets
     */
    fun saveChanges() {
        val editedSets = _uiState.value.editedSets
        if (editedSets.isEmpty()) {
            cancelEditMode()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)

            try {
                // Update each edited set in the repository
                editedSets.forEach { (setId, editData) ->
                    val originalSet = findSetById(setId)
                    if (originalSet != null) {
                        val updatedSet = originalSet.copy(
                            weight = editData.weight,
                            reps = editData.reps
                        )
                        workoutRepository.updateSet(updatedSet)
                    }
                }

                // Reload workout details to reflect changes
                loadWorkoutDetails()

                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    isEditMode = false,
                    editedSets = emptyMap()
                )

                _events.emit(WorkoutDetailEvent.SaveSuccess)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isSaving = false)
                _events.emit(WorkoutDetailEvent.SaveError(e.message ?: "Failed to save changes"))
            }
        }
    }

    // ==================== Delete Workout ====================

    /**
     * Show delete confirmation dialog
     */
    fun showDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = true)
    }

    /**
     * Hide delete confirmation dialog
     */
    fun hideDeleteConfirmation() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmation = false)
    }

    /**
     * Delete the workout and all associated data
     */
    fun deleteWorkout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isDeleting = true,
                showDeleteConfirmation = false
            )

            try {
                workoutRepository.deleteWorkoutById(workoutId)
                _events.emit(WorkoutDetailEvent.WorkoutDeleted)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeleting = false,
                    error = e.message ?: "Failed to delete workout"
                )
            }
        }
    }
}
