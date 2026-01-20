package com.example.fitlog.domain.model

data class MuscleGroupStats(
    val muscleGroup: MuscleGroup,
    val totalVolume: Float,
    val totalSets: Int,
    val weeklyFrequency: Int,
    val lastWorkedDate: Long?,
    val averageVolumePerWorkout: Float,
    val exerciseCount: Int
) {
    val volumePerSet: Float
        get() = if (totalSets > 0) totalVolume / totalSets else 0f

    val daysSinceLastWorked: Int?
        get() = lastWorkedDate?.let {
            val currentTime = System.currentTimeMillis()
            ((currentTime - it) / (1000 * 60 * 60 * 24)).toInt()
        }
}

data class MuscleGroupAnalytics(
    val muscleStats: List<MuscleGroupStats>,
    val imbalances: List<MuscleGroupImbalance>,
    val recommendations: List<TrainingRecommendation>
)

data class MuscleGroupImbalance(
    val strongerMuscle: MuscleGroup,
    val weakerMuscle: MuscleGroup,
    val volumeRatio: Float,
    val severity: ImbalanceSeverity
) {
    val description: String
        get() = "${strongerMuscle.displayName} is trained ${String.format("%.1f", volumeRatio)}x more than ${weakerMuscle.displayName}"
}

enum class ImbalanceSeverity {
    MINOR,      // 1.5x - 2x ratio
    MODERATE,   // 2x - 3x ratio
    SEVERE      // 3x+ ratio
}

data class TrainingRecommendation(
    val muscleGroup: MuscleGroup,
    val type: RecommendationType,
    val reason: String,
    val priority: RecommendationPriority
)

enum class RecommendationType {
    INCREASE_FREQUENCY,     // Muscle is undertrained
    INCREASE_VOLUME,        // Low volume compared to other muscles
    ALLOW_RECOVERY,         // Trained too frequently
    MAINTAIN,               // Balanced training
    ADDRESS_IMBALANCE       // Part of an imbalance pair
}

enum class RecommendationPriority {
    HIGH,
    MEDIUM,
    LOW
}

data class WeeklyMuscleFrequency(
    val muscleGroup: MuscleGroup,
    val workoutsPerWeek: Int,
    val setsPerWeek: Int,
    val totalVolumePerWeek: Float
)
