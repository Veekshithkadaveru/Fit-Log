package com.example.fitlog.presentation.screens.workout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitlog.presentation.navigation.FitLogDestinations
import com.example.fitlog.presentation.screens.workout.components.ExerciseSection
import com.example.fitlog.presentation.screens.workout.components.WorkoutControls
import com.example.fitlog.presentation.screens.workout.components.WorkoutHeader
import com.example.fitlog.presentation.viewmodel.ActiveWorkoutViewModel
import com.example.fitlog.presentation.viewmodel.WorkoutNavigationEvent

/**
 * Main screen for active workout tracking
 * Shows live timer, exercises, and workout controls
 */
@Composable
fun ActiveWorkoutScreen(
    navController: NavController,
    routineId: Int? = null,
    viewModel: ActiveWorkoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Auto-start workout if routineId provided and no active workout
    LaunchedEffect(routineId, uiState.isLoading, uiState.isWorkoutActive) {
        if (routineId != null && !uiState.isWorkoutActive && !uiState.isLoading) {
            viewModel.startWorkout(routineId)
        }
    }

    // Handle navigation events
    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is WorkoutNavigationEvent.NavigateToHistory -> {
                    navController.navigate(FitLogDestinations.HISTORY) {
                        popUpTo(FitLogDestinations.ACTIVE_WORKOUT) {
                            inclusive = true
                        }
                    }
                }
                is WorkoutNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                is WorkoutNavigationEvent.NavigateToExerciseDetail -> {
                    // Will be implemented in future PRs
                }
            }
        }
    }

    // Show error messages
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            if (uiState.isWorkoutActive) {
                WorkoutHeader(
                    workoutDuration = uiState.workoutDuration,
                    routineName = uiState.routineName,
                    exerciseCount = uiState.exerciseCount,
                    totalSets = uiState.totalSetsCount,
                    onBackClick = { navController.popBackStack() }
                )
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                !uiState.isWorkoutActive -> {
                    EmptyWorkoutState(
                        onQuickStart = { viewModel.startWorkout() },
                        onStartFromRoutine = {
                            navController.navigate(FitLogDestinations.ROUTINE_LIST)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    ActiveWorkoutContent(
                        uiState = uiState,
                        onEndWorkout = { viewModel.endWorkout() },
                        onDiscardWorkout = { viewModel.discardWorkout() },
                        onAddSet = { exerciseId -> viewModel.addSet(exerciseId, autoFill = false) },
                        onAddSetWithAutoFill = { exerciseId -> viewModel.addSet(exerciseId, autoFill = true) },
                        onUpdateSetWeight = { setId, weight ->
                            viewModel.updateSet(setId, weight = weight.toFloatOrNull())
                        },
                        onUpdateSetReps = { setId, reps ->
                            viewModel.updateSet(setId, reps = reps.toIntOrNull())
                        },
                        onToggleSetCompleted = { setId -> viewModel.toggleSetCompleted(setId) },
                        onDeleteSet = { setId -> viewModel.deleteSet(setId) },
                        onDuplicateSet = { setId -> viewModel.duplicateSet(setId) },
                        
                    )
                }
            }
        }
    }
}

/**
 * Content shown when workout is active
 */
@Composable
private fun ActiveWorkoutContent(
    uiState: com.example.fitlog.presentation.viewmodel.ActiveWorkoutUiState,
    onEndWorkout: () -> Unit,
    onDiscardWorkout: () -> Unit,
    onAddSet: (exerciseId: Int) -> Unit,
    onAddSetWithAutoFill: (exerciseId: Int) -> Unit,
    onUpdateSetWeight: (setId: Int, weight: String) -> Unit,
    onUpdateSetReps: (setId: Int, reps: String) -> Unit,
    onToggleSetCompleted: (setId: Int) -> Unit,
    onDeleteSet: (setId: Int) -> Unit,
    onDuplicateSet: (setId: Int) -> Unit,
    
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Exercise List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.workoutExercises.isEmpty()) {
                item {
                    NoExercisesPlaceholder()
                }
            } else {
                items(
                    items = uiState.workoutExercises,
                    key = { it.exercise.id }
                ) { exercise ->
                    ExerciseSection(
                        exercise = exercise,
                        sets = exercise.sets,
                        onWeightChange = onUpdateSetWeight,
                        onRepsChange = onUpdateSetReps,
                        onSetCompleted = onToggleSetCompleted,
                        onDeleteSet = onDeleteSet,
                        onDuplicateSet = onDuplicateSet,
                        
                        onAddSet = { onAddSet(exercise.exercise.id) },
                        onAddSetWithAutoFill = { onAddSetWithAutoFill(exercise.exercise.id) }
                    )
                }
            }
        }

        // Workout Controls at bottom
        WorkoutControls(
            onEndWorkout = onEndWorkout,
            onDiscardWorkout = onDiscardWorkout
        )
    }
}

/**
 * Empty state shown when no workout is active
 */
@Composable
private fun EmptyWorkoutState(
    onQuickStart: () -> Unit,
    onStartFromRoutine: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "No Active Workout",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Start a workout to begin tracking your progress",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onQuickStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null
                )
                Text(
                    text = "Quick Start",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Button(
                onClick = onStartFromRoutine,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null
                )
                Text(
                    text = "Start from Routine",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * Placeholder shown when workout has no exercises
 */
@Composable
private fun NoExercisesPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "No exercises yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = "Adding exercises will be available in PR #2",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
