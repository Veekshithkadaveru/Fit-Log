package com.example.fitlog.domain.repository

import com.example.fitlog.domain.model.*

/**
 * Repository interface for progress analytics data.
 * Provides methods for fetching chart data and progress summaries.
 */
interface ProgressRepository {

    // ==================== Exercise-Specific Progress ====================

    /**
     * Get max weight progression for a specific exercise over time.
     * @param exerciseId The exercise to track
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return List of weight progress data points sorted by date
     */
    suspend fun getWeightProgressForExercise(
        exerciseId: Int,
        startDate: Long,
        endDate: Long
    ): List<WeightProgressPoint>

    /**
     * Get volume progression for a specific exercise over time.
     * @param exerciseId The exercise to track
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return List of volume progress data points sorted by date
     */
    suspend fun getVolumeProgressForExercise(
        exerciseId: Int,
        startDate: Long,
        endDate: Long
    ): List<VolumeProgressPoint>

    /**
     * Get complete progress data for an exercise including weight, volume, and stats.
     * @param exerciseId The exercise to track
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return Complete exercise progress data
     */
    suspend fun getExerciseProgressData(
        exerciseId: Int,
        startDate: Long,
        endDate: Long
    ): ExerciseProgressData?

    // ==================== Overall Progress ====================

    /**
     * Get total volume per workout day within a date range.
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return List of daily volume data points sorted by date
     */
    suspend fun getDailyVolume(
        startDate: Long,
        endDate: Long
    ): List<VolumeProgressPoint>

    /**
     * Get workout count aggregated by month.
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return List of monthly workout counts sorted by month
     */
    suspend fun getWorkoutCountByMonth(
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint>

    /**
     * Get workout count aggregated by week.
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return List of weekly workout counts sorted by week
     */
    suspend fun getWorkoutCountByWeek(
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint>

    /**
     * Get workout count aggregated by day.
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return List of daily workout counts sorted by day
     */
    suspend fun getWorkoutCountByDay(
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint>

    /**
     * Get workout durations within a date range.
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return List of workout duration data points sorted by date
     */
    suspend fun getWorkoutDurations(
        startDate: Long,
        endDate: Long
    ): List<DurationProgressPoint>

    // ==================== Summary Statistics ====================

    /**
     * Get progress summary statistics for the dashboard.
     * @return Summary including workout counts, PRs, volume, and streaks
     */
    suspend fun getProgressSummary(): ProgressSummary

    /**
     * Get average workout duration within a date range.
     * @param startDate Start of date range (epoch millis)
     * @param endDate End of date range (epoch millis)
     * @return Average duration in minutes, or null if no workouts
     */
    suspend fun getAverageWorkoutDuration(
        startDate: Long,
        endDate: Long
    ): Long?

    /**
     * Get the current workout streak (consecutive days with workouts).
     * @return Number of consecutive days with workouts ending today/yesterday
     */
    suspend fun getCurrentStreak(): Int

    /**
     * Get the first workout date for determining "all time" range.
     * @return Epoch millis of first workout, or null if no workouts
     */
    suspend fun getFirstWorkoutDate(): Long?

    // ==================== Utility ====================

    /**
     * Calculate date range based on filter selection.
     * @param filter The date range filter
     * @return Pair of (startDate, endDate) in epoch millis
     */
    fun getDateRangeForFilter(filter: DateRangeFilter): Pair<Long, Long>
}
