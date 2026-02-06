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
            val recentSets = workoutRepository.getRecentSetsForExercise(exerciseId, WorkoutConstants.RECENT_SETS_HISTORY_LIMIT)
            if (recentSets.isNotEmpty()) {
                weight = recentSets.map { it.weight }.average().toFloat()
                reps = recentSets.map { it.reps }.average().toInt()
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
        val updatedSet = currentSet.copy(
            weight = weight ?: currentSet.weight,
            reps = reps ?: currentSet.reps
        )
        workoutRepository.updateSet(updatedSet)
    }

    suspend fun deleteSet(setId: Int) {
        workoutRepository.deleteSetById(setId)
    }

    suspend fun toggleSetCompleted(currentSet: WorkoutSet): Boolean {
        val isCompleting = !currentSet.isCompleted
        var updatedSet = currentSet.copy(isCompleted = isCompleting)
        var isPRFound = false

        if (isCompleting && currentSet.weight >= 0 && currentSet.reps > 0) {
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
                        exerciseName = exerciseName,
                        weight = currentSet.weight,
                        reps = currentSet.reps,
                        prResult = prResult,
                        repRangePRResult = repRangePRResult
                    )
                )
            }
        }

        workoutRepository.updateSet(updatedSet)
        return isPRFound
    }
}
