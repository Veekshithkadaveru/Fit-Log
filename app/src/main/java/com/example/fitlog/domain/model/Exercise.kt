package com.example.fitlog.domain.model

/**
 * Domain model representing an exercise in the workout library
 */
data class Exercise(
    val id: Int = 0,
    val name: String,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup>,
    val category: ExerciseCategory,
    val equipment: Equipment,
    val thumbnailRes: String? = null
)

/**
 * Muscle groups that can be targeted by exercises
 */
enum class MuscleGroup(val displayName: String) {
    CHEST("Chest"),
    BACK("Back"),
    SHOULDERS("Shoulders"),
    BICEPS("Biceps"),
    TRICEPS("Triceps"),
    LEGS("Legs"),
    QUADRICEPS("Quadriceps"),
    HAMSTRINGS("Hamstrings"),
    GLUTES("Glutes"),
    CALVES("Calves"),
    CORE("Core"),
    FOREARMS("Forearms"),
    TRAPS("Traps"),
    LATS("Lats"),
    REAR_DELTS("Rear Delts");

    companion object {
        fun fromString(value: String): MuscleGroup {
            return entries.find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) 
            } ?: CHEST
        }
    }
}

/**
 * Exercise categories (compound vs isolation)
 */
enum class ExerciseCategory(val displayName: String) {
    COMPOUND("Compound"),
    ISOLATION("Isolation"),
    CARDIO("Cardio"),
    STRETCHING("Stretching");

    companion object {
        fun fromString(value: String): ExerciseCategory {
            return entries.find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) 
            } ?: COMPOUND
        }
    }
}

/**
 * Equipment types for exercises
 */
enum class Equipment(val displayName: String) {
    BARBELL("Barbell"),
    DUMBBELL("Dumbbell"),
    CABLE("Cable"),
    MACHINE("Machine"),
    BODYWEIGHT("Bodyweight"),
    KETTLEBELL("Kettlebell"),
    RESISTANCE_BAND("Resistance Band"),
    EZ_BAR("EZ Bar"),
    SMITH_MACHINE("Smith Machine"),
    OTHER("Other");

    companion object {
        fun fromString(value: String): Equipment {
            return entries.find { 
                it.name.equals(value, ignoreCase = true) || 
                it.displayName.equals(value, ignoreCase = true) 
            } ?: OTHER
        }
    }
}

