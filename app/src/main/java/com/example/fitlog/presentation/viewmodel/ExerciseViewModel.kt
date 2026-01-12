package com.example.fitlog.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.repository.RoutineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: RoutineRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedMuscle = MutableStateFlow<com.example.fitlog.domain.model.MuscleGroup?>(null)
    val selectedMuscle: StateFlow<com.example.fitlog.domain.model.MuscleGroup?> = _selectedMuscle

    private val allExercises = repository.getAllExercises()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val filteredExercises = combine(
        allExercises,
        _searchQuery,
        _selectedMuscle
    ) { exercises, query, muscle ->
        exercises.filter { exercise ->
            val matchesQuery = exercise.name.contains(query, ignoreCase = true)
            val matchesMuscle = muscle == null || exercise.primaryMuscle == muscle
            matchesQuery && matchesMuscle
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Distinct muscles for filter chips, derived from all exercises to avoid extra DB calls
    val muscleGroups = allExercises.combine(MutableStateFlow(Unit)) { exercises, _ ->
        exercises.map { it.primaryMuscle }.distinct().sortedBy { it.displayName }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onMuscleSelect(muscle: com.example.fitlog.domain.model.MuscleGroup?) {
        if (_selectedMuscle.value == muscle) {
            _selectedMuscle.value = null // Toggle off
        } else {
            _selectedMuscle.value = muscle
        }
    }
}
