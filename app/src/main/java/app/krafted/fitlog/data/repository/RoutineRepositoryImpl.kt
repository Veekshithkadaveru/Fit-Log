package app.krafted.fitlog.data.repository

import app.krafted.fitlog.data.database.dao.RoutineDao
import app.krafted.fitlog.data.mapper.toDomainModel
import app.krafted.fitlog.data.mapper.toEntity
import app.krafted.fitlog.data.mapper.toRoutineDomainModels
import app.krafted.fitlog.data.mapper.toRoutineExerciseDomainModels
import app.krafted.fitlog.data.mapper.toRoutineExerciseWithDetailsDomainModels
import app.krafted.fitlog.data.mapper.toRoutineExerciseEntities
import app.krafted.fitlog.domain.model.Routine
import app.krafted.fitlog.domain.model.RoutineExercise
import app.krafted.fitlog.domain.repository.RoutineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import app.krafted.fitlog.data.database.AppDatabase
import androidx.room.withTransaction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoutineRepositoryImpl @Inject constructor(
    private val routineDao: RoutineDao,
    private val exerciseDao: app.krafted.fitlog.data.database.dao.ExerciseDao,
    private val database: AppDatabase
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

    override fun getAllExercises(): Flow<List<app.krafted.fitlog.domain.model.Exercise>> {
        return exerciseDao.getAllExercises().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun cloneRoutineFromTemplate(template: Routine): Int {
        return database.withTransaction {
            // 1. Create new routine from template
            val newRoutine = template.copy(
                id = 0, // Reset ID for auto-generation
                isTemplate = false,
                dayOrder = 0
            )
            val newRoutineId = routineDao.insertRoutine(newRoutine.toEntity())

            // 2. Copy exercises
            if (template.exercises.isNotEmpty()) {
                val newExercises = template.exercises.map {
                    it.copy(
                        id = 0,
                        routineId = newRoutineId.toInt(),
                        exercise = null // Don't duplicate the full exercise object
                    )
                }
                routineDao.insertRoutineExercises(newExercises.toRoutineExerciseEntities())
            }
            newRoutineId.toInt()
        }
    }
}

