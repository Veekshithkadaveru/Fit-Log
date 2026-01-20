package com.example.fitlog.domain.usecase

import com.example.fitlog.data.database.dao.WorkoutDao
import com.example.fitlog.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.math.abs

class MuscleTrackingUseCase @Inject constructor(
    private val workoutDao: WorkoutDao
) {

    companion object {
        private const val DAYS_IN_WEEK = 7
        private const val MINOR_IMBALANCE_THRESHOLD = 1.5f
        private const val MODERATE_IMBALANCE_THRESHOLD = 2.0f
        private const val SEVERE_IMBALANCE_THRESHOLD = 3.0f
        private const val OPTIMAL_FREQUENCY_MIN = 2
        private const val OPTIMAL_FREQUENCY_MAX = 3
        private const val MIN_RECOVERY_DAYS = 2
    }

    /**
     * Calculate muscle group statistics for a given time range
     */
    suspend fun getMuscleGroupStats(
        startDate: Long,
        endDate: Long
    ): List<MuscleGroupStats> = withContext(Dispatchers.IO) {
        val activeMuscles = workoutDao.getActiveMuscleGroupsInRange(startDate, endDate)
        val daysInRange = TimeUnit.MILLISECONDS.toDays(endDate - startDate).toInt()
        val weeksInRange = if (daysInRange >= DAYS_IN_WEEK) daysInRange / DAYS_IN_WEEK else 1

        activeMuscles.mapNotNull { muscleGroupName ->
            try {
                val muscleGroup = MuscleGroup.valueOf(muscleGroupName)

                val totalVolume = workoutDao.getTotalVolumeForMuscleInRange(
                    muscleGroupName, startDate, endDate
                ) ?: 0f

                val totalSets = workoutDao.getTotalSetsForMuscleInRange(
                    muscleGroupName, startDate, endDate
                )

                val workoutCount = workoutDao.getWorkoutCountForMuscleInRange(
                    muscleGroupName, startDate, endDate
                )

                val lastWorkedDate = workoutDao.getLastWorkoutDateForMuscle(muscleGroupName)

                val exerciseCount = workoutDao.getExerciseCountForMuscleInRange(
                    muscleGroupName, startDate, endDate
                )

                // Calculate weekly frequency
                val weeklyFrequency = if (weeksInRange > 0) {
                    workoutCount / weeksInRange
                } else {
                    workoutCount
                }

                val averageVolumePerWorkout = if (workoutCount > 0) {
                    totalVolume / workoutCount
                } else {
                    0f
                }

                MuscleGroupStats(
                    muscleGroup = muscleGroup,
                    totalVolume = totalVolume,
                    totalSets = totalSets,
                    weeklyFrequency = weeklyFrequency,
                    lastWorkedDate = lastWorkedDate,
                    averageVolumePerWorkout = averageVolumePerWorkout,
                    exerciseCount = exerciseCount
                )
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    }

    /**
     * Calculate weekly muscle frequency for each muscle group
     */
    suspend fun getWeeklyMuscleFrequencies(
        startDate: Long,
        endDate: Long
    ): List<WeeklyMuscleFrequency> = withContext(Dispatchers.IO) {
        val stats = getMuscleGroupStats(startDate, endDate)
        val daysInRange = TimeUnit.MILLISECONDS.toDays(endDate - startDate).toInt()
        val weeksInRange = if (daysInRange >= DAYS_IN_WEEK) {
            daysInRange.toFloat() / DAYS_IN_WEEK
        } else {
            1f
        }

        stats.map { stat ->
            val workoutCount = workoutDao.getWorkoutCountForMuscleInRange(
                stat.muscleGroup.name,
                startDate,
                endDate
            )

            WeeklyMuscleFrequency(
                muscleGroup = stat.muscleGroup,
                workoutsPerWeek = (workoutCount / weeksInRange).toInt(),
                setsPerWeek = (stat.totalSets / weeksInRange).toInt(),
                totalVolumePerWeek = stat.totalVolume / weeksInRange
            )
        }
    }

    /**
     * Detect muscle group imbalances based on volume ratios
     */
    suspend fun detectMuscleImbalances(
        startDate: Long,
        endDate: Long
    ): List<MuscleGroupImbalance> = withContext(Dispatchers.IO) {
        val stats = getMuscleGroupStats(startDate, endDate)
        val imbalances = mutableListOf<MuscleGroupImbalance>()

        // Define antagonistic muscle pairs to check for imbalances
        val musclePairs = listOf(
            MuscleGroup.CHEST to MuscleGroup.BACK,
            MuscleGroup.BICEPS to MuscleGroup.TRICEPS,
            MuscleGroup.QUADRICEPS to MuscleGroup.HAMSTRINGS,
            MuscleGroup.SHOULDERS to MuscleGroup.BACK,
            MuscleGroup.LATS to MuscleGroup.CHEST
        )

        for ((muscle1, muscle2) in musclePairs) {
            val stats1 = stats.find { it.muscleGroup == muscle1 }
            val stats2 = stats.find { it.muscleGroup == muscle2 }

            if (stats1 != null && stats2 != null) {
                val volume1 = stats1.totalVolume
                val volume2 = stats2.totalVolume

                if (volume1 > 0 && volume2 > 0) {
                    val ratio = if (volume1 > volume2) volume1 / volume2 else volume2 / volume1

                    if (ratio >= MINOR_IMBALANCE_THRESHOLD) {
                        val stronger = if (volume1 > volume2) muscle1 else muscle2
                        val weaker = if (volume1 > volume2) muscle2 else muscle1

                        val severity = when {
                            ratio >= SEVERE_IMBALANCE_THRESHOLD -> ImbalanceSeverity.SEVERE
                            ratio >= MODERATE_IMBALANCE_THRESHOLD -> ImbalanceSeverity.MODERATE
                            else -> ImbalanceSeverity.MINOR
                        }

                        imbalances.add(
                            MuscleGroupImbalance(
                                strongerMuscle = stronger,
                                weakerMuscle = weaker,
                                volumeRatio = ratio,
                                severity = severity
                            )
                        )
                    }
                }
            }
        }

        imbalances
    }

    /**
     * Generate training recommendations based on muscle stats and imbalances
     */
    suspend fun generateTrainingRecommendations(
        startDate: Long,
        endDate: Long
    ): List<TrainingRecommendation> = withContext(Dispatchers.IO) {
        val stats = getMuscleGroupStats(startDate, endDate)
        val imbalances = detectMuscleImbalances(startDate, endDate)
        val recommendations = mutableListOf<TrainingRecommendation>()

        // Average weekly frequency across all muscles
        val avgWeeklyFrequency = if (stats.isNotEmpty()) {
            stats.map { it.weeklyFrequency }.average()
        } else {
            0.0
        }

        // Check each muscle group for recommendations
        for (stat in stats) {
            // Check for imbalances involving this muscle
            val involvedInImbalance = imbalances.find {
                it.weakerMuscle == stat.muscleGroup
            }

            if (involvedInImbalance != null) {
                recommendations.add(
                    TrainingRecommendation(
                        muscleGroup = stat.muscleGroup,
                        type = RecommendationType.ADDRESS_IMBALANCE,
                        reason = involvedInImbalance.description,
                        priority = when (involvedInImbalance.severity) {
                            ImbalanceSeverity.SEVERE -> RecommendationPriority.HIGH
                            ImbalanceSeverity.MODERATE -> RecommendationPriority.MEDIUM
                            ImbalanceSeverity.MINOR -> RecommendationPriority.LOW
                        }
                    )
                )
            }

            // Check frequency recommendations
            when {
                stat.weeklyFrequency < OPTIMAL_FREQUENCY_MIN -> {
                    recommendations.add(
                        TrainingRecommendation(
                            muscleGroup = stat.muscleGroup,
                            type = RecommendationType.INCREASE_FREQUENCY,
                            reason = "${stat.muscleGroup.displayName} is trained ${stat.weeklyFrequency}x/week. Consider increasing to ${OPTIMAL_FREQUENCY_MIN}-${OPTIMAL_FREQUENCY_MAX}x/week.",
                            priority = if (stat.weeklyFrequency == 0) RecommendationPriority.HIGH else RecommendationPriority.MEDIUM
                        )
                    )
                }
                stat.weeklyFrequency > OPTIMAL_FREQUENCY_MAX -> {
                    val daysSinceLastWorked = stat.daysSinceLastWorked ?: Int.MAX_VALUE
                    if (daysSinceLastWorked < MIN_RECOVERY_DAYS) {
                        recommendations.add(
                            TrainingRecommendation(
                                muscleGroup = stat.muscleGroup,
                                type = RecommendationType.ALLOW_RECOVERY,
                                reason = "${stat.muscleGroup.displayName} trained ${stat.weeklyFrequency}x/week. Last trained ${daysSinceLastWorked} days ago. Consider adding recovery time.",
                                priority = RecommendationPriority.MEDIUM
                            )
                        )
                    }
                }
                else -> {
                    // Optimal frequency - check volume
                    if (stat.totalVolume < avgWeeklyFrequency * 100) {
                        recommendations.add(
                            TrainingRecommendation(
                                muscleGroup = stat.muscleGroup,
                                type = RecommendationType.INCREASE_VOLUME,
                                reason = "${stat.muscleGroup.displayName} volume is below average. Consider adding sets or weight.",
                                priority = RecommendationPriority.LOW
                            )
                        )
                    }
                }
            }
        }

        recommendations.sortedByDescending {
            when (it.priority) {
                RecommendationPriority.HIGH -> 3
                RecommendationPriority.MEDIUM -> 2
                RecommendationPriority.LOW -> 1
            }
        }
    }

    /**
     * Get comprehensive muscle group analytics including stats, imbalances, and recommendations
     */
    suspend fun getMuscleGroupAnalytics(
        startDate: Long,
        endDate: Long
    ): MuscleGroupAnalytics = withContext(Dispatchers.IO) {
        MuscleGroupAnalytics(
            muscleStats = getMuscleGroupStats(startDate, endDate),
            imbalances = detectMuscleImbalances(startDate, endDate),
            recommendations = generateTrainingRecommendations(startDate, endDate)
        )
    }

    /**
     * Get stats for the last N weeks
     */
    suspend fun getStatsForLastWeeks(weeks: Int): List<MuscleGroupStats> {
        val endDate = System.currentTimeMillis()
        val startDate = endDate - TimeUnit.DAYS.toMillis((weeks * DAYS_IN_WEEK).toLong())
        return getMuscleGroupStats(startDate, endDate)
    }

    /**
     * Get analytics for the last N weeks
     */
    suspend fun getAnalyticsForLastWeeks(weeks: Int): MuscleGroupAnalytics {
        val endDate = System.currentTimeMillis()
        val startDate = endDate - TimeUnit.DAYS.toMillis((weeks * DAYS_IN_WEEK).toLong())
        return getMuscleGroupAnalytics(startDate, endDate)
    }
}
