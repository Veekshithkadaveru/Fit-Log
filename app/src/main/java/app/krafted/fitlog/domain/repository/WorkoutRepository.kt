package app.krafted.fitlog.domain.repository

import app.krafted.fitlog.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Workout, WorkoutSet, and CardioSession operations.
 */
interface WorkoutRepository {

    // ==================== Workout Operations ====================

    suspend fun insertWorkout(workout: Workout): Long

    suspend fun updateWorkout(workout: Workout)

    suspend fun deleteWorkout(workout: Workout)

    suspend fun deleteWorkoutById(workoutId: Int)

    suspend fun getWorkoutById(id: Int): Workout?

    fun getWorkoutByIdFlow(id: Int): Flow<Workout?>

    fun getAllWorkouts(): Flow<List<Workout>>

    suspend fun getActiveWorkout(): Workout?

    fun getActiveWorkoutFlow(): Flow<Workout?>

    fun getWorkoutsInDateRange(startDate: Long, endDate: Long): Flow<List<Workout>>

    fun getWorkoutsByRoutine(routineId: Int): Flow<List<Workout>>

    suspend fun getCompletedWorkoutCount(): Int

    suspend fun getWorkoutCountInRange(startDate: Long, endDate: Long): Int

    // ==================== Workout Set Operations ====================

    suspend fun insertSet(set: WorkoutSet): Long

    suspend fun insertSets(sets: List<WorkoutSet>)

    suspend fun updateSet(set: WorkoutSet)

    suspend fun deleteSet(set: WorkoutSet)

    suspend fun deleteSetById(setId: Int)

    fun getSetsForWorkout(workoutId: Int): Flow<List<WorkoutSet>>

    suspend fun getSetsForWorkoutOnce(workoutId: Int): List<WorkoutSet>

    fun getSetsForExerciseInWorkout(workoutId: Int, exerciseId: Int): Flow<List<WorkoutSet>>

    suspend fun getBestSetForExercise(exerciseId: Int): WorkoutSet?

    suspend fun getRecentSetsForExercise(exerciseId: Int, limit: Int = 10): List<WorkoutSet>

    suspend fun getMaxWeightForExercise(exerciseId: Int): Float?

    suspend fun getCompletedSetCount(workoutId: Int): Int

    // ==================== Cardio Session Operations ====================

    suspend fun insertCardioSession(session: CardioSession): Long

    suspend fun updateCardioSession(session: CardioSession)

    suspend fun deleteCardioSession(session: CardioSession)

    fun getCardioSessionsForWorkout(workoutId: Int): Flow<List<CardioSession>>

    suspend fun getCardioSessionsForWorkoutOnce(workoutId: Int): List<CardioSession>

    suspend fun getTotalCardioDuration(workoutId: Int): Int?

    // ==================== Analytics Operations ====================

    suspend fun getTotalVolumeForWorkout(workoutId: Int): Float?

    suspend fun getTotalVolumeInRange(startDate: Long, endDate: Long): Float?

    suspend fun getAverageWorkoutDuration(): Long?

    // ==================== Muscle Group Analytics Operations ====================

    suspend fun getTotalVolumeForMuscleInRange(
        muscleGroup: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): Float?

    suspend fun getTotalSetsForMuscleInRange(
        muscleGroup: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): Int

    suspend fun getWorkoutCountForMuscleInRange(
        muscleGroup: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): Int

    suspend fun getLastWorkoutDateForMuscle(muscleGroup: MuscleGroup): Long?

    suspend fun getExerciseCountForMuscleInRange(
        muscleGroup: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): Int

    suspend fun getActiveMuscleGroupsInRange(
        startDate: Long,
        endDate: Long
    ): List<MuscleGroup>
}

