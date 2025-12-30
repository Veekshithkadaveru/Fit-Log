package com.example.fitlog.data.mapper

import com.example.fitlog.data.database.entity.RoutineEntity
import com.example.fitlog.data.database.entity.RoutineExerciseEntity
import com.example.fitlog.domain.model.Routine
import com.example.fitlog.domain.model.RoutineExercise

fun RoutineEntity.toDomainModel(
    exercises: List<RoutineExercise> = emptyList()
): Routine {
    return Routine(
        id = id,
        name = name,
        isTemplate = isTemplate,
        dayOrder = dayOrder,
        exercises = exercises
    )
}

fun Routine.toEntity(): RoutineEntity {
    return RoutineEntity(
        id = id,
        name = name,
        isTemplate = isTemplate,
        dayOrder = dayOrder
    )
}

fun List<RoutineEntity>.toRoutineDomainModels(): List<Routine> {
    return map { it.toDomainModel() }
}

fun RoutineExerciseEntity.toDomainModel(): RoutineExercise {
    return RoutineExercise(
        id = id,
        routineId = routineId,
        exerciseId = exerciseId,
        exercise = null,
        targetSets = targetSets,
        targetReps = targetReps,
        orderIndex = orderIndex
    )
}

fun RoutineExercise.toEntity(): RoutineExerciseEntity {
    return RoutineExerciseEntity(
        id = id,
        routineId = routineId,
        exerciseId = exerciseId,
        targetSets = targetSets,
        targetReps = targetReps,
        orderIndex = orderIndex
    )
}

fun List<RoutineExerciseEntity>.toRoutineExerciseDomainModels(): List<RoutineExercise> {
    return map { it.toDomainModel() }
}

fun List<RoutineExercise>.toRoutineExerciseEntities(): List<RoutineExerciseEntity> {
    return map { it.toEntity() }
}

