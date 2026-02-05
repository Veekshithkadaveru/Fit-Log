package app.krafted.fitlog.domain.repository

import app.krafted.fitlog.domain.model.CardioSession
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Cardio session operations.
 */
interface CardioRepository {

    suspend fun insert(session: CardioSession): Long

    suspend fun update(session: CardioSession)

    suspend fun delete(session: CardioSession)

    fun getCardioSessionsForWorkout(workoutId: Int): Flow<List<CardioSession>>

    suspend fun getCardioSessionsForWorkoutOnce(workoutId: Int): List<CardioSession>

    suspend fun getTotalCardioDuration(workoutId: Int): Int?

    suspend fun getAllCardioSessions(): List<CardioSession>

    fun getAllCardioSessionsFlow(): Flow<List<CardioSession>>

    suspend fun getCardioSessionsByDateRange(startDate: Long, endDate: Long): List<CardioSession>
}
