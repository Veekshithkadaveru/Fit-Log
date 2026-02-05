package app.krafted.fitlog.data.mapper

import app.krafted.fitlog.data.database.entity.RoutineEntity
import app.krafted.fitlog.data.database.entity.RoutineExerciseEntity
import app.krafted.fitlog.domain.model.Routine
import app.krafted.fitlog.domain.model.RoutineExercise

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

fun List<app.krafted.fitlog.data.database.entity.RoutineExerciseWithDetails>.toRoutineExerciseWithDetailsDomainModels(): List<RoutineExercise> {
    return map {
        val domainExercise = it.exercise?.let { entity ->
            app.krafted.fitlog.domain.model.Exercise(
                id = entity.id,
                name = entity.name,
                primaryMuscle = app.krafted.fitlog.domain.model.MuscleGroup.fromString(entity.primaryMuscle),
                secondaryMuscles = entity.secondaryMuscles.split(",").mapNotNull { 
                    if (it.isBlank()) null else app.krafted.fitlog.domain.model.MuscleGroup.fromString(it.trim()) 
                },
                category = app.krafted.fitlog.domain.model.ExerciseCategory.fromString(entity.category),
                equipment = app.krafted.fitlog.domain.model.Equipment.fromString(entity.equipment),
                thumbnailRes = entity.thumbnailRes
            )
        }
        
        it.routineExercise.toDomainModel().copy(
            exercise = domainExercise
        )
    }
}

