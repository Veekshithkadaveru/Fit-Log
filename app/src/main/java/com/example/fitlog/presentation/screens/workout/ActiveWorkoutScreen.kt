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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import com.example.fitlog.presentation.navigation.FitLogRoutes
import com.example.fitlog.presentation.screens.workout.components.ExerciseSection
import com.example.fitlog.presentation.screens.workout.components.RestTimerOverlay
import com.example.fitlog.presentation.screens.workout.components.WorkoutControls
import com.example.fitlog.presentation.screens.workout.components.WorkoutHeader
import com.example.fitlog.presentation.viewmodel.ActiveWorkoutViewModel
import com.example.fitlog.presentation.viewmodel.WorkoutNavigationEvent
import com.example.fitlog.presentation.screens.workout.components.MusclesTrainedPanel

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
    val restTimerState by viewModel.restTimerState.collectAsState()
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
                    navController.navigate(FitLogRoutes.exerciseDetail(event.exerciseId))
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
                        onViewAnalytics = {
                            navController.navigate(FitLogDestinations.MUSCLE_ANALYTICS)
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
                        onAddExercise = {
                            navController.navigate(FitLogDestinations.ACTIVE_WORKOUT_EXERCISE_PICKER)
                        },
                        onExerciseClick = { exerciseId ->
                            viewModel.navigateToExerciseDetail(exerciseId)
                        }
                    )
                }
            }

            // Rest Timer Overlay (on top of everything)
            RestTimerOverlay(
                isVisible = restTimerState.isActive,
                remainingSeconds = restTimerState.remainingSeconds,
                totalSeconds = restTimerState.totalSeconds,
                isPaused = restTimerState.isPaused,
                onPauseResume = { viewModel.pauseResumeRestTimer() },
                onSkip = { viewModel.skipRestTimer() },
                onAddTime = { seconds -> viewModel.addRestTime(seconds) },
                onClose = { viewModel.closeRestTimer() }
            )
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
    onAddExercise: () -> Unit,
    onExerciseClick: (exerciseId: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Exercise List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Muscles Trained Summary
                if (uiState.workoutExercises.isNotEmpty()) {
                    item {
                        MusclesTrainedPanel(
                            exercises = uiState.workoutExercises
                        )
                    }
                }

                if (uiState.workoutExercises.isEmpty()) {
                    item {
                        NoExercisesPlaceholder(onAddExercise = onAddExercise)
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
                            onAddSetWithAutoFill = { onAddSetWithAutoFill(exercise.exercise.id) },
                            onExerciseClick = { onExerciseClick(exercise.exercise.id) }
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

        // Floating Action Button to add exercises (only show when there are existing exercises)
        if (uiState.workoutExercises.isNotEmpty()) {
            FloatingActionButton(
                onClick = onAddExercise,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 80.dp, end = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Exercise"
                )
            }
        }
    }
}

/**
 * Empty state shown when no workout is active
 */
@Composable
private fun EmptyWorkoutState(
    onQuickStart: () -> Unit,
    onStartFromRoutine: () -> Unit,
    onViewAnalytics: () -> Unit,
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

            Button(
                onClick = onViewAnalytics,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors()
            ) {
                Icon(
                    imageVector = Icons.Default.Info, // Fallback or Assessment if available. Let's use Info for safety or Assessment.
                    contentDescription = null
                )
                Text(
                    text = "Muscle Analytics",
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
private fun NoExercisesPlaceholder(
    onAddExercise: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "No exercises yet",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Add exercises to start logging your workout",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onAddExercise,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Text(
                text = "Add Exercise",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
