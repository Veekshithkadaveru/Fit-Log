package com.example.fitlog.data.repository

import com.example.fitlog.data.database.dao.RoutineDao
import com.example.fitlog.data.mapper.toDomainModel
import com.example.fitlog.data.mapper.toEntity
import com.example.fitlog.data.mapper.toRoutineDomainModels
import com.example.fitlog.data.mapper.toRoutineExerciseDomainModels
import com.example.fitlog.data.mapper.toRoutineExerciseWithDetailsDomainModels
import com.example.fitlog.data.mapper.toRoutineExerciseEntities
import com.example.fitlog.domain.model.Routine
import com.example.fitlog.domain.model.RoutineExercise
import com.example.fitlog.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.example.fitlog.data.mapper.toRoutineExerciseWithDetailsDomainModels
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val exerciseDao: com.example.fitlog.data.database.dao.ExerciseDao
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
        // Fix: Fetch exercises with details using Relation
        val exercises = routineDao.getRoutineExercisesWithDetails(id).toRoutineExerciseWithDetailsDomainModels()
        return entity.toDomainModel(exercises = exercises)
    }

    override fun getRoutineByIdFlow(id: Int): Flow<Routine?> {
        return routineDao.getRoutineByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getUserRoutines(): Flow<List<Routine>> {
        return routineDao.getUserRoutines().map { entities ->
            entities.map { entity ->
                val exercises = routineDao.getRoutineExercisesWithDetails(entity.id).toRoutineExerciseWithDetailsDomainModels()
                entity.toDomainModel(exercises = exercises)
            }
        }
    }

    override fun getTemplateRoutines(): Flow<List<Routine>> {
        return routineDao.getTemplateRoutines().map { entities ->
            entities.map { entity ->
                val exercises = routineDao.getRoutineExercisesWithDetails(entity.id).toRoutineExerciseWithDetailsDomainModels()
                entity.toDomainModel(exercises = exercises)
            }
        }
    }

    override fun getAllRoutines(): Flow<List<Routine>> {
        return routineDao.getAllRoutines().map { entities ->
            entities.map { entity ->
                val exercises = routineDao.getRoutineExercisesWithDetails(entity.id).toRoutineExerciseWithDetailsDomainModels()
                entity.toDomainModel(exercises = exercises)
            }
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
        return routineDao.getRoutineExercisesWithDetails(routineId).toRoutineExerciseWithDetailsDomainModels()
    }

    override suspend fun getExerciseCountForRoutine(routineId: Int): Int {
        return routineDao.getExerciseCountForRoutine(routineId)
    }

    override suspend fun reorderExercises(routineId: Int, exerciseIds: List<Int>) {
        routineDao.reorderExercises(routineId, exerciseIds)
    }

    override fun getAllExercises(): Flow<List<com.example.fitlog.domain.model.Exercise>> {
        return exerciseDao.getAllExercises().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }
}

