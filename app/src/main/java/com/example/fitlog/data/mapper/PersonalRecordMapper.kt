package com.example.fitlog.data.mapper

import com.example.fitlog.data.database.entity.PersonalRecordEntity
import com.example.fitlog.domain.model.PersonalRecord

fun PersonalRecordEntity.toDomainModel(): PersonalRecord {
    return PersonalRecord(
        id = id,
        exerciseId = exerciseId,
        exercise = null,
        maxWeight = maxWeight,
        maxReps = maxReps,
        maxVolume = maxVolume,
        achievedDate = achievedDate
    )
}

fun PersonalRecord.toEntity(): PersonalRecordEntity {
    return PersonalRecordEntity(
        id = id,
        exerciseId = exerciseId,
        maxWeight = maxWeight,
        maxReps = maxReps,
        maxVolume = maxVolume,
        achievedDate = achievedDate
    )
}

fun List<PersonalRecordEntity>.toPersonalRecordDomainModels(): List<PersonalRecord> {
    return map { it.toDomainModel() }
}

