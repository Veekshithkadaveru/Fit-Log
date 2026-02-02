package com.example.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutEntity::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("workoutId"),
        Index("exerciseId")
    ]
)
data class WorkoutSetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val workoutId: Int,
    val exerciseId: Int,
    val weight: Float = 0f, // In user's preferred unit (kg)
    val reps: Int = 0,
    val isCompleted: Boolean = false,
    val isPR: Boolean = false, // Personal Record flag
    val setOrder: Int = 0 // Order of set within the exercise
)

