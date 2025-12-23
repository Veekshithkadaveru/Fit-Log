package com.example.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "routine_exercises",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
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
        Index("routineId"),
        Index("exerciseId")
    ]
)
data class RoutineExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val routineId: Int,
    val exerciseId: Int,
    val targetSets: Int = 3,
    val targetReps: String = "8-12", // Can be range like "8-12" or fixed like "10"
    val orderIndex: Int = 0 // For ordering exercises within a routine
)

