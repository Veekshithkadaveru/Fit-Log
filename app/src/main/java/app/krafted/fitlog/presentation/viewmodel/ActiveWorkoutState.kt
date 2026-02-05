package app.krafted.fitlog.presentation.viewmodel

import app.krafted.fitlog.domain.model.Exercise
import app.krafted.fitlog.domain.model.PRCheckResult
import app.krafted.fitlog.domain.model.RepRange
import app.krafted.fitlog.domain.model.RepRangePRResult
import app.krafted.fitlog.domain.model.Workout
import app.krafted.fitlog.domain.model.WorkoutSet

/**
 * UI state for active workout screen
 */
data class ActiveWorkoutUiState(
    val isLoading: Boolean = false,
    val currentWorkout: Workout? = null,
    val workoutDuration: Long = 0L,
    val isWorkoutActive: Boolean = false,
    val routineName: String? = null,
    val workoutExercises: List<WorkoutExerciseWithDetails> = emptyList(),
    val error: String? = null,
    val sessionPRCount: Int = 0,
    val sessionPRs: List<PREvent> = emptyList(),
    val showWorkoutSummary: Boolean = false
) {
    val exerciseCount: Int
        get() = workoutExercises.size

    val completedSetsCount: Int
        get() = workoutExercises.sumOf { it.completedSets }

    val totalSetsCount: Int
        get() = workoutExercises.sumOf { kotlin.math.max(it.targetSets, it.sets.size) }
}

/**
 * Exercise details for display in workout
 */
data class WorkoutExerciseWithDetails(
    val exercise: Exercise,
    val targetSets: Int,
    val targetReps: String,
    val completedSets: Int,
    val orderIndex: Int,
    val sets: List<WorkoutSet> = emptyList()
)

/**
 * Navigation events for workout screen
 */
sealed class WorkoutNavigationEvent {
    object NavigateToHistory : WorkoutNavigationEvent()
    object NavigateBack : WorkoutNavigationEvent()
    data class NavigateToExerciseDetail(val exerciseId: Int) : WorkoutNavigationEvent()
}

/**
 * Rest timer state
 */
data class RestTimerState(
    val isActive: Boolean = false,
    val remainingSeconds: Int = 0,
    val totalSeconds: Int = 0,
    val isPaused: Boolean = false,
    val isComplete: Boolean = false,
    val targetTimestamp: Long? = null
)

/**
 * PR event for celebration/notification
 */
data class PREvent(
    val exerciseName: String,
    val weight: Float,
    val reps: Int,
    val prResult: PRCheckResult,
    val repRangePRResult: RepRangePRResult? = null
) {
    /**
     * Get the rep range display name (e.g., "5RM", "10RM")
     */
    val repRangeDisplayName: String
        get() = repRangePRResult?.repRange?.displayName ?: RepRange.fromReps(reps).displayName

    /**
     * Check if this is a new estimated 1RM PR
     */
    val isNew1RMPR: Boolean
        get() = repRangePRResult?.isNew1RMPR == true

    /**
     * Get the estimated 1RM value
     */
    val estimated1RM: Float
        get() = repRangePRResult?.newEstimated1RM ?: 0f
}
