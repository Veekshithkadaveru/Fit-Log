package app.krafted.fitlog.presentation.viewmodel.manager

import app.krafted.fitlog.domain.model.PRCheckResult
import app.krafted.fitlog.domain.model.RepRangePRResult
import app.krafted.fitlog.domain.model.Workout
import app.krafted.fitlog.domain.model.WorkoutSet
import app.krafted.fitlog.domain.repository.ExerciseRepository
import app.krafted.fitlog.domain.repository.WorkoutRepository
import app.krafted.fitlog.domain.usecase.PRDetectionUseCase
import app.krafted.fitlog.presentation.screens.workout.WorkoutConstants
import app.krafted.fitlog.presentation.viewmodel.PREvent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

/**
 * Manages set operations for an active workout:
 * - Adding, updating, deleting sets
 * - Toggling completion
 * - Detecting PRs
 */
@javax.inject.Singleton
class ActiveWorkoutSetManager @Inject constructor(
    private val workoutRepository: WorkoutRepository,
    private val exerciseRepository: ExerciseRepository,
    private val prDetectionUseCase: PRDetectionUseCase
) {

    private val _prEvents = MutableSharedFlow<PREvent>()
    val prEvents: SharedFlow<PREvent> = _prEvents.asSharedFlow()

    suspend fun addSet(workoutId: Int, exerciseId: Int, currentSets: List<WorkoutSet>, autoFill: Boolean): WorkoutSet {
        val existingSets = currentSets.filter { it.exerciseId == exerciseId }
        val nextSetOrder = (existingSets.maxOfOrNull { it.setOrder } ?: -1) + 1

        var weight = 0f
        var reps = 0

        if (autoFill) {
            // Check if there are sets in the current session first
            val lastSetInSession = existingSets.lastOrNull { it.weight > 0f || it.reps > 0 }
            if (lastSetInSession != null) {
                weight = lastSetInSession.weight
                reps = lastSetInSession.reps
            } else {
                // Get the single most recent completed set across all workouts
                val recentSets = workoutRepository.getRecentSetsForExercise(
                    exerciseId = exerciseId,
                    limit = 1
                )
                if (recentSets.isNotEmpty()) {
                    weight = recentSets.first().weight
                    reps = recentSets.first().reps
                }
            }
        }

        val newSet = WorkoutSet(
            workoutId = workoutId,
            exerciseId = exerciseId,
            weight = weight,
            reps = reps,
            isCompleted = false,
            isPR = false,
            setOrder = nextSetOrder
        )

        val setId = workoutRepository.insertSet(newSet).toInt()
        return newSet.copy(id = setId)
    }

    suspend fun duplicateSet(workoutId: Int, setToDuplicate: WorkoutSet, allSets: List<WorkoutSet>): WorkoutSet {
        val exerciseSets = allSets.filter { it.exerciseId == setToDuplicate.exerciseId }
        val nextSetOrder = (exerciseSets.maxOfOrNull { it.setOrder } ?: -1) + 1

        val newSet = WorkoutSet(
            workoutId = workoutId,
            exerciseId = setToDuplicate.exerciseId,
            weight = setToDuplicate.weight,
            reps = setToDuplicate.reps,
            isCompleted = false,
            isPR = false,
            setOrder = nextSetOrder
        )

        val newSetId = workoutRepository.insertSet(newSet).toInt()
        return newSet.copy(id = newSetId)
    }

    suspend fun updateSet(setId: Int, weight: Float? = null, reps: Int? = null, currentSet: WorkoutSet) {
        val newWeight = weight ?: currentSet.weight
        val newReps = reps ?: currentSet.reps
        var updatedSet = currentSet.copy(
            weight = newWeight,
            reps = newReps
        )

        // If the set is completed, we should recheck its PR status
        if (currentSet.isCompleted) {
            // First reset isPR to false so we don't carry over stale PR state
            updatedSet = updatedSet.copy(isPR = false)

            if (newWeight >= 0f && newReps > 0) {
                val (prResult, repRangePRResult) = prDetectionUseCase.checkAndUpdatePRWithRepRange(
                    exerciseId = currentSet.exerciseId,
                    weight = newWeight,
                    reps = newReps
                )

                if (prResult.isAnyPR || repRangePRResult.isAnyPR) {
                    updatedSet = updatedSet.copy(isPR = true)

                    val exercise = exerciseRepository.getExerciseById(currentSet.exerciseId)
                    val exerciseName = exercise?.name ?: "Exercise"

                    _prEvents.emit(
                        PREvent(
                            setId = currentSet.id,
                            exerciseName = exerciseName,
                            weight = newWeight,
                            reps = newReps,
                            prResult = prResult,
                            repRangePRResult = repRangePRResult
                        )
                    )
                }
            }
        }
        workoutRepository.updateSet(updatedSet)
    }

    suspend fun deleteSet(setId: Int) {
        workoutRepository.deleteSetById(setId)
    }

    suspend fun toggleSetCompleted(currentSet: WorkoutSet): Boolean {
        val isCompleting = !currentSet.isCompleted
        var updatedSet = currentSet.copy(isCompleted = isCompleting)
        var isPRFound = false

        if (isCompleting) {
            if (currentSet.weight >= 0f && currentSet.reps > 0) {
                val (prResult, repRangePRResult) = prDetectionUseCase.checkAndUpdatePRWithRepRange(
                    exerciseId = currentSet.exerciseId,
                    weight = currentSet.weight,
                    reps = currentSet.reps
                )

                if (prResult.isAnyPR || repRangePRResult.isAnyPR) {
                    isPRFound = true
                    updatedSet = updatedSet.copy(isPR = true)

                    val exercise = exerciseRepository.getExerciseById(currentSet.exerciseId)
                    val exerciseName = exercise?.name ?: "Exercise"

                    _prEvents.emit(
                        PREvent(
                            setId = currentSet.id,
                            exerciseName = exerciseName,
                            weight = currentSet.weight,
                            reps = currentSet.reps,
                            prResult = prResult,
                            repRangePRResult = repRangePRResult
                        )
                    )
                }
            }
        } else {
            updatedSet = updatedSet.copy(isPR = false)
        }

        workoutRepository.updateSet(updatedSet)
        return isPRFound
    }
}
