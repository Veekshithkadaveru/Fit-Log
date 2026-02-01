package com.example.fitlog.presentation.navigation

/**
 * Navigation destinations for FitLog app
 */
object FitLogDestinations {
    

    const val HOME = "home"
    const val WORKOUT = "workout"
    const val EXERCISES = "exercises"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    

    const val EXERCISE_DETAIL = "exercise_detail/{exerciseId}"
    

    const val ACTIVE_WORKOUT = "active_workout"
    const val ACTIVE_WORKOUT_WITH_ROUTINE = "active_workout?routineId={routineId}"
    const val ACTIVE_WORKOUT_EXERCISE_PICKER = "active_workout_exercise_picker"
    const val ROUTINE_EDITOR = "routine_editor/{routineId}"
    const val EXERCISE_PICKER = "exercise_picker/{routineId}"
    const val ROUTINE_LIST = "routine_list"
    const val TEMPLATE_PICKER = "template_picker"
    

    const val WORKOUT_DETAIL = "workout_detail/{workoutId}"
    const val CALENDAR_VIEW = "calendar_view"
    

    const val MUSCLE_ANALYTICS = "muscle_analytics"
    const val EXERCISE_PROGRESS = "exercise_progress"
    const val EXERCISE_PROGRESS_WITH_ID = "exercise_progress?exerciseId={exerciseId}"


    const val CARDIO_LOG = "cardio_log"
    const val BODYWEIGHT_LOG = "bodyweight_log"
    

    const val PREMIUM = "premium"
    const val EXPORT_DATA = "export_data"
    

    object Args {
        const val EXERCISE_ID = "exerciseId"
        const val WORKOUT_ID = "workoutId"
        const val ROUTINE_ID = "routineId"
    }
}


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

    fun exerciseProgress(exerciseId: Int? = null): String {
        return if (exerciseId != null) {
            "exercise_progress?exerciseId=$exerciseId"
        } else {
            "exercise_progress"
        }
    }
}