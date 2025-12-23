package com.example.fitlog.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlog.data.database.entity.PersonalRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonalRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: PersonalRecordEntity): Long

    @Update
    suspend fun update(record: PersonalRecordEntity)

    @Delete
    suspend fun delete(record: PersonalRecordEntity)

    @Query("SELECT * FROM personal_records WHERE id = :id")
    suspend fun getById(id: Int): PersonalRecordEntity?

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId")
    suspend fun getByExerciseId(exerciseId: Int): PersonalRecordEntity?

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId")
    fun getByExerciseIdFlow(exerciseId: Int): Flow<PersonalRecordEntity?>

    @Query("SELECT * FROM personal_records ORDER BY achievedDate DESC")
    fun getAllRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records ORDER BY achievedDate DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE achievedDate >= :startDate AND achievedDate <= :endDate ORDER BY achievedDate DESC")
    fun getRecordsInDateRange(startDate: Long, endDate: Long): Flow<List<PersonalRecordEntity>>

    @Query("SELECT COUNT(*) FROM personal_records WHERE achievedDate >= :startDate AND achievedDate <= :endDate")
    suspend fun getRecordCountInRange(startDate: Long, endDate: Long): Int

    @Query("SELECT COUNT(*) FROM personal_records")
    suspend fun getTotalRecordCount(): Int

    @Query("""
        INSERT OR REPLACE INTO personal_records (exerciseId, maxWeight, maxReps, maxVolume, achievedDate)
        VALUES (:exerciseId, :maxWeight, :maxReps, :maxVolume, :achievedDate)
    """)
    suspend fun upsertRecord(
        exerciseId: Int,
        maxWeight: Float,
        maxReps: Int,
        maxVolume: Float,
        achievedDate: Long
    )

    // Check if a new set beats existing records
    @Query("""
        SELECT CASE 
            WHEN :weight > COALESCE((SELECT maxWeight FROM personal_records WHERE exerciseId = :exerciseId), 0) THEN 1
            WHEN :reps > COALESCE((SELECT maxReps FROM personal_records WHERE exerciseId = :exerciseId), 0) THEN 1
            WHEN (:weight * :reps) > COALESCE((SELECT maxVolume FROM personal_records WHERE exerciseId = :exerciseId), 0) THEN 1
            ELSE 0
        END
    """)
    suspend fun isNewRecord(exerciseId: Int, weight: Float, reps: Int): Int
}

