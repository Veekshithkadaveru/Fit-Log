package app.krafted.fitlog.data.repository

import app.krafted.fitlog.data.database.dao.WorkoutDao
import app.krafted.fitlog.data.mapper.toCardioDomainModels
import app.krafted.fitlog.data.mapper.toDomainModel
import app.krafted.fitlog.data.mapper.toDomainModels
import app.krafted.fitlog.data.mapper.toEntity
import app.krafted.fitlog.data.mapper.toWorkoutSetDomainModels
import app.krafted.fitlog.data.mapper.toWorkoutSetEntities
import app.krafted.fitlog.domain.model.*
import app.krafted.fitlog.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao
) : WorkoutRepository {

    // ==================== Workout Operations ====================

    override suspend fun insertWorkout(workout: Workout): Long {
        return workoutDao.insertWorkout(workout.toEntity())
    }

    override suspend fun updateWorkout(workout: Workout) {
        workoutDao.updateWorkout(workout.toEntity())
    }

    override suspend fun deleteWorkout(workout: Workout) {
        workoutDao.deleteWorkout(workout.toEntity())
    }

    override suspend fun deleteWorkoutById(workoutId: Int) {
        workoutDao.deleteWorkoutById(workoutId)
    }

    override suspend fun getWorkoutById(id: Int): Workout? {
        val entity = workoutDao.getWorkoutById(id) ?: return null
        val sets = workoutDao.getSetsForWorkoutOnce(id).toWorkoutSetDomainModels()
        val cardio = workoutDao.getCardioSessionsForWorkoutOnce(id).toCardioDomainModels()
        return entity.toDomainModel(sets = sets, cardioSessions = cardio)
    }

    override fun getWorkoutByIdFlow(id: Int): Flow<Workout?> {
        return workoutDao.getWorkoutByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getAllWorkouts(): Flow<List<Workout>> {
        return workoutDao.getAllWorkouts().map { entities ->
            entities.toDomainModels()
        }
    }

    override suspend fun getActiveWorkout(): Workout? {
        val entity = workoutDao.getActiveWorkout() ?: return null
        val sets = workoutDao.getSetsForWorkoutOnce(entity.id).toWorkoutSetDomainModels()
        val cardio = workoutDao.getCardioSessionsForWorkoutOnce(entity.id).toCardioDomainModels()
        return entity.toDomainModel(sets = sets, cardioSessions = cardio)
    }

    override fun getActiveWorkoutFlow(): Flow<Workout?> {
        return workoutDao.getActiveWorkoutFlow().map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getWorkoutsInDateRange(startDate: Long, endDate: Long): Flow<List<Workout>> {
        return workoutDao.getWorkoutsInDateRange(startDate, endDate).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getWorkoutsByRoutine(routineId: Int): Flow<List<Workout>> {
        return workoutDao.getWorkoutsByRoutine(routineId).map { entities ->
            entities.toDomainModels()
        }
    }

    override suspend fun getCompletedWorkoutCount(): Int {
        return workoutDao.getCompletedWorkoutCount()
    }

    override suspend fun getWorkoutCountInRange(startDate: Long, endDate: Long): Int {
        return workoutDao.getWorkoutCountInRange(startDate, endDate)
    }

    // ==================== Workout Set Operations ====================

    override suspend fun insertSet(set: WorkoutSet): Long {
        return workoutDao.insertSet(set.toEntity())
    }

    override suspend fun insertSets(sets: List<WorkoutSet>) {
        workoutDao.insertSets(sets.toWorkoutSetEntities())
    }

    override suspend fun updateSet(set: WorkoutSet) {
        workoutDao.updateSet(set.toEntity())
    }

    override suspend fun deleteSet(set: WorkoutSet) {
        workoutDao.deleteSet(set.toEntity())
    }

    override suspend fun deleteSetById(setId: Int) {
        workoutDao.deleteSetById(setId)
    }

    override fun getSetsForWorkout(workoutId: Int): Flow<List<WorkoutSet>> {
        return workoutDao.getSetsForWorkout(workoutId).map { entities ->
            entities.toWorkoutSetDomainModels()
        }
    }

    override suspend fun getSetsForWorkoutOnce(workoutId: Int): List<WorkoutSet> {
        return workoutDao.getSetsForWorkoutOnce(workoutId).toWorkoutSetDomainModels()
    }

    override fun getSetsForExerciseInWorkout(workoutId: Int, exerciseId: Int): Flow<List<WorkoutSet>> {
        return workoutDao.getSetsForExerciseInWorkout(workoutId, exerciseId).map { entities ->
            entities.toWorkoutSetDomainModels()
        }
    }

    override suspend fun getBestSetForExercise(exerciseId: Int): WorkoutSet? {
        return workoutDao.getBestSetForExercise(exerciseId)?.toDomainModel()
    }

    override suspend fun getRecentSetsForExercise(exerciseId: Int, limit: Int): List<WorkoutSet> {
        return workoutDao.getRecentSetsForExercise(exerciseId, limit).toWorkoutSetDomainModels()
    }

    override suspend fun getMaxWeightForExercise(exerciseId: Int): Float? {
        return workoutDao.getMaxWeightForExercise(exerciseId)
    }

    override suspend fun getCompletedSetCount(workoutId: Int): Int {
        return workoutDao.getCompletedSetCount(workoutId)
    }

    // ==================== Cardio Session Operations ====================

    override suspend fun insertCardioSession(session: CardioSession): Long {
        return workoutDao.insertCardioSession(session.toEntity())
    }

    override suspend fun updateCardioSession(session: CardioSession) {
        workoutDao.updateCardioSession(session.toEntity())
    }

    override suspend fun deleteCardioSession(session: CardioSession) {
        workoutDao.deleteCardioSession(session.toEntity())
    }

    override fun getCardioSessionsForWorkout(workoutId: Int): Flow<List<CardioSession>> {
        return workoutDao.getCardioSessionsForWorkout(workoutId).map { entities ->
            entities.toCardioDomainModels()
        }
    }

    override suspend fun getCardioSessionsForWorkoutOnce(workoutId: Int): List<CardioSession> {
        return workoutDao.getCardioSessionsForWorkoutOnce(workoutId).toCardioDomainModels()
    }

    override suspend fun getTotalCardioDuration(workoutId: Int): Int? {
        return workoutDao.getTotalCardioDuration(workoutId)
    }

    // ==================== Analytics Operations ====================

    override suspend fun getTotalVolumeForWorkout(workoutId: Int): Float? {
        return workoutDao.getTotalVolumeForWorkout(workoutId)
    }

    override suspend fun getTotalVolumeInRange(startDate: Long, endDate: Long): Float? {
        return workoutDao.getTotalVolumeInRange(startDate, endDate)
    }

    override suspend fun getAverageWorkoutDuration(): Long? {
        return workoutDao.getAverageWorkoutDuration()
    }

    // ==================== Muscle Group Analytics Operations ====================

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

