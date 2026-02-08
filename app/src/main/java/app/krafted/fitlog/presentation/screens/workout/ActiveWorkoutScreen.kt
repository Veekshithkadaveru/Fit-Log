package app.krafted.fitlog.presentation.screens.workout

import app.krafted.fitlog.presentation.viewmodel.WorkoutNavigationEvent
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.krafted.fitlog.presentation.navigation.FitLogDestinations
import app.krafted.fitlog.presentation.navigation.FitLogRoutes
import app.krafted.fitlog.presentation.screens.workout.components.EmptyWorkoutState
import app.krafted.fitlog.presentation.screens.workout.components.ExerciseSection
import app.krafted.fitlog.presentation.screens.workout.components.MusclesTrainedPanel
import app.krafted.fitlog.presentation.screens.workout.components.PRToastNotification
import app.krafted.fitlog.presentation.screens.workout.components.RestTimerOverlay
import app.krafted.fitlog.presentation.screens.workout.components.WorkoutControls
import app.krafted.fitlog.presentation.screens.workout.components.WorkoutHeader
import app.krafted.fitlog.presentation.screens.workout.components.WorkoutSummaryDialog
import app.krafted.fitlog.presentation.viewmodel.ActiveWorkoutViewModel
import app.krafted.fitlog.presentation.viewmodel.PREvent
import app.krafted.fitlog.presentation.viewmodel.RestTimerState
import app.krafted.fitlog.presentation.viewmodel.ActiveWorkoutUiState


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
    val weightUnit by viewModel.weightUnit.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var currentPREvent by remember { mutableStateOf<PREvent?>(null) }

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

    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.clearError()
        }
    }

    // Listen for PR events and show celebration
    LaunchedEffect(Unit) {
        viewModel.prEvent.collect { prEvent ->
            currentPREvent = prEvent
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
                    prCount = uiState.sessionPRCount,
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
                        onLogCardio = {
                            navController.navigate(FitLogDestinations.CARDIO_LOG)
                        },
                        onViewAnalytics = {
                            navController.navigate(FitLogDestinations.MUSCLE_ANALYTICS)
                        },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    val actions = rememberActiveWorkoutActions(
                        viewModel = viewModel,
                        onNavigateToAddExercise = {
                            navController.navigate(FitLogDestinations.ACTIVE_WORKOUT_EXERCISE_PICKER)
                        }
                    )
                    
                    ActiveWorkoutContent(
                        uiState = uiState,
                        actions = actions,
                        weightUnitLabel = weightUnit.label
                    )
                }
            }

            // PR Toast Notification (positioned at top)
            currentPREvent?.let { prEvent ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 8.dp)
                ) {
                    PRToastNotification(
                        prEvent = prEvent,
                        onDismiss = { currentPREvent = null },
                        weightUnitLabel = weightUnit.label
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

    // Workout Summary Dialog (shown after workout ends if there are PRs)
    if (uiState.showWorkoutSummary) {
        WorkoutSummaryDialog(
            sessionPRs = uiState.sessionPRs,
            workoutDuration = uiState.workoutDuration,
            totalSets = uiState.totalSetsCount,
            onDismiss = { viewModel.dismissWorkoutSummary() },
            weightUnitLabel = weightUnit.label
        )
    }
}

/**
 * Content shown when workout is active
 */
@Composable
private fun ActiveWorkoutContent(
    uiState: ActiveWorkoutUiState,
    actions: ActiveWorkoutActions,
    weightUnitLabel: String = "kg",
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
                        NoExercisesPlaceholder(onAddExercise = actions.onAddExercise)
                    }
                } else {
                    items(
                        items = uiState.workoutExercises,
                        key = { it.exercise.id }
                    ) { exercise ->
                        ExerciseSection(
                            exercise = exercise,
                            sets = exercise.sets,
                            onWeightChange = actions.onUpdateSetWeight,
                            onRepsChange = actions.onUpdateSetReps,
                            onSetCompleted = actions.onToggleSetCompleted,
                            onDeleteSet = actions.onDeleteSet,
                            onDuplicateSet = actions.onDuplicateSet,
                            onAddSet = { actions.onAddSet(exercise.exercise.id) },
                            onAddSetWithAutoFill = { actions.onAddSetWithAutoFill(exercise.exercise.id) },
                            onExerciseClick = { actions.onExerciseClick(exercise.exercise.id) },
                            weightUnitLabel = weightUnitLabel
                        )
                    }
                }
            }

            // Workout Controls at bottom
            WorkoutControls(
                onEndWorkout = actions.onEndWorkout,
                onDiscardWorkout = actions.onDiscardWorkout
            )
        }

        // Floating Action Button to add exercises (only show when there are existing exercises)
        if (uiState.workoutExercises.isNotEmpty()) {
            FloatingActionButton(
                onClick = actions.onAddExercise,
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
