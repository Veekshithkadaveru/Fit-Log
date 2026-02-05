package app.krafted.fitlog.data.repository

import app.krafted.fitlog.data.database.dao.PersonalRecordDao
import app.krafted.fitlog.data.mapper.toDomainModel
import app.krafted.fitlog.data.mapper.toEntity
import app.krafted.fitlog.data.mapper.toPersonalRecordDomainModels
import app.krafted.fitlog.domain.model.PersonalRecord
import app.krafted.fitlog.domain.repository.PersonalRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonalRecordRepositoryImpl @Inject constructor(
    private val personalRecordDao: PersonalRecordDao
) : PersonalRecordRepository {

    override suspend fun insert(record: PersonalRecord): Long {
        return personalRecordDao.insert(record.toEntity())
    }

    override suspend fun update(record: PersonalRecord) {
        personalRecordDao.update(record.toEntity())
    }

    override suspend fun delete(record: PersonalRecord) {
        personalRecordDao.delete(record.toEntity())
    }

    override suspend fun getById(id: Int): PersonalRecord? {
        return personalRecordDao.getById(id)?.toDomainModel()
    }

    override suspend fun getByExerciseId(exerciseId: Int): PersonalRecord? {
        return personalRecordDao.getByExerciseId(exerciseId)?.toDomainModel()
    }

    override fun getByExerciseIdFlow(exerciseId: Int): Flow<PersonalRecord?> {
        return personalRecordDao.getByExerciseIdFlow(exerciseId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getAllRecords(): Flow<List<PersonalRecord>> {
        return personalRecordDao.getAllRecords().map { entities ->
            entities.toPersonalRecordDomainModels()
        }
    }

    override fun getRecentRecords(limit: Int): Flow<List<PersonalRecord>> {
        return personalRecordDao.getRecentRecords(limit).map { entities ->
            entities.toPersonalRecordDomainModels()
        }
    }

    override fun getRecordsInDateRange(startDate: Long, endDate: Long): Flow<List<PersonalRecord>> {
        return personalRecordDao.getRecordsInDateRange(startDate, endDate).map { entities ->
            entities.toPersonalRecordDomainModels()
        }
    }

    override suspend fun getRecordCountInRange(startDate: Long, endDate: Long): Int {
        return personalRecordDao.getRecordCountInRange(startDate, endDate)
    }

    override suspend fun getTotalRecordCount(): Int {
        return personalRecordDao.getTotalRecordCount()
    }

    override suspend fun upsertRecord(
        exerciseId: Int,
        maxWeight: Float,
        maxReps: Int,
        maxVolume: Float,
        achievedDate: Long
    ) {
        personalRecordDao.upsertRecord(exerciseId, maxWeight, maxReps, maxVolume, achievedDate)
    }

    override suspend fun isNewRecord(exerciseId: Int, weight: Float, reps: Int): Boolean {
        return personalRecordDao.isNewRecord(exerciseId, weight, reps) == 1
    }
}

