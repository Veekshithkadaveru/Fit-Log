package com.example.fitlog.data.repository

import com.example.fitlog.data.database.dao.RoutineDao
import com.example.fitlog.data.mapper.toDomainModel
import com.example.fitlog.data.mapper.toEntity
import com.example.fitlog.data.mapper.toRoutineDomainModels
import com.example.fitlog.data.mapper.toRoutineExerciseDomainModels
import com.example.fitlog.data.mapper.toRoutineExerciseEntities
import com.example.fitlog.domain.model.Routine
import com.example.fitlog.domain.model.RoutineExercise
import com.example.fitlog.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao
) : RoutineRepository {

    override suspend fun insertRoutine(routine: Routine): Long {
        return routineDao.insertRoutine(routine.toEntity())
    }

    override suspend fun updateRoutine(routine: Routine) {
        routineDao.updateRoutine(routine.toEntity())
    }

    override suspend fun deleteRoutine(routine: Routine) {
        routineDao.deleteRoutine(routine.toEntity())
    }

    override suspend fun getRoutineById(id: Int): Routine? {
        val entity = routineDao.getRoutineById(id) ?: return null
        val exercises = routineDao.getExercisesForRoutineOnce(id).toRoutineExerciseDomainModels()
        return entity.toDomainModel(exercises = exercises)
    }

    override fun getRoutineByIdFlow(id: Int): Flow<Routine?> {
        return routineDao.getRoutineByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getUserRoutines(): Flow<List<Routine>> {
        return routineDao.getUserRoutines().map { entities ->
            entities.toRoutineDomainModels()
        }
    }

    override fun getTemplateRoutines(): Flow<List<Routine>> {
        return routineDao.getTemplateRoutines().map { entities ->
            entities.toRoutineDomainModels()
        }
    }

    override fun getAllRoutines(): Flow<List<Routine>> {
        return routineDao.getAllRoutines().map { entities ->
            entities.toRoutineDomainModels()
        }
    }

    override suspend fun insertRoutineExercise(routineExercise: RoutineExercise): Long {
        return routineDao.insertRoutineExercise(routineExercise.toEntity())
    }

    override suspend fun insertRoutineExercises(routineExercises: List<RoutineExercise>) {
        routineDao.insertRoutineExercises(routineExercises.toRoutineExerciseEntities())
    }

    override suspend fun updateRoutineExercise(routineExercise: RoutineExercise) {
        routineDao.updateRoutineExercise(routineExercise.toEntity())
    }

    override suspend fun deleteRoutineExercise(routineExercise: RoutineExercise) {
        routineDao.deleteRoutineExercise(routineExercise.toEntity())
    }

    override suspend fun deleteAllExercisesFromRoutine(routineId: Int) {
        routineDao.deleteAllExercisesFromRoutine(routineId)
    }

    override fun getExercisesForRoutine(routineId: Int): Flow<List<RoutineExercise>> {
        return routineDao.getExercisesForRoutine(routineId).map { entities ->
            entities.toRoutineExerciseDomainModels()
        }
    }

    override suspend fun getExercisesForRoutineOnce(routineId: Int): List<RoutineExercise> {
        return routineDao.getExercisesForRoutineOnce(routineId).toRoutineExerciseDomainModels()
    }

    override suspend fun getExerciseCountForRoutine(routineId: Int): Int {
        return routineDao.getExerciseCountForRoutine(routineId)
    }

    override suspend fun reorderExercises(routineId: Int, exerciseIds: List<Int>) {
        routineDao.reorderExercises(routineId, exerciseIds)
    }
}

