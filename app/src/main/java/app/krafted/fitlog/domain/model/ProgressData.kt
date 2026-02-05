package app.krafted.fitlog.domain.model

/**
 * Data point for tracking max weight progression over time for an exercise
 */
data class WeightProgressPoint(
    val date: Long,
    val maxWeight: Float,
    val exerciseId: Int,
    val exerciseName: String? = null
)

/**
 * Data point for tracking volume (weight × reps × sets) over time
 */
data class VolumeProgressPoint(
    val date: Long,
    val totalVolume: Float
)

/**
 * Data point for workout count aggregated by period (week/month)
 */
data class WorkoutCountPoint(
    val periodStart: Long,
    val count: Int
)

/**
 * Data point for workout duration trends
 */
data class DurationProgressPoint(
    val date: Long,
    val durationMinutes: Long
)

/**
 * Aggregated progress data for a specific exercise
 */
data class ExerciseProgressData(
    val exerciseId: Int,
    val exerciseName: String,
    val weightProgress: List<WeightProgressPoint>,
    val volumeProgress: List<VolumeProgressPoint>,
    val bestWeight: Float?,
    val bestVolume: Float?,
    val totalWorkouts: Int
)

/**
 * Summary statistics for the progress dashboard
 */
data class ProgressSummary(
    val workoutsThisWeek: Int,
    val workoutsThisMonth: Int,
    val workoutsThisYear: Int,
    val totalWorkouts: Int,
    val prsThisMonth: Int,
    val prsThisYear: Int,
    val totalVolumeThisWeek: Float,
    val totalVolumeThisMonth: Float,
    val totalVolumeThisYear: Float,
    val averageDurationThisWeek: Long,
    val averageDurationThisMonth: Long,
    val averageDurationThisYear: Long,
    val averageWorkoutDurationMinutes: Long,
    val currentStreak: Int
)

/**
 * Date range filter options for progress charts
 */
enum class DateRangeFilter(val days: Int, val displayName: String) {
    LAST_7_DAYS(7, "7 Days"),
    LAST_30_DAYS(30, "30 Days"),
    LAST_YEAR(365, "1 Year")
}
