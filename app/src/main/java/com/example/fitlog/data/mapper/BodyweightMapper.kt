package com.example.fitlog.data.mapper

import com.example.fitlog.data.database.entity.BodyweightEntity
import com.example.fitlog.domain.model.Bodyweight

fun BodyweightEntity.toDomainModel(): Bodyweight {
    return Bodyweight(
        id = id,
        date = date,
        weight = weight,
        notes = notes
    )
}

fun Bodyweight.toEntity(): BodyweightEntity {
    return BodyweightEntity(
        id = id,
        date = date,
        weight = weight,
        notes = notes
    )
}

fun List<BodyweightEntity>.toBodyweightDomainModels(): List<Bodyweight> {
    return map { it.toDomainModel() }
}

