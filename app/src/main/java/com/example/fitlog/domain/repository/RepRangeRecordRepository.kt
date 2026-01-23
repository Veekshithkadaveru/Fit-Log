package com.example.fitlog.domain.repository

import com.example.fitlog.domain.model.RepRange
import com.example.fitlog.domain.model.RepRangeRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for rep-range specific Personal Record operations.
 */
interface RepRangeRecordRepository {

    suspend fun insert(record: RepRangeRecord): Long

    suspend fun update(record: RepRangeRecord)

    suspend fun delete(record: RepRangeRecord)

    suspend fun getById(id: Int): RepRangeRecord?

    suspend fun getByExerciseIdAndRepRange(exerciseId: Int, repRange: RepRange): RepRangeRecord?

    suspend fun getByExerciseId(exerciseId: Int): List<RepRangeRecord>

    fun getByExerciseIdFlow(exerciseId: Int): Flow<List<RepRangeRecord>>

    fun getAllRecords(): Flow<List<RepRangeRecord>>

    fun getRecentRecords(limit: Int): Flow<List<RepRangeRecord>>

    fun getRecordsInDateRange(startDate: Long, endDate: Long): Flow<List<RepRangeRecord>>

    suspend fun getRecordCountInRange(startDate: Long, endDate: Long): Int

    suspend fun getTotalRecordCount(): Int

    suspend fun getBestEstimated1RM(exerciseId: Int): Float?

    suspend fun upsertRecord(
        exerciseId: Int,
        repRange: RepRange,
        bestWeight: Float,
        repsAtBestWeight: Int,
        estimated1RM: Float,
        achievedDate: Long
    )
}
