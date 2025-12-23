package com.example.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "personal_records",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId")]
)
data class PersonalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseId: Int,
    val maxWeight: Float = 0f, // Max weight ever lifted
    val maxReps: Int = 0, // Max reps at any weight
    val maxVolume: Float = 0f, // Max total volume (weight × reps) in a single set
    val achievedDate: Long // When the PR was achieved
)

