package app.krafted.fitlog.domain.repository

import app.krafted.fitlog.domain.model.PersonalRecord
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Personal Record (PR) operations.
 */
interface PersonalRecordRepository {

    suspend fun insert(record: PersonalRecord): Long

    suspend fun update(record: PersonalRecord)

    suspend fun delete(record: PersonalRecord)

    suspend fun getById(id: Int): PersonalRecord?

    suspend fun getByExerciseId(exerciseId: Int): PersonalRecord?

    fun getByExerciseIdFlow(exerciseId: Int): Flow<PersonalRecord?>

    fun getAllRecords(): Flow<List<PersonalRecord>>

    fun getRecentRecords(limit: Int): Flow<List<PersonalRecord>>

    fun getRecordsInDateRange(startDate: Long, endDate: Long): Flow<List<PersonalRecord>>

    suspend fun getRecordCountInRange(startDate: Long, endDate: Long): Int

    suspend fun getTotalRecordCount(): Int

    suspend fun upsertRecord(
        exerciseId: Int,
        maxWeight: Float,
        maxReps: Int,
        maxVolume: Float,
        achievedDate: Long
    )

    suspend fun isNewRecord(exerciseId: Int, weight: Float, reps: Int): Boolean
}

