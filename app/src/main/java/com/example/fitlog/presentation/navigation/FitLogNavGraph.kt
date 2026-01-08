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
        startDestination = startDestination
    ) {
        
        // Bottom Navigation Screens
        composable(FitLogDestinations.HOME) {
            PlaceholderScreen(title = "Home", navController = navController)
        }
        
        composable(FitLogDestinations.WORKOUT) {
            PlaceholderScreen(title = "Workout", navController = navController)
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
        
        composable(FitLogDestinations.ROUTINE_LIST) {
            PlaceholderScreen(title = "Routine List", navController = navController)
        }
        
        composable(FitLogDestinations.ROUTINE_EDITOR) {
            PlaceholderScreen(title = "Routine Editor", navController = navController)
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