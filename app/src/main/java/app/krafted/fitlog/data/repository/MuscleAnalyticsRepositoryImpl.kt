package app.krafted.fitlog.data.repository

import app.krafted.fitlog.data.database.dao.WorkoutDao
import app.krafted.fitlog.domain.model.MuscleGroup
import app.krafted.fitlog.domain.repository.MuscleAnalyticsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MuscleAnalyticsRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao
) : MuscleAnalyticsRepository {

    override suspend fun getTotalVolumeForMuscleInRange(
        muscleGroup: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): Float? {
        return workoutDao.getTotalVolumeForMuscleInRange(muscleGroup.name, startDate, endDate)
    }

    override suspend fun getTotalSetsForMuscleInRange(
        muscleGroup: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): Int {
        return workoutDao.getTotalSetsForMuscleInRange(muscleGroup.name, startDate, endDate)
    }

    override suspend fun getWorkoutCountForMuscleInRange(
        muscleGroup: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): Int {
        return workoutDao.getWorkoutCountForMuscleInRange(muscleGroup.name, startDate, endDate)
    }

    override suspend fun getLastWorkoutDateForMuscle(muscleGroup: MuscleGroup): Long? {
        return workoutDao.getLastWorkoutDateForMuscle(muscleGroup.name)
    }

    override suspend fun getExerciseCountForMuscleInRange(
        muscleGroup: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): Int {
        return workoutDao.getExerciseCountForMuscleInRange(muscleGroup.name, startDate, endDate)
    }

    override suspend fun getActiveMuscleGroupsInRange(
        startDate: Long,
        endDate: Long
    ): List<MuscleGroup> {
        return workoutDao.getActiveMuscleGroupsInRange(startDate, endDate)
            .mapNotNull { muscleGroupName ->
                try {
                    MuscleGroup.valueOf(muscleGroupName)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
    }
}
