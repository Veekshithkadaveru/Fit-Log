package app.krafted.fitlog.domain.repository

import app.krafted.fitlog.domain.model.Bodyweight
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Bodyweight tracking operations.
 */
interface BodyweightRepository {

    suspend fun insert(bodyweight: Bodyweight): Long

    suspend fun update(bodyweight: Bodyweight)

    suspend fun delete(bodyweight: Bodyweight)

    suspend fun getById(id: Int): Bodyweight?

    fun getAllEntries(): Flow<List<Bodyweight>>

    suspend fun getLatestEntry(): Bodyweight?

    fun getLatestEntryFlow(): Flow<Bodyweight?>

    fun getEntriesInDateRange(startDate: Long, endDate: Long): Flow<List<Bodyweight>>

    suspend fun getEntryForDate(date: Long): Bodyweight?

    suspend fun getAverageWeightInRange(startDate: Long, endDate: Long): Float?

    suspend fun getMinWeight(): Float?

    suspend fun getMaxWeight(): Float?

    suspend fun getEntryCount(): Int
}

