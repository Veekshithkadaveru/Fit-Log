package com.example.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val isTemplate: Boolean = false, // True for prebuilt templates
    val dayOrder: Int = 0 // For multi-day routines (e.g., Day 1, Day 2)
)

