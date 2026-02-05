package app.krafted.fitlog.data.mapper

import app.krafted.fitlog.data.database.entity.RepRangeRecordEntity
import app.krafted.fitlog.domain.model.RepRange
import app.krafted.fitlog.domain.model.RepRangeRecord

fun RepRangeRecordEntity.toDomainModel(): RepRangeRecord {
    return RepRangeRecord(
        id = id,
        exerciseId = exerciseId,
        exercise = null,
        repRange = RepRange.valueOf(repRange),
        bestWeight = bestWeight,
        repsAtBestWeight = repsAtBestWeight,
        estimated1RM = estimated1RM,
        achievedDate = achievedDate
    )
}

fun RepRangeRecord.toEntity(): RepRangeRecordEntity {
    return RepRangeRecordEntity(
        id = id,
        exerciseId = exerciseId,
        repRange = repRange.name,
        bestWeight = bestWeight,
        repsAtBestWeight = repsAtBestWeight,
        estimated1RM = estimated1RM,
        achievedDate = achievedDate
    )
}

fun List<RepRangeRecordEntity>.toRepRangeRecordDomainModels(): List<RepRangeRecord> {
    return map { it.toDomainModel() }
}
