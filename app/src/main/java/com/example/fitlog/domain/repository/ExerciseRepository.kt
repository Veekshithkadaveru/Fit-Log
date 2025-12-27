package com.example.fitlog.domain.repository

import com.example.fitlog.domain.model.Equipment
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.model.ExerciseCategory
import com.example.fitlog.domain.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Exercise operations.
 * 
 * This interface defines the contract for accessing exercise data.
 * The implementation resides in the data layer and uses Room DAOs internally.
 */
interface ExerciseRepository {

    /**
     * Insert a new exercise into the database
     * @param exercise The exercise to insert
     * @return The ID of the newly inserted exercise
     */
    suspend fun insertExercise(exercise: Exercise): Long

    /**
     * Insert multiple exercises at once (bulk insert)
     * @param exercises List of exercises to insert
     */
    suspend fun insertAllExercises(exercises: List<Exercise>)

    /**
     * Update an existing exercise
     * @param exercise The exercise with updated values
     */
    suspend fun updateExercise(exercise: Exercise)

    /**
     * Delete an exercise from the database
     * @param exercise The exercise to delete
     */
    suspend fun deleteExercise(exercise: Exercise)

    /**
     * Get a single exercise by its ID
     * @param id The exercise ID
     * @return The exercise or null if not found
     */
    suspend fun getExerciseById(id: Int): Exercise?

    /**
     * Get a single exercise by ID as a Flow (reactive updates)
     * @param id The exercise ID
     * @return Flow emitting the exercise or null
     */
    fun getExerciseByIdFlow(id: Int): Flow<Exercise?>

    /**
     * Get all exercises, sorted by name
     * @return Flow emitting the list of all exercises
     */
    fun getAllExercises(): Flow<List<Exercise>>

    /**
     * Get exercises filtered by primary muscle group
     * @param muscle The muscle group to filter by
     * @return Flow emitting filtered exercises
     */
    fun getExercisesByMuscle(muscle: MuscleGroup): Flow<List<Exercise>>

    /**
     * Get exercises filtered by category (compound, isolation, etc.)
     * @param category The category to filter by
     * @return Flow emitting filtered exercises
     */
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>>

    /**
     * Get exercises filtered by equipment type
     * @param equipment The equipment type to filter by
     * @return Flow emitting filtered exercises
     */
    fun getExercisesByEquipment(equipment: Equipment): Flow<List<Exercise>>

    /**
     * Search exercises by name
     * @param query The search query string
     * @return Flow emitting matching exercises
     */
    fun searchExercises(query: String): Flow<List<Exercise>>

    /**
     * Get exercises with multiple optional filters applied
     * @param muscle Optional muscle group filter
     * @param equipment Optional equipment filter
     * @param category Optional category filter
     * @return Flow emitting filtered exercises
     */
    fun getFilteredExercises(
        muscle: MuscleGroup? = null,
        equipment: Equipment? = null,
        category: ExerciseCategory? = null
    ): Flow<List<Exercise>>

    /**
     * Get all distinct muscle groups that have exercises
     * @return Flow emitting list of muscle groups
     */
    fun getAllMuscleGroups(): Flow<List<MuscleGroup>>

    /**
     * Get all distinct equipment types used by exercises
     * @return Flow emitting list of equipment types
     */
    fun getAllEquipmentTypes(): Flow<List<Equipment>>

    /**
     * Get the total count of exercises in the database
     * @return The number of exercises
     */
    suspend fun getExerciseCount(): Int
}

