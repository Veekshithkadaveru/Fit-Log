package com.example.fitlog.data.repository

import com.example.fitlog.data.database.dao.WorkoutDao
import com.example.fitlog.data.mapper.toCardioDomainModels
import com.example.fitlog.data.mapper.toDomainModel
import com.example.fitlog.data.mapper.toEntity
import com.example.fitlog.domain.model.CardioSession
import com.example.fitlog.domain.repository.CardioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardioRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao
) : CardioRepository {

    override suspend fun insert(session: CardioSession): Long {
        return workoutDao.insertCardioSession(session.toEntity())
    }

    override suspend fun update(session: CardioSession) {
        workoutDao.updateCardioSession(session.toEntity())
    }

    override suspend fun delete(session: CardioSession) {
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

    override suspend fun getAllCardioSessions(): List<CardioSession> {
        return workoutDao.getAllCardioSessions().toCardioDomainModels()
    }

    override fun getAllCardioSessionsFlow(): Flow<List<CardioSession>> {
        return workoutDao.getAllCardioSessionsFlow().map { entities ->
            entities.toCardioDomainModels()
        }
    }

    override suspend fun getCardioSessionsByDateRange(startDate: Long, endDate: Long): List<CardioSession> {
        return workoutDao.getCardioSessionsByDateRange(startDate, endDate).toCardioDomainModels()
    }
}
