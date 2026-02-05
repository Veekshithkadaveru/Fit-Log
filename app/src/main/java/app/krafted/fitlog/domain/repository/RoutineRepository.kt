package app.krafted.fitlog.domain.repository

import app.krafted.fitlog.domain.model.Routine
import app.krafted.fitlog.domain.model.RoutineExercise
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Routine and RoutineExercise operations.
 */
interface RoutineRepository {

    suspend fun insertRoutine(routine: Routine): Long

    suspend fun updateRoutine(routine: Routine)

    suspend fun deleteRoutine(routine: Routine)

    suspend fun getRoutineById(id: Int): Routine?

    fun getRoutineByIdFlow(id: Int): Flow<Routine?>

    fun getUserRoutines(): Flow<List<Routine>>

    fun getTemplateRoutines(): Flow<List<Routine>>

    fun getAllRoutines(): Flow<List<Routine>>

    suspend fun insertRoutineExercise(routineExercise: RoutineExercise): Long

    suspend fun insertRoutineExercises(routineExercises: List<RoutineExercise>)

    suspend fun updateRoutineExercise(routineExercise: RoutineExercise)

    suspend fun deleteRoutineExercise(routineExercise: RoutineExercise)

    suspend fun deleteAllExercisesFromRoutine(routineId: Int)

    fun getExercisesForRoutine(routineId: Int): Flow<List<RoutineExercise>>

    suspend fun getExercisesForRoutineOnce(routineId: Int): List<RoutineExercise>

    suspend fun getExerciseCountForRoutine(routineId: Int): Int

    suspend fun reorderExercises(routineId: Int, exerciseIds: List<Int>)

    fun getAllExercises(): Flow<List<app.krafted.fitlog.domain.model.Exercise>>

    suspend fun cloneRoutineFromTemplate(template: Routine): Int
}
