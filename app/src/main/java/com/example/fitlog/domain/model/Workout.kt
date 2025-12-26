package com.example.fitlog.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Domain model representing a workout session
 */
data class Workout(
    val id: Int = 0,
    val startTime: Long,
    val endTime: Long? = null,
    val routineId: Int? = null,
    val notes: String? = null,
    val isCardio: Boolean = false,
    val sets: List<WorkoutSet> = emptyList(),
    val cardioSessions: List<CardioSession> = emptyList()
) {
    val isInProgress: Boolean
        get() = endTime == null

    val durationMillis: Long?
        get() = endTime?.let { it - startTime }

    val durationMinutes: Int?
        get() = durationMillis?.let { (it / 60000).toInt() }

    val startDateTime: LocalDateTime
        @RequiresApi(Build.VERSION_CODES.O)
        get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(startTime), ZoneId.systemDefault())

    val endDateTime: LocalDateTime?
        @RequiresApi(Build.VERSION_CODES.O)
        get() = endTime?.let { LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault()) }

    val totalVolume: Float
        get() = sets.filter { it.isCompleted }.sumOf { (it.weight * it.reps).toDouble() }.toFloat()

    val completedSets: Int
        get() = sets.count { it.isCompleted }

    val totalSets: Int
        get() = sets.size

    val exerciseCount: Int
        get() = sets.map { it.exerciseId }.distinct().size

    val prCount: Int
        get() = sets.count { it.isPR }
}

/**
 * Domain model representing a single set within a workout
 */
data class WorkoutSet(
    val id: Int = 0,
    val workoutId: Int,
    val exerciseId: Int,
    val weight: Float = 0f,
    val reps: Int = 0,
    val isCompleted: Boolean = false,
    val isPR: Boolean = false,
    val setOrder: Int = 0
) {
    val volume: Float
        get() = weight * reps
}

/**
 * Domain model representing a cardio session within a workout
 */
data class CardioSession(
    val id: Int = 0,
    val workoutId: Int,
    val type: CardioType,
    val durationMinutes: Int,
    val distance: Float? = null,
    val rpe: Int? = null
)

/**
 * Types of cardio exercises
 */
enum class CardioType(val displayName: String) {
    TREADMILL("Treadmill"),
    CYCLING("Cycling"),
    RUNNING("Running"),
    ROWING("Rowing"),
    WALKING("Walking"),
    ELLIPTICAL("Elliptical"),
    STAIR_CLIMBER("Stair Climber"),
    SWIMMING("Swimming"),
    JUMP_ROPE("Jump Rope"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): CardioType {
            return entries.find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) 
            } ?: OTHER
        }
    }
}


