package app.krafted.fitlog.data.repository

import app.krafted.fitlog.data.database.dao.ExerciseDao
import app.krafted.fitlog.data.mapper.toDomainModel
import app.krafted.fitlog.data.mapper.toDomainModels
import app.krafted.fitlog.data.mapper.toEntity
import app.krafted.fitlog.data.mapper.toEntities
import app.krafted.fitlog.domain.model.Equipment
import app.krafted.fitlog.domain.model.Exercise
import app.krafted.fitlog.domain.model.ExerciseCategory
import app.krafted.fitlog.domain.model.MuscleGroup
import app.krafted.fitlog.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ExerciseRepository.
 * 
 * This class bridges the domain layer with the data layer by:
 * - Using ExerciseDao for database operations
 * - Converting between Entity and Domain models using mappers
 * - Exposing reactive Flows for UI observation
 * 
 * @param exerciseDao The Room DAO for exercise database operations
 */
@Singleton
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao
) : ExerciseRepository {

    override suspend fun insertExercise(exercise: Exercise): Long {
        return exerciseDao.insert(exercise.toEntity())
    }

    override suspend fun insertAllExercises(exercises: List<Exercise>) {
        exerciseDao.insertAll(exercises.toEntities())
    }

    override suspend fun updateExercise(exercise: Exercise) {
        exerciseDao.update(exercise.toEntity())
    }

    override suspend fun deleteExercise(exercise: Exercise) {
        exerciseDao.delete(exercise.toEntity())
    }

    override suspend fun getExerciseById(id: Int): Exercise? {
        return exerciseDao.getById(id)?.toDomainModel()
    }

    override fun getExerciseByIdFlow(id: Int): Flow<Exercise?> {
        return exerciseDao.getByIdFlow(id).map { entity ->
            entity?.toDomainModel()
        }
    }

    override fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises().map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getExercisesByMuscle(muscle: MuscleGroup): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByMuscle(muscle.name).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByCategory(category.name).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getExercisesByEquipment(equipment: Equipment): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByEquipment(equipment.name).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun searchExercises(query: String): Flow<List<Exercise>> {
        return exerciseDao.searchExercises(query).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getFilteredExercises(
        muscle: MuscleGroup?,
        equipment: Equipment?,
        category: ExerciseCategory?
    ): Flow<List<Exercise>> {
        return exerciseDao.getFilteredExercises(
            muscle = muscle?.name,
            equipment = equipment?.name,
            category = category?.name
        ).map { entities ->
            entities.toDomainModels()
        }
    }

    override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> {
        return exerciseDao.getAllMuscleGroups().map { muscleStrings ->
            muscleStrings.map { MuscleGroup.fromString(it) }
        }
    }

    override fun getAllEquipmentTypes(): Flow<List<Equipment>> {
        return exerciseDao.getAllEquipmentTypes().map { equipmentStrings ->
            equipmentStrings.map { Equipment.fromString(it) }
        }
    }

    override suspend fun getExerciseCount(): Int {
        return exerciseDao.getExerciseCount()
    }
}

