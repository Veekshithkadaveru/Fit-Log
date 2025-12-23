package com.example.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cardio_sessions",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class CardioSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val workoutId: Int,
    val type: String, // e.g., "Treadmill", "Cycling", "Running", "Rowing", "Walking"
    val durationMinutes: Int,
    val distance: Float? = null, // Optional - in km or miles
    val rpe: Int? = null // Rate of Perceived Exertion (1-10)
)

