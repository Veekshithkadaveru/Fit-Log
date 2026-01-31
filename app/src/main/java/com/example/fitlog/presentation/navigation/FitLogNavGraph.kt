package com.example.fitlog.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.fitlog.presentation.screens.PlaceholderScreen
import com.example.fitlog.presentation.screens.exercises.ExerciseDetailScreen
import com.example.fitlog.presentation.screens.exercises.ExerciseLibraryScreen
import com.example.fitlog.presentation.screens.calendar.CalendarScreen
import com.example.fitlog.presentation.screens.history.HistoryScreen
import com.example.fitlog.presentation.screens.history.WorkoutDetailScreen
import com.example.fitlog.presentation.screens.progress.ExerciseProgressScreen
import com.example.fitlog.presentation.screens.progress.ProgressDashboardScreen

/**
 * Main navigation graph for the FitLog app
 */
@Composable
fun FitLogNavGraph(
    navController: NavHostController,
    startDestination: String = FitLogDestinations.HOME,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(FitLogDestinations.HOME) {
            ProgressDashboardScreen()
        }
        
        composable(FitLogDestinations.WORKOUT) {
            com.example.fitlog.presentation.screens.workout.ActiveWorkoutScreen(
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
            com.example.fitlog.presentation.screens.workout.ActiveWorkoutScreen(
                navController = navController,
                routineId = routineId
            )
        }
        
        

        composable(FitLogDestinations.ROUTINE_LIST) {
            com.example.fitlog.presentation.screens.routines.RoutineListScreen(
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
            com.example.fitlog.presentation.screens.routines.RoutineEditorScreen(
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
            com.example.fitlog.presentation.screens.exercises.ExercisePickerScreen(
                navController = navController,
                routineId = routineId
            )
        }


        composable(FitLogDestinations.TEMPLATE_PICKER) {
            com.example.fitlog.presentation.screens.routines.TemplatePickerScreen(
                navController = navController,
                onTemplateClick = { templateId ->
                    // Logic to copy template to new routine and edit
                }
            )
        }
        composable(FitLogDestinations.ACTIVE_WORKOUT_EXERCISE_PICKER) {
            com.example.fitlog.presentation.screens.workout.ActiveWorkoutExercisePickerScreen(
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
            com.example.fitlog.presentation.screens.analytics.MuscleAnalyticsScreen(
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
    }
}