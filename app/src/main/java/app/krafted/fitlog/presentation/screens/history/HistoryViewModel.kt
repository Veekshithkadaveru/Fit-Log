package app.krafted.fitlog.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fitlog.domain.model.Workout
import app.krafted.fitlog.domain.repository.RoutineRepository
import app.krafted.fitlog.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Filter types for history list
 */
enum class HistoryFilterType {
    ALL,
    STRENGTH,
    CARDIO
}

/**
 * UI state for the History screen
 */
data class HistoryUiState(
    val isLoading: Boolean = true,
    val workouts: List<Workout> = emptyList(),
    val routineNames: Map<Int, String> = emptyMap(),
    val error: String? = null,
    val searchQuery: String = "",
    val currentFilter: HistoryFilterType = HistoryFilterType.ALL
)

/**
 * ViewModel for the History screen
 * Manages the list of completed workouts and their associated routine names
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val routineRepository: RoutineRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    // Keep track of all loaded workouts to apply filters locally
    private var allWorkouts: List<Workout> = emptyList()

    init {
        loadWorkouts()
    }

    private fun loadWorkouts() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            workoutRepository.getAllWorkouts()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load workouts"
                    )
                }
                .collect { workouts ->
                    // Filter out in-progress workouts (only show completed ones)
                    allWorkouts = workouts.filter { !it.isInProgress }

                    // Get unique routine IDs and fetch their names
                    val routineIds = allWorkouts.mapNotNull { it.routineId }.distinct()
                    val routineNames = _uiState.value.routineNames.toMutableMap()

                    // Only fetch missing routine names
                    val missingRoutineIds = routineIds.filter { !routineNames.containsKey(it) }
                    
                    missingRoutineIds.forEach { routineId ->
                        try {
                            val routine = routineRepository.getRoutineById(routineId)
                            routine?.let {
                                routineNames[routineId] = it.name
                            }
                        } catch (e: Exception) {
                            // If we can't get the routine name, just skip it
                        }
                    }

                    _uiState.value = _uiState.value.copy(
                        routineNames = routineNames,
                        isLoading = false,
                        error = null
                    )
                    
                    // Apply current filters to updates
                    applyFilters()
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onFilterChange(filter: HistoryFilterType) {
        _uiState.value = _uiState.value.copy(currentFilter = filter)
        applyFilters()
    }

    private fun applyFilters() {
        val query = _uiState.value.searchQuery.trim().lowercase()
        val filter = _uiState.value.currentFilter

        val filteredList = allWorkouts.filter { workout ->
            // 1. Apply Type Filter
            val matchesType = when (filter) {
                HistoryFilterType.ALL -> true
                HistoryFilterType.STRENGTH -> !workout.isCardio
                HistoryFilterType.CARDIO -> workout.isCardio
            }

            // 2. Apply Search Filter
            val matchesSearch = if (query.isEmpty()) {
                true
            } else {
                val routineName = getRoutineNameForWorkout(workout).lowercase()
                val notes = workout.notes?.lowercase() ?: ""
                routineName.contains(query) || notes.contains(query)
            }

            matchesType && matchesSearch
        }

        _uiState.value = _uiState.value.copy(workouts = filteredList)
    }

    /**
     * Get the routine name for a workout, or a default label
     */
    fun getRoutineNameForWorkout(workout: Workout): String {
        return when {
            workout.isCardio -> "Cardio Session"
            workout.routineId != null -> _uiState.value.routineNames[workout.routineId] ?: "Custom Workout"
            else -> "Quick Workout"
        }
    }

    /**
     * Refresh the workout list
     */
    fun refresh() {
        loadWorkouts()
    }
}
