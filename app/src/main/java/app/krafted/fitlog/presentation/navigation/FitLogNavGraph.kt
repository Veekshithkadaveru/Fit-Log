package app.krafted.fitlog.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import app.krafted.fitlog.presentation.screens.PlaceholderScreen
import app.krafted.fitlog.presentation.screens.exercises.ExerciseDetailScreen
import app.krafted.fitlog.presentation.screens.exercises.ExerciseLibraryScreen
import app.krafted.fitlog.presentation.screens.calendar.CalendarScreen
import app.krafted.fitlog.presentation.screens.history.HistoryScreen
import app.krafted.fitlog.presentation.screens.history.WorkoutDetailScreen
import app.krafted.fitlog.presentation.screens.bodyweight.BodyweightScreen
import app.krafted.fitlog.presentation.screens.cardio.CardioScreen
import app.krafted.fitlog.presentation.screens.progress.ExerciseProgressScreen
import app.krafted.fitlog.presentation.screens.progress.ProgressDashboardScreen
import app.krafted.fitlog.ui.theme.AnimationDuration

/**
 * Main navigation graph for the FitLog app with professional animations
 */
@Composable
fun FitLogNavGraph(
    navController: NavHostController,
    startDestination: String = FitLogDestinations.HOME,
    modifier: Modifier = Modifier
) {
    // Bottom nav destinations for special handling
    val bottomNavRoutes = setOf(
        FitLogDestinations.HOME,
        FitLogDestinations.WORKOUT,
        FitLogDestinations.EXERCISES,
        FitLogDestinations.HISTORY,
        FitLogDestinations.SETTINGS
    )

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        // Default enter/exit transitions for all screens
        enterTransition = {
            val isBottomNav = initialState.destination.route in bottomNavRoutes &&
                             targetState.destination.route in bottomNavRoutes
            if (isBottomNav) {
                // Bottom nav: crossfade with subtle scale
                fadeIn(
                    animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing)
                ) + scaleIn(
                    animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
                    initialScale = 0.94f
                )
            } else {
                // Detail screens: slide from right
                fadeIn(
                    animationSpec = tween(AnimationDuration.medium)
                ) + slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
                    initialOffset = { fullWidth -> fullWidth / 4 }
                )
            }
        },
        exitTransition = {
            val isBottomNav = initialState.destination.route in bottomNavRoutes &&
                             targetState.destination.route in bottomNavRoutes
            if (isBottomNav) {
                fadeOut(
                    animationSpec = tween(AnimationDuration.short, easing = FastOutSlowInEasing)
                ) + scaleOut(
                    animationSpec = tween(AnimationDuration.short, easing = FastOutSlowInEasing),
                    targetScale = 0.94f
                )
            } else {
                fadeOut(
                    animationSpec = tween(AnimationDuration.short)
                ) + slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Start,
                    animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
                    targetOffset = { fullWidth -> -fullWidth / 4 }
                )
            }
        },
        popEnterTransition = {
            // Coming back: slide from left
            fadeIn(
                animationSpec = tween(AnimationDuration.medium)
            ) + slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
                initialOffset = { fullWidth -> -fullWidth / 4 }
            )
        },
        popExitTransition = {
            // Going back: slide out to right
            fadeOut(
                animationSpec = tween(AnimationDuration.short)
            ) + slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
                targetOffset = { fullWidth -> fullWidth / 4 }
            )
        }
    ) {
        // Bottom Navigation Destinations
        composable(FitLogDestinations.HOME) {
            ProgressDashboardScreen(
                onNavigateToBodyweight = {
                    navController.navigate(FitLogDestinations.BODYWEIGHT_LOG)
                }
            )
        }

        composable(FitLogDestinations.WORKOUT) {
            app.krafted.fitlog.presentation.screens.workout.ActiveWorkoutScreen(
                navController = navController,
                routineId = null
            )
        }

        composable(FitLogDestinations.EXERCISES) {
            ExerciseLibraryScreen(
                onExerciseClick = { exerciseId ->
                    navController.navigate(FitLogRoutes.exerciseDetail(exerciseId))
                }
            )
        }

        composable(FitLogDestinations.HISTORY) {
            HistoryScreen(navController = navController)
        }

        composable(FitLogDestinations.SETTINGS) {
            PlaceholderScreen(title = "Settings", navController = navController)
        }

        // Detail Screens with slide animations
        composable(
            route = FitLogDestinations.EXERCISE_DETAIL,
            arguments = listOf(
                navArgument(FitLogDestinations.Args.EXERCISE_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt(FitLogDestinations.Args.EXERCISE_ID) ?: 0
            ExerciseDetailScreen(
                exerciseId = exerciseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = FitLogDestinations.WORKOUT_DETAIL,
            arguments = listOf(
                navArgument(FitLogDestinations.Args.WORKOUT_ID) {
                    type = NavType.IntType
                }
            )
        ) {
            WorkoutDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "active_workout?routineId={routineId}",
            arguments = listOf(
                navArgument("routineId") {
                    type = NavType.IntType
                    defaultValue = -1
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt("routineId")?.takeIf { it != -1 }
            app.krafted.fitlog.presentation.screens.workout.ActiveWorkoutScreen(
                navController = navController,
                routineId = routineId
            )
        }

        composable(FitLogDestinations.ROUTINE_LIST) {
            app.krafted.fitlog.presentation.screens.routines.RoutineListScreen(
                navController = navController,
                onRoutineClick = { routineId ->
                    navController.navigate(FitLogRoutes.routineEditor(routineId))
                }
            )
        }

        composable(
            route = FitLogDestinations.ROUTINE_EDITOR,
            arguments = listOf(
                navArgument(FitLogDestinations.Args.ROUTINE_ID) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt(FitLogDestinations.Args.ROUTINE_ID) ?: 0
            app.krafted.fitlog.presentation.screens.routines.RoutineEditorScreen(
                navController = navController,
                routineId = routineId
            )
        }

        composable(
            route = FitLogDestinations.EXERCISE_PICKER,
            arguments = listOf(
                navArgument(FitLogDestinations.Args.ROUTINE_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt(FitLogDestinations.Args.ROUTINE_ID) ?: 0
            app.krafted.fitlog.presentation.screens.exercises.ExercisePickerScreen(
                navController = navController,
                routineId = routineId
            )
        }

        composable(FitLogDestinations.TEMPLATE_PICKER) {
            app.krafted.fitlog.presentation.screens.routines.TemplatePickerScreen(
                navController = navController,
                onTemplateClick = { templateId ->
                    // Logic to copy template to new routine and edit
                }
            )
        }

        composable(FitLogDestinations.ACTIVE_WORKOUT_EXERCISE_PICKER) {
            app.krafted.fitlog.presentation.screens.workout.ActiveWorkoutExercisePickerScreen(
                navController = navController
            )
        }

        composable(FitLogDestinations.CALENDAR_VIEW) {
            CalendarScreen(
                onNavigateBack = { navController.popBackStack() },
                onWorkoutClick = { workoutId ->
                    navController.navigate(FitLogRoutes.workoutDetail(workoutId))
                }
            )
        }

        composable(FitLogDestinations.MUSCLE_ANALYTICS) {
            app.krafted.fitlog.presentation.screens.analytics.MuscleAnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = FitLogDestinations.EXERCISE_PROGRESS_WITH_ID,
            arguments = listOf(
                navArgument(FitLogDestinations.Args.EXERCISE_ID) {
                    type = NavType.IntType
                    defaultValue = -1
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getInt(FitLogDestinations.Args.EXERCISE_ID)?.takeIf { it != -1 }
            ExerciseProgressScreen(
                exerciseId = exerciseId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(FitLogDestinations.PREMIUM) {
            PlaceholderScreen(title = "Premium", navController = navController)
        }

        composable(FitLogDestinations.EXPORT_DATA) {
            PlaceholderScreen(title = "Export Data", navController = navController)
        }

        composable(FitLogDestinations.CARDIO_LOG) {
            CardioScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToHistory = {
                    navController.navigate(FitLogDestinations.HISTORY) {
                        popUpTo(FitLogDestinations.CARDIO_LOG) { inclusive = true }
                    }
                }
            )
        }

        composable(FitLogDestinations.BODYWEIGHT_LOG) {
            BodyweightScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
