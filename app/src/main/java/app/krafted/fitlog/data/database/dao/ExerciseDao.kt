package app.krafted.fitlog.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import app.krafted.fitlog.data.database.entity.ExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntity>)

    @Update
    suspend fun update(exercise: ExerciseEntity)

    @Delete
    suspend fun delete(exercise: ExerciseEntity)

    @Query("SELECT * FROM exercises WHERE id = :id")
    suspend fun getById(id: Int): ExerciseEntity?

    @Query("SELECT * FROM exercises WHERE id = :id")
    fun getByIdFlow(id: Int): Flow<ExerciseEntity?>

    @Query("SELECT * FROM exercises ORDER BY name ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE primaryMuscle = :muscle ORDER BY name ASC")
    fun getExercisesByMuscle(muscle: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY name ASC")
    fun getExercisesByCategory(category: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE equipment = :equipment ORDER BY name ASC")
    fun getExercisesByEquipment(equipment: String): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchExercises(query: String): Flow<List<ExerciseEntity>>

    @Query("""
        SELECT * FROM exercises 
        WHERE (:muscle IS NULL OR primaryMuscle = :muscle)
        AND (:equipment IS NULL OR equipment = :equipment)
        AND (:category IS NULL OR category = :category)
        ORDER BY name ASC
    """)
    fun getFilteredExercises(
        muscle: String?,
        equipment: String?,
        category: String?
    ): Flow<List<ExerciseEntity>>

    @Query("SELECT DISTINCT primaryMuscle FROM exercises ORDER BY primaryMuscle ASC")
    fun getAllMuscleGroups(): Flow<List<String>>

    @Query("SELECT DISTINCT equipment FROM exercises ORDER BY equipment ASC")
    fun getAllEquipmentTypes(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getExerciseCount(): Int

    @Query("DELETE FROM exercises")
    suspend fun deleteAll()
}

