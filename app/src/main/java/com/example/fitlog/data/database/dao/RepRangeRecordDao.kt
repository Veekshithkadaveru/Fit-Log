package com.example.fitlog.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fitlog.data.database.entity.RepRangeRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RepRangeRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: RepRangeRecordEntity): Long

    @Update
    suspend fun update(record: RepRangeRecordEntity)

    @Delete
    suspend fun delete(record: RepRangeRecordEntity)

    @Query("SELECT * FROM rep_range_records WHERE id = :id")
    suspend fun getById(id: Int): RepRangeRecordEntity?

    @Query("SELECT * FROM rep_range_records WHERE exerciseId = :exerciseId AND repRange = :repRange")
    suspend fun getByExerciseIdAndRepRange(exerciseId: Int, repRange: String): RepRangeRecordEntity?

    @Query("SELECT * FROM rep_range_records WHERE exerciseId = :exerciseId")
    suspend fun getByExerciseId(exerciseId: Int): List<RepRangeRecordEntity>

    @Query("SELECT * FROM rep_range_records WHERE exerciseId = :exerciseId")
    fun getByExerciseIdFlow(exerciseId: Int): Flow<List<RepRangeRecordEntity>>

    @Query("SELECT * FROM rep_range_records ORDER BY achievedDate DESC")
    fun getAllRecords(): Flow<List<RepRangeRecordEntity>>

    @Query("SELECT * FROM rep_range_records ORDER BY achievedDate DESC LIMIT :limit")
    fun getRecentRecords(limit: Int): Flow<List<RepRangeRecordEntity>>

    @Query("SELECT * FROM rep_range_records WHERE achievedDate >= :startDate AND achievedDate <= :endDate ORDER BY achievedDate DESC")
    fun getRecordsInDateRange(startDate: Long, endDate: Long): Flow<List<RepRangeRecordEntity>>

    @Query("SELECT COUNT(*) FROM rep_range_records WHERE achievedDate >= :startDate AND achievedDate <= :endDate")
    suspend fun getRecordCountInRange(startDate: Long, endDate: Long): Int

    @Query("SELECT COUNT(*) FROM rep_range_records")
    suspend fun getTotalRecordCount(): Int

    @Query("SELECT MAX(estimated1RM) FROM rep_range_records WHERE exerciseId = :exerciseId")
    suspend fun getBestEstimated1RM(exerciseId: Int): Float?

    @Query("""
        INSERT OR REPLACE INTO rep_range_records (exerciseId, repRange, bestWeight, repsAtBestWeight, estimated1RM, achievedDate)
        VALUES (:exerciseId, :repRange, :bestWeight, :repsAtBestWeight, :estimated1RM, :achievedDate)
    """)
    suspend fun upsertRecord(
        exerciseId: Int,
        repRange: String,
        bestWeight: Float,
        repsAtBestWeight: Int,
        estimated1RM: Float,
        achievedDate: Long
    )
}
