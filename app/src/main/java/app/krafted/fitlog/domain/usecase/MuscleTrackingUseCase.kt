package app.krafted.fitlog.domain.usecase

import app.krafted.fitlog.domain.model.MuscleGroup
import app.krafted.fitlog.domain.model.MuscleGroupAnalytics
import app.krafted.fitlog.domain.model.MuscleGroupImbalance
import app.krafted.fitlog.domain.model.MuscleGroupStats
import app.krafted.fitlog.domain.model.TrainingRecommendation
import app.krafted.fitlog.domain.model.WeeklyMuscleFrequency
import app.krafted.fitlog.domain.model.RecommendationType
import app.krafted.fitlog.domain.model.RecommendationPriority
import app.krafted.fitlog.domain.model.ImbalanceSeverity
import app.krafted.fitlog.domain.repository.WorkoutRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MuscleTrackingUseCase @Inject constructor(
    private val workoutRepository: WorkoutRepository
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
     * Get analytics for all muscle groups over the specified number of weeks
     */
    suspend fun getAnalyticsForLastWeeks(weeks: Int): MuscleGroupAnalytics {
        val endDate = System.currentTimeMillis()
        val startDate = endDate - TimeUnit.DAYS.toMillis((weeks * DAYS_IN_WEEK).toLong())
        return getMuscleGroupAnalytics(startDate, endDate)
    }

    /**
     * Get comprehensive muscle group analytics including stats, imbalances, and recommendations
     */
    suspend fun getMuscleGroupAnalytics(
        startDate: Long,
        endDate: Long
    ): MuscleGroupAnalytics = withContext(Dispatchers.IO) {
        val stats = getMuscleGroupStats(startDate, endDate)
        MuscleGroupAnalytics(
            muscleStats = stats,
            imbalances = detectImbalances(stats),
            recommendations = generateRecommendations(stats, detectImbalances(stats))
        )
    }

    // ... (intermediate code unchanged, assumed handled by other replacements or existing)

    /**
     * Generate training recommendations based on stats and imbalances
     */
    private fun generateRecommendations(
        stats: List<MuscleGroupStats>,
        imbalances: List<MuscleGroupImbalance>
    ): List<TrainingRecommendation> {
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
            // ... (rest of logic)
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
                    val daysSinceLastWorked = (System.currentTimeMillis() - (stat.lastWorkedDate ?: 0L)).let {
                        TimeUnit.MILLISECONDS.toDays(it).toInt()
                    }
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
                    if (stat.totalVolume < avgWeeklyFrequency * 100) { // Arbitrary threshold for now
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

        return recommendations.sortedByDescending {
            when (it.priority) {
                RecommendationPriority.HIGH -> 3
                RecommendationPriority.MEDIUM -> 2
                RecommendationPriority.LOW -> 1
            }
        }
    }

    /**
     * Get weekly frequency distribution for heatmap
     */
    suspend fun getWeeklyMuscleFrequencies(startDate: Long, endDate: Long): List<WeeklyMuscleFrequency> {
        val stats = getMuscleGroupStats(startDate, endDate)
        // Need to calculate weekly values. getMuscleGroupStats returns stats with weeklyFrequency (Int).
        // But WeeklyMuscleFrequency needs setsPerWeek and totalVolumePerWeek.
        // And MuscleGroupStats has totalVolume and totalSets (totals, not weekly).
        
        val daysInRange = TimeUnit.MILLISECONDS.toDays(endDate - startDate).toInt()
        val weeksInRange = if (daysInRange >= 7) daysInRange / 7.0f else 1.0f
        
        return stats.map { stat ->
            WeeklyMuscleFrequency(
                muscleGroup = stat.muscleGroup,
                workoutsPerWeek = stat.weeklyFrequency,
                setsPerWeek = (stat.totalSets / weeksInRange).toInt(),
                totalVolumePerWeek = stat.totalVolume / weeksInRange
            )
        }
    }

    /**
     * Calculate muscle group statistics for a given time range
     */
    suspend fun getMuscleGroupStats(
        startDate: Long,
        endDate: Long
    ): List<MuscleGroupStats> = withContext(Dispatchers.IO) {
        val activeMuscles = workoutRepository.getActiveMuscleGroupsInRange(startDate, endDate)
        val daysInRange = TimeUnit.MILLISECONDS.toDays(endDate - startDate).toInt()
        val weeksInRange = if (daysInRange >= DAYS_IN_WEEK) daysInRange / DAYS_IN_WEEK else 1

        activeMuscles.mapNotNull { muscleGroup ->
            val totalVolume = workoutRepository.getTotalVolumeForMuscleInRange(
                muscleGroup, startDate, endDate
            ) ?: 0f

            val totalSets = workoutRepository.getTotalSetsForMuscleInRange(
                muscleGroup, startDate, endDate
            )

            val workoutCount = workoutRepository.getWorkoutCountForMuscleInRange(
                muscleGroup, startDate, endDate
            )

            val lastWorkedDate = workoutRepository.getLastWorkoutDateForMuscle(muscleGroup)

            val exerciseCount = workoutRepository.getExerciseCountForMuscleInRange(
                muscleGroup, startDate, endDate
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
                lastWorkedDate = lastWorkedDate ?: 0L,
                averageVolumePerWorkout = averageVolumePerWorkout,
                exerciseCount = exerciseCount
            )
        }
    }

    /**
     * Calculate stats for a specific muscle group
     */
    private suspend fun calculateMuscleStats(
        muscle: MuscleGroup,
        startDate: Long,
        endDate: Long
    ): MuscleGroupStats {
        val totalVolume = workoutRepository.getTotalVolumeForMuscleInRange(muscle, startDate, endDate) ?: 0f
        val totalSets = workoutRepository.getTotalSetsForMuscleInRange(muscle, startDate, endDate)
        val workoutCount = workoutRepository.getWorkoutCountForMuscleInRange(muscle, startDate, endDate)
        val lastWorkoutDate = workoutRepository.getLastWorkoutDateForMuscle(muscle)
        val exerciseCount = workoutRepository.getExerciseCountForMuscleInRange(muscle, startDate, endDate)

        // Calculate weeks in range to get weekly averages
        val daysInRange = (endDate - startDate) / (24 * 60 * 60 * 1000L)
        val weeksInRange = daysInRange / 7.0f

        // Calculate weekly frequency
        val weeklyFrequency = if (weeksInRange > 0) {
            (workoutCount / weeksInRange).toInt()
        } else {
            workoutCount
        }

        val averageVolumePerWorkout = if (workoutCount > 0) {
            totalVolume / workoutCount
        } else {
            0f
        }

        return MuscleGroupStats(
            muscleGroup = muscle,
            totalVolume = totalVolume,
            totalSets = totalSets,
            weeklyFrequency = weeklyFrequency,
            lastWorkedDate = lastWorkoutDate,
            averageVolumePerWorkout = averageVolumePerWorkout,
            exerciseCount = exerciseCount
        )
    }

    /**
     * Detect muscle imbalances based on volume and frequency ratios
     */
    private fun detectImbalances(stats: List<MuscleGroupStats>): List<MuscleGroupImbalance> {
        val imbalances = mutableListOf<MuscleGroupImbalance>()
        val statsMap = stats.associateBy { it.muscleGroup }

        // Rule 1: Push/Pull Ratio (Chest vs Back)
        checkRatio(
            statsMap,
            MuscleGroup.CHEST,
            MuscleGroup.BACK,
            targetRatio = 1.0f, // 1:1 is ideal
            tolerance = 0.2f, // 20% tolerance
            imbalances
        )

        // Rule 2: Quad/Hamstring Ratio
        checkRatio(
            statsMap,
            MuscleGroup.QUADRICEPS,
            MuscleGroup.HAMSTRINGS,
            targetRatio = 1.5f, // Quads are typically stronger/higher volume
            tolerance = 0.3f,
            imbalances
        )

        // Rule 3: Bicep/Tricep Ratio
        checkRatio(
            statsMap,
            MuscleGroup.TRICEPS,
            MuscleGroup.BICEPS,
            targetRatio = 1.0f,
            tolerance = 0.2f,
            imbalances
        )

        return imbalances
    }

    private fun checkRatio(
        statsMap: Map<MuscleGroup, MuscleGroupStats>,
        muscle1: MuscleGroup,
        muscle2: MuscleGroup,
        targetRatio: Float,
        tolerance: Float,
        resultList: MutableList<MuscleGroupImbalance>
    ) {
        val stats1 = statsMap[muscle1]
        val stats2 = statsMap[muscle2]

        if (stats1 != null && stats2 != null && stats1.totalSets > 3 && stats2.totalSets > 3) { // Changed to totalSets as weeklySets is not in MuscleGroupStats
            val ratio = stats1.totalSets.toFloat() / stats2.totalSets.toFloat()
            
                if (ratio > targetRatio + tolerance) {
                // Muscle 1 is dominant
                resultList.add(
                    MuscleGroupImbalance(
                        strongerMuscle = muscle1,
                        weakerMuscle = muscle2,
                        volumeRatio = ratio,
                        severity = calculateSeverity(ratio, targetRatio, tolerance)
                    )
                )
            } else if (ratio < targetRatio - tolerance) {
                // Muscle 2 is dominant (or Muscle 1 is lagging)
                resultList.add(
                    MuscleGroupImbalance(
                        strongerMuscle = muscle2,
                        weakerMuscle = muscle1,
                        volumeRatio = 1/ratio,
                        severity = calculateSeverity(1/ratio, 1/targetRatio, tolerance)
                    )
                )
            }
        }
    }

    private fun calculateSeverity(ratio: Float, target: Float, tolerance: Float): ImbalanceSeverity {
        val deviation = kotlin.math.abs(ratio - target)
        return when {
            deviation > tolerance * 2 -> ImbalanceSeverity.SEVERE
            deviation > tolerance * 1.5 -> ImbalanceSeverity.MODERATE
            else -> ImbalanceSeverity.MINOR
        }
    }

    /**
     * Generate training recommendations based on stats and imbalances
     */



}

// Helper for parallel execution
suspend fun <T, R> Iterable<T>.mapAsync(transform: suspend (T) -> R): List<R> {
    // In a real app, use coroutineScope to run these in parallel
    // For simplicity here, we'll map sequentially but calling suspend functions
    val destination = ArrayList<R>()
    for (item in this) {
        destination.add(transform(item))
    }
    return destination
}
