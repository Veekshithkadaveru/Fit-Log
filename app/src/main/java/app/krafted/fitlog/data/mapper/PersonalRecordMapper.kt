package app.krafted.fitlog.data.mapper

import app.krafted.fitlog.data.database.entity.PersonalRecordEntity
import app.krafted.fitlog.domain.model.PersonalRecord

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

