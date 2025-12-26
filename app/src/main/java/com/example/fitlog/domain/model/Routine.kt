package com.example.fitlog.domain.model

/**
 * Domain model representing a workout routine/template
 */
data class Routine(
    val id: Int = 0,
    val name: String,
    val isTemplate: Boolean = false,
    val dayOrder: Int = 0,
    val exercises: List<RoutineExercise> = emptyList()
) {
    val exerciseCount: Int
        get() = exercises.size

    val totalTargetSets: Int
        get() = exercises.sumOf { it.targetSets }
}

/**
 * Domain model representing an exercise within a routine
 */
data class RoutineExercise(
    val id: Int = 0,
    val routineId: Int,
    val exerciseId: Int,
    val exercise: Exercise? = null,
    val targetSets: Int = 3,
    val targetReps: String = "8-12",
    val orderIndex: Int = 0
)

