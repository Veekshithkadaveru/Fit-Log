package com.example.fitlog.data.repository

import com.example.fitlog.data.database.dao.RepRangeRecordDao
import com.example.fitlog.data.mapper.toDomainModel
import com.example.fitlog.data.mapper.toEntity
import com.example.fitlog.data.mapper.toRepRangeRecordDomainModels
import com.example.fitlog.domain.model.RepRange
import com.example.fitlog.domain.model.RepRangeRecord
import com.example.fitlog.domain.repository.RepRangeRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RepRangeRecordRepositoryImpl @Inject constructor(
    private val repRangeRecordDao: RepRangeRecordDao
) : RepRangeRecordRepository {

    override suspend fun insert(record: RepRangeRecord): Long {
        return repRangeRecordDao.insert(record.toEntity())
    }

    override suspend fun update(record: RepRangeRecord) {
        repRangeRecordDao.update(record.toEntity())
    }

    override suspend fun delete(record: RepRangeRecord) {
        repRangeRecordDao.delete(record.toEntity())
    }

    override suspend fun getById(id: Int): RepRangeRecord? {
        return repRangeRecordDao.getById(id)?.toDomainModel()
    }

    override suspend fun getByExerciseIdAndRepRange(exerciseId: Int, repRange: RepRange): RepRangeRecord? {
        return repRangeRecordDao.getByExerciseIdAndRepRange(exerciseId, repRange.name)?.toDomainModel()
    }

    override suspend fun getByExerciseId(exerciseId: Int): List<RepRangeRecord> {
        return repRangeRecordDao.getByExerciseId(exerciseId).toRepRangeRecordDomainModels()
    }

    override fun getByExerciseIdFlow(exerciseId: Int): Flow<List<RepRangeRecord>> {
        return repRangeRecordDao.getByExerciseIdFlow(exerciseId).map { entities ->
            entities.toRepRangeRecordDomainModels()
        }
    }

    override fun getAllRecords(): Flow<List<RepRangeRecord>> {
        return repRangeRecordDao.getAllRecords().map { entities ->
            entities.toRepRangeRecordDomainModels()
        }
    }

    override fun getRecentRecords(limit: Int): Flow<List<RepRangeRecord>> {
        return repRangeRecordDao.getRecentRecords(limit).map { entities ->
            entities.toRepRangeRecordDomainModels()
        }
    }

    override fun getRecordsInDateRange(startDate: Long, endDate: Long): Flow<List<RepRangeRecord>> {
        return repRangeRecordDao.getRecordsInDateRange(startDate, endDate).map { entities ->
            entities.toRepRangeRecordDomainModels()
        }
    }

    override suspend fun getRecordCountInRange(startDate: Long, endDate: Long): Int {
        return repRangeRecordDao.getRecordCountInRange(startDate, endDate)
    }

    override suspend fun getTotalRecordCount(): Int {
        return repRangeRecordDao.getTotalRecordCount()
    }

    override suspend fun getBestEstimated1RM(exerciseId: Int): Float? {
        return repRangeRecordDao.getBestEstimated1RM(exerciseId)
    }

    override suspend fun upsertRecord(
        exerciseId: Int,
        repRange: RepRange,
        bestWeight: Float,
        repsAtBestWeight: Int,
        estimated1RM: Float,
        achievedDate: Long
    ) {
        repRangeRecordDao.upsertRecord(
            exerciseId = exerciseId,
            repRange = repRange.name,
            bestWeight = bestWeight,
            repsAtBestWeight = repsAtBestWeight,
            estimated1RM = estimated1RM,
            achievedDate = achievedDate
        )
    }
}
