package com.example.fitlog.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.fitlog.data.database.entity.RoutineEntity
import com.example.fitlog.data.database.entity.RoutineExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    // Routine CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity): Long

    @Update
    suspend fun updateRoutine(routine: RoutineEntity)

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("SELECT * FROM routines WHERE id = :id")
    suspend fun getRoutineById(id: Int): RoutineEntity?

    @Query("SELECT * FROM routines WHERE id = :id")
    fun getRoutineByIdFlow(id: Int): Flow<RoutineEntity?>

    @Query("SELECT * FROM routines WHERE isTemplate = 0 ORDER BY name ASC")
    fun getUserRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines WHERE isTemplate = 1 ORDER BY dayOrder ASC, name ASC")
    fun getTemplateRoutines(): Flow<List<RoutineEntity>>

    @Query("SELECT * FROM routines ORDER BY isTemplate DESC, name ASC")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    // Routine Exercise CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercise(routineExercise: RoutineExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercises(routineExercises: List<RoutineExerciseEntity>)

    @Update
    suspend fun updateRoutineExercise(routineExercise: RoutineExerciseEntity)

    @Delete
    suspend fun deleteRoutineExercise(routineExercise: RoutineExerciseEntity)

    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId")
    suspend fun deleteAllExercisesFromRoutine(routineId: Int)

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    fun getExercisesForRoutine(routineId: Int): Flow<List<RoutineExerciseEntity>>

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    suspend fun getExercisesForRoutineOnce(routineId: Int): List<RoutineExerciseEntity>

    @Query("SELECT COUNT(*) FROM routine_exercises WHERE routineId = :routineId")
    suspend fun getExerciseCountForRoutine(routineId: Int): Int

    // Update order of exercises (for drag-drop reordering)
    @Query("UPDATE routine_exercises SET orderIndex = :newOrder WHERE id = :id")
    suspend fun updateExerciseOrder(id: Int, newOrder: Int)

    @Transaction
    suspend fun reorderExercises(routineId: Int, exerciseIds: List<Int>) {
        exerciseIds.forEachIndexed { index, id ->
            updateExerciseOrder(id, index)
        }
    }
    
    // Delete all methods for database clearing
    @Query("DELETE FROM routines")
    suspend fun deleteAllRoutines()
    
    @Query("DELETE FROM routine_exercises")
    suspend fun deleteAllRoutineExercises()
}

