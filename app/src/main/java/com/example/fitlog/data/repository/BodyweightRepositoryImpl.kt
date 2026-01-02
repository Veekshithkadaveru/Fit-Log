package com.example.fitlog.data.repository

import com.example.fitlog.data.database.dao.BodyweightDao
import com.example.fitlog.data.mapper.toBodyweightDomainModels
import com.example.fitlog.data.mapper.toDomainModel
import com.example.fitlog.data.mapper.toEntity
import com.example.fitlog.domain.model.Bodyweight
import com.example.fitlog.domain.repository.BodyweightRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BodyweightRepositoryImpl @Inject constructor(
    private val bodyweightDao: BodyweightDao
) : BodyweightRepository {

    override suspend fun insert(bodyweight: Bodyweight): Long {
        return bodyweightDao.insert(bodyweight.toEntity())
    }

    override suspend fun update(bodyweight: Bodyweight) {
        bodyweightDao.update(bodyweight.toEntity())
    }

    override suspend fun delete(bodyweight: Bodyweight) {
        bodyweightDao.delete(bodyweight.toEntity())
    }

    override suspend fun getById(id: Int): Bodyweight? {
        return bodyweightDao.getById(id)?.toDomainModel()
    }

    override fun getAllEntries(): Flow<List<Bodyweight>> {
        return bodyweightDao.getAllEntries().map { entities ->
            entities.toBodyweightDomainModels()
        }
    }

    override suspend fun getLatestEntry(): Bodyweight? {
        return bodyweightDao.getLatestEntry()?.toDomainModel()
    }

    override fun getLatestEntryFlow(): Flow<Bodyweight?> {
        return bodyweightDao.getLatestEntryFlow().map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getEntriesInDateRange(startDate: Long, endDate: Long): Flow<List<Bodyweight>> {
        return bodyweightDao.getEntriesInDateRange(startDate, endDate).map { entities ->
            entities.toBodyweightDomainModels()
        }
    }

    override suspend fun getEntryForDate(date: Long): Bodyweight? {
        return bodyweightDao.getEntryForDate(date)?.toDomainModel()
    }

    override suspend fun getAverageWeightInRange(startDate: Long, endDate: Long): Float? {
        return bodyweightDao.getAverageWeightInRange(startDate, endDate)
    }

    override suspend fun getMinWeight(): Float? {
        return bodyweightDao.getMinWeight()
    }

    override suspend fun getMaxWeight(): Float? {
        return bodyweightDao.getMaxWeight()
    }

    override suspend fun getEntryCount(): Int {
        return bodyweightDao.getEntryCount()
    }
}

