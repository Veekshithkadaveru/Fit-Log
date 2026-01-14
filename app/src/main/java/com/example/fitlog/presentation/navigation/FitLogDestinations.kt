package com.example.fitlog.presentation.navigation

/**
 * Navigation destinations for FitLog app
 */
object FitLogDestinations {
    
    // Bottom Navigation Destinations
    const val HOME = "home"
    const val WORKOUT = "workout"
    const val EXERCISES = "exercises"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    
    // Exercise Library Destinations
    const val EXERCISE_DETAIL = "exercise_detail/{exerciseId}"
    
    // Workout Destinations
    const val ACTIVE_WORKOUT = "active_workout"
    const val ACTIVE_WORKOUT_WITH_ROUTINE = "active_workout?routineId={routineId}"
    const val ROUTINE_EDITOR = "routine_editor/{routineId}"
    const val EXERCISE_PICKER = "exercise_picker/{routineId}"
    const val ROUTINE_LIST = "routine_list"
    const val TEMPLATE_PICKER = "template_picker"
    
    // History Destinations
    const val WORKOUT_DETAIL = "workout_detail/{workoutId}"
    const val CALENDAR_VIEW = "calendar_view"
    
    // Settings Destinations
    const val PREMIUM = "premium"
    const val EXPORT_DATA = "export_data"
    
    // Navigation argument keys
    object Args {
        const val EXERCISE_ID = "exerciseId"
        const val WORKOUT_ID = "workoutId"
        const val ROUTINE_ID = "routineId"
    }
}

/**
 * Helper functions for building navigation routes with arguments
 */
object FitLogRoutes {
    
    fun exerciseDetail(exerciseId: Int): String {
        return "exercise_detail/$exerciseId"
    }
    
    fun workoutDetail(workoutId: Int): String {
        return "workout_detail/$workoutId"
    }

    fun routineEditor(routineId: Int): String {
        return "routine_editor/$routineId"
    }

    fun exercisePicker(routineId: Int): String {
        return "exercise_picker/$routineId"
    }

    fun activeWorkoutWithRoutine(routineId: Int): String {
        return "active_workout?routineId=$routineId"
    }
}