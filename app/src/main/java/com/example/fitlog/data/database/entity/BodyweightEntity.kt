package com.example.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bodyweight")
data class BodyweightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long, // Epoch milliseconds (start of day)
    val weight: Float, // In user's preferred unit (kg)
    val notes: String? = null
)

