package app.krafted.fitlog.domain.repository

import app.krafted.fitlog.domain.model.MuscleGroup

/**
 * Repository interface for muscle group analytics operations.
 */
interface MuscleAnalyticsRepository {

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
