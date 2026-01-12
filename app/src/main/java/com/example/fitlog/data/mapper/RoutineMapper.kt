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

fun List<com.example.fitlog.data.database.entity.RoutineExerciseWithDetails>.toRoutineExerciseWithDetailsDomainModels(): List<RoutineExercise> {
    return map {
        val domainExercise = it.exercise?.let { entity ->
            com.example.fitlog.domain.model.Exercise(
                id = entity.id,
                name = entity.name,
                primaryMuscle = com.example.fitlog.domain.model.MuscleGroup.fromString(entity.primaryMuscle),
                secondaryMuscles = entity.secondaryMuscles.split(",").mapNotNull { 
                    if (it.isBlank()) null else com.example.fitlog.domain.model.MuscleGroup.fromString(it.trim()) 
                },
                category = com.example.fitlog.domain.model.ExerciseCategory.fromString(entity.category),
                equipment = com.example.fitlog.domain.model.Equipment.fromString(entity.equipment),
                thumbnailRes = entity.thumbnailRes
            )
        }
        
        it.routineExercise.toDomainModel().copy(
            exercise = domainExercise
        )
    }
fun com.example.fitlog.data.database.entity.RoutineExerciseWithDetails.toDomainModel(): RoutineExercise {
    return routineExercise.toDomainModel().copy(
        exercise = exercise.toDomainModel()
    )
}

fun List<com.example.fitlog.data.database.entity.RoutineExerciseWithDetails>.toRoutineExerciseWithDetailsDomainModels(): List<RoutineExercise> {
    return map { it.toDomainModel() }
}

