package com.example.fitlog.data.mapper

import com.example.fitlog.data.database.entity.CardioSessionEntity
import com.example.fitlog.data.database.entity.WorkoutEntity
import com.example.fitlog.data.database.entity.WorkoutSetEntity
import com.example.fitlog.domain.model.CardioSession
import com.example.fitlog.domain.model.CardioType
import com.example.fitlog.domain.model.Workout
import com.example.fitlog.domain.model.WorkoutSet


fun WorkoutEntity.toDomainModel(
    sets: List<WorkoutSet> = emptyList(),
    cardioSessions: List<CardioSession> = emptyList()
): Workout {
    return Workout(
        id = id,
        startTime = startTime,
        endTime = endTime,
        routineId = routineId,
        notes = notes,
        isCardio = isCardio,
        sets = sets,
        cardioSessions = cardioSessions
    )
}

fun Workout.toEntity(): WorkoutEntity {
    return WorkoutEntity(
        id = id,
        startTime = startTime,
        endTime = endTime,
        routineId = routineId,
        notes = notes,
        isCardio = isCardio
    )
}

fun List<WorkoutEntity>.toDomainModels(): List<Workout> {
    return map { it.toDomainModel() }
}


fun WorkoutSetEntity.toDomainModel(): WorkoutSet {
    return WorkoutSet(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        weight = weight,
        reps = reps,
        isCompleted = isCompleted,
        isPR = isPR,
        setOrder = setOrder
    )
}

fun WorkoutSet.toEntity(): WorkoutSetEntity {
    return WorkoutSetEntity(
        id = id,
        workoutId = workoutId,
        exerciseId = exerciseId,
        weight = weight,
        reps = reps,
        isCompleted = isCompleted,
        isPR = isPR,
        setOrder = setOrder
    )
}

fun List<WorkoutSetEntity>.toWorkoutSetDomainModels(): List<WorkoutSet> {
    return map { it.toDomainModel() }
}

fun List<WorkoutSet>.toWorkoutSetEntities(): List<WorkoutSetEntity> {
    return map { it.toEntity() }
}

fun CardioSessionEntity.toDomainModel(): CardioSession {
    return CardioSession(
        id = id,
        workoutId = workoutId,
        type = CardioType.fromString(type),
        durationMinutes = durationMinutes,
        distance = distance,
        rpe = rpe
    )
}

fun CardioSession.toEntity(): CardioSessionEntity {
    return CardioSessionEntity(
        id = id,
        workoutId = workoutId,
        type = type.name,
        durationMinutes = durationMinutes,
        distance = distance,
        rpe = rpe
    )
}

fun List<CardioSessionEntity>.toCardioDomainModels(): List<CardioSession> {
    return map { it.toDomainModel() }
}
