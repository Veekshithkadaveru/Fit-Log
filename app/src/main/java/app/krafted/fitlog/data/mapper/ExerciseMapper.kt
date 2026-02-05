package app.krafted.fitlog.data.mapper

import app.krafted.fitlog.data.database.entity.ExerciseEntity
import app.krafted.fitlog.domain.model.Equipment
import app.krafted.fitlog.domain.model.Exercise
import app.krafted.fitlog.domain.model.ExerciseCategory
import app.krafted.fitlog.domain.model.MuscleGroup

/**
 * Mapper functions to convert between ExerciseEntity (data layer) and Exercise (domain layer).
 * 
 * The Entity uses Strings for database storage, while the Domain model uses 
 * type-safe Enums. These mappers handle the conversion between the two.
 */

/**
 * Convert ExerciseEntity (database) to Exercise (domain model)
 */
fun ExerciseEntity.toDomainModel(): Exercise {
    return Exercise(
        id = id,
        name = name,
        primaryMuscle = MuscleGroup.fromString(primaryMuscle),
        secondaryMuscles = parseSecondaryMuscles(secondaryMuscles),
        category = ExerciseCategory.fromString(category),
        equipment = Equipment.fromString(equipment),
        thumbnailRes = thumbnailRes
    )
}

/**
 * Convert Exercise (domain model) to ExerciseEntity (database)
 */
fun Exercise.toEntity(): ExerciseEntity {
    return ExerciseEntity(
        id = id,
        name = name,
        primaryMuscle = primaryMuscle.name,
        secondaryMuscles = secondaryMuscles.joinToString(",") { it.name },
        category = category.name,
        equipment = equipment.name,
        thumbnailRes = thumbnailRes
    )
}

/**
 * Convert a list of ExerciseEntity to a list of Exercise domain models
 */
fun List<ExerciseEntity>.toDomainModels(): List<Exercise> {
    return map { it.toDomainModel() }
}

/**
 * Convert a list of Exercise domain models to a list of ExerciseEntity
 */
fun List<Exercise>.toEntities(): List<ExerciseEntity> {
    return map { it.toEntity() }
}

/**
 * Parse comma-separated muscle group string into a list of MuscleGroup enums
 */
private fun parseSecondaryMuscles(secondaryMuscles: String): List<MuscleGroup> {
    if (secondaryMuscles.isBlank()) {
        return emptyList()
    }
    return secondaryMuscles
        .split(",")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
        .map { MuscleGroup.fromString(it) }
}


