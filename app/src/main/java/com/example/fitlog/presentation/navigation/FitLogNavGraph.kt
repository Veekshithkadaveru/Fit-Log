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
        
        // Bottom Navigation Screens
        composable(FitLogDestinations.HOME) {
            PlaceholderScreen(title = "Home", navController = navController)
        }
        
        composable(FitLogDestinations.WORKOUT) {
            com.example.fitlog.presentation.screens.routines.RoutineListScreen(
                navController = navController,
                onRoutineClick = { routineId ->
                    navController.navigate(FitLogRoutes.routineEditor(routineId))
                }
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
            PlaceholderScreen(title = "History", navController = navController)
        }
        
        composable(FitLogDestinations.SETTINGS) {
            PlaceholderScreen(title = "Settings", navController = navController)
        }
        
        // Exercise Detail Screen
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
        
        // Workout Detail Screen
        composable(
            route = FitLogDestinations.WORKOUT_DETAIL,
            arguments = listOf(
                navArgument(FitLogDestinations.Args.WORKOUT_ID) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val workoutId = backStackEntry.arguments?.getInt(FitLogDestinations.Args.WORKOUT_ID) ?: 0
            PlaceholderScreen(
                title = "Workout Detail",
                subtitle = "Workout ID: $workoutId",
                navController = navController
            )
        }
        
        // Additional screens (for future implementation)
        composable(FitLogDestinations.ACTIVE_WORKOUT) {
            PlaceholderScreen(title = "Active Workout", navController = navController)
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

        composable(
            route = FitLogDestinations.EXERCISE_PICKER,
            arguments = listOf(
                navArgument(FitLogDestinations.Args.ROUTINE_ID) {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getInt(FitLogDestinations.Args.ROUTINE_ID) ?: 0
            com.example.fitlog.presentation.screens.exercises.ExercisePickerScreen(
                navController = navController,
                routineId = routineId
            )
        }
        
        composable(FitLogDestinations.CALENDAR_VIEW) {
            PlaceholderScreen(title = "Calendar", navController = navController)
        }
        
        composable(FitLogDestinations.PREMIUM) {
            PlaceholderScreen(title = "Premium", navController = navController)
        }
        
        composable(FitLogDestinations.EXPORT_DATA) {
            PlaceholderScreen(title = "Export Data", navController = navController)
        }
    }
}