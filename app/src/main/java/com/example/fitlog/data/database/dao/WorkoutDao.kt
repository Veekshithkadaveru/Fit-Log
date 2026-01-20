package com.example.fitlog.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlog.data.database.entity.CardioSessionEntity
import com.example.fitlog.data.database.entity.WorkoutEntity
import com.example.fitlog.data.database.entity.WorkoutSetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    // Workout CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutEntity): Long

    @Update
    suspend fun updateWorkout(workout: WorkoutEntity)

    @Delete
    suspend fun deleteWorkout(workout: WorkoutEntity)

    @Query("DELETE FROM workouts WHERE id = :workoutId")
    suspend fun deleteWorkoutById(workoutId: Int)

    @Query("SELECT * FROM workouts WHERE id = :id")
    suspend fun getWorkoutById(id: Int): WorkoutEntity?

    @Query("SELECT * FROM workouts WHERE id = :id")
    fun getWorkoutByIdFlow(id: Int): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts ORDER BY startTime DESC")
    fun getAllWorkouts(): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveWorkout(): WorkoutEntity?

    @Query("SELECT * FROM workouts WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    fun getActiveWorkoutFlow(): Flow<WorkoutEntity?>

    @Query("SELECT * FROM workouts WHERE startTime >= :startDate AND startTime <= :endDate ORDER BY startTime DESC")
    fun getWorkoutsInDateRange(startDate: Long, endDate: Long): Flow<List<WorkoutEntity>>

    @Query("SELECT * FROM workouts WHERE routineId = :routineId ORDER BY startTime DESC")
    fun getWorkoutsByRoutine(routineId: Int): Flow<List<WorkoutEntity>>

    @Query("SELECT COUNT(*) FROM workouts WHERE endTime IS NOT NULL")
    suspend fun getCompletedWorkoutCount(): Int

    @Query("SELECT COUNT(*) FROM workouts WHERE startTime >= :startDate AND startTime <= :endDate AND endTime IS NOT NULL")
    suspend fun getWorkoutCountInRange(startDate: Long, endDate: Long): Int

    // Workout Sets CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSet(set: WorkoutSetEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSets(sets: List<WorkoutSetEntity>)

    @Update
    suspend fun updateSet(set: WorkoutSetEntity)

    @Delete
    suspend fun deleteSet(set: WorkoutSetEntity)

    @Query("DELETE FROM workout_sets WHERE id = :setId")
    suspend fun deleteSetById(setId: Int)

    @Query("SELECT * FROM workout_sets WHERE workoutId = :workoutId ORDER BY exerciseId, setOrder ASC")
    fun getSetsForWorkout(workoutId: Int): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE workoutId = :workoutId ORDER BY exerciseId, setOrder ASC")
    suspend fun getSetsForWorkoutOnce(workoutId: Int): List<WorkoutSetEntity>

    @Query("SELECT * FROM workout_sets WHERE workoutId = :workoutId AND exerciseId = :exerciseId ORDER BY setOrder ASC")
    fun getSetsForExerciseInWorkout(workoutId: Int, exerciseId: Int): Flow<List<WorkoutSetEntity>>

    @Query("SELECT * FROM workout_sets WHERE exerciseId = :exerciseId AND isCompleted = 1 ORDER BY weight DESC, reps DESC LIMIT 1")
    suspend fun getBestSetForExercise(exerciseId: Int): WorkoutSetEntity?

    @Query("""
        SELECT ws.* FROM workout_sets ws 
        INNER JOIN workouts w ON ws.workoutId = w.id 
        WHERE ws.exerciseId = :exerciseId AND ws.isCompleted = 1 
        ORDER BY w.startTime DESC 
        LIMIT :limit
    """)
    suspend fun getRecentSetsForExercise(exerciseId: Int, limit: Int = 10): List<WorkoutSetEntity>

    @Query("SELECT MAX(weight) FROM workout_sets WHERE exerciseId = :exerciseId AND isCompleted = 1")
    suspend fun getMaxWeightForExercise(exerciseId: Int): Float?

    @Query("SELECT COUNT(*) FROM workout_sets WHERE workoutId = :workoutId AND isCompleted = 1")
    suspend fun getCompletedSetCount(workoutId: Int): Int

    // Cardio Sessions CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCardioSession(session: CardioSessionEntity): Long

    @Update
    suspend fun updateCardioSession(session: CardioSessionEntity)

    @Delete
    suspend fun deleteCardioSession(session: CardioSessionEntity)

    @Query("SELECT * FROM cardio_sessions WHERE workoutId = :workoutId")
    fun getCardioSessionsForWorkout(workoutId: Int): Flow<List<CardioSessionEntity>>

    @Query("SELECT * FROM cardio_sessions WHERE workoutId = :workoutId")
    suspend fun getCardioSessionsForWorkoutOnce(workoutId: Int): List<CardioSessionEntity>

    @Query("SELECT SUM(durationMinutes) FROM cardio_sessions WHERE workoutId = :workoutId")
    suspend fun getTotalCardioDuration(workoutId: Int): Int?

    // Analytics queries
    @Query("""
        SELECT SUM(weight * reps) FROM workout_sets 
        WHERE workoutId = :workoutId AND isCompleted = 1
    """)
    suspend fun getTotalVolumeForWorkout(workoutId: Int): Float?

    @Query("""
        SELECT SUM(ws.weight * ws.reps) FROM workout_sets ws
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE w.startTime >= :startDate AND w.startTime <= :endDate AND ws.isCompleted = 1
    """)
    suspend fun getTotalVolumeInRange(startDate: Long, endDate: Long): Float?

    @Query("SELECT AVG(endTime - startTime) FROM workouts WHERE endTime IS NOT NULL")
    suspend fun getAverageWorkoutDuration(): Long?

    // Muscle Group Analytics queries
    @Query("""
        SELECT SUM(ws.weight * ws.reps) FROM workout_sets ws
        INNER JOIN exercises e ON ws.exerciseId = e.id
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE e.primaryMuscle = :muscleGroup
        AND w.startTime >= :startDate
        AND w.startTime <= :endDate
        AND ws.isCompleted = 1
    """)
    suspend fun getTotalVolumeForMuscleInRange(
        muscleGroup: String,
        startDate: Long,
        endDate: Long
    ): Float?

    @Query("""
        SELECT COUNT(DISTINCT ws.id) FROM workout_sets ws
        INNER JOIN exercises e ON ws.exerciseId = e.id
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE e.primaryMuscle = :muscleGroup
        AND w.startTime >= :startDate
        AND w.startTime <= :endDate
        AND ws.isCompleted = 1
    """)
    suspend fun getTotalSetsForMuscleInRange(
        muscleGroup: String,
        startDate: Long,
        endDate: Long
    ): Int

    @Query("""
        SELECT COUNT(DISTINCT w.id) FROM workouts w
        INNER JOIN workout_sets ws ON ws.workoutId = w.id
        INNER JOIN exercises e ON ws.exerciseId = e.id
        WHERE e.primaryMuscle = :muscleGroup
        AND w.startTime >= :startDate
        AND w.startTime <= :endDate
        AND ws.isCompleted = 1
        AND w.endTime IS NOT NULL
    """)
    suspend fun getWorkoutCountForMuscleInRange(
        muscleGroup: String,
        startDate: Long,
        endDate: Long
    ): Int

    @Query("""
        SELECT MAX(w.startTime) FROM workouts w
        INNER JOIN workout_sets ws ON ws.workoutId = w.id
        INNER JOIN exercises e ON ws.exerciseId = e.id
        WHERE e.primaryMuscle = :muscleGroup
        AND ws.isCompleted = 1
        AND w.endTime IS NOT NULL
    """)
    suspend fun getLastWorkoutDateForMuscle(muscleGroup: String): Long?

    @Query("""
        SELECT COUNT(DISTINCT ws.exerciseId) FROM workout_sets ws
        INNER JOIN exercises e ON ws.exerciseId = e.id
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE e.primaryMuscle = :muscleGroup
        AND w.startTime >= :startDate
        AND w.startTime <= :endDate
        AND ws.isCompleted = 1
    """)
    suspend fun getExerciseCountForMuscleInRange(
        muscleGroup: String,
        startDate: Long,
        endDate: Long
    ): Int

    @Query("""
        SELECT DISTINCT e.primaryMuscle FROM exercises e
        INNER JOIN workout_sets ws ON ws.exerciseId = e.id
        INNER JOIN workouts w ON ws.workoutId = w.id
        WHERE w.startTime >= :startDate
        AND w.startTime <= :endDate
        AND ws.isCompleted = 1
    """)
    suspend fun getActiveMuscleGroupsInRange(
        startDate: Long,
        endDate: Long
    ): List<String>
}

