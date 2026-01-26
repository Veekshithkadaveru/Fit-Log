package com.example.fitlog.data.repository

import com.example.fitlog.data.database.dao.ExerciseDao
import com.example.fitlog.data.database.dao.PersonalRecordDao
import com.example.fitlog.data.database.dao.WorkoutDao
import com.example.fitlog.domain.model.*
import com.example.fitlog.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val exerciseDao: ExerciseDao,
    private val personalRecordDao: PersonalRecordDao
) : ProgressRepository {

    override suspend fun getWeightProgressForExercise(
        exerciseId: Int,
        startDate: Long,
        endDate: Long
    ): List<WeightProgressPoint> {
        return workoutDao.getMaxWeightProgressForExercise(exerciseId, startDate, endDate)
            .map { point ->
                WeightProgressPoint(
                    date = point.date,
                    maxWeight = point.maxWeight,
                    exerciseId = exerciseId
                )
            }
    }

    override suspend fun getVolumeProgressForExercise(
        exerciseId: Int,
        startDate: Long,
        endDate: Long
    ): List<VolumeProgressPoint> {
        return workoutDao.getVolumeProgressForExercise(exerciseId, startDate, endDate)
            .map { point ->
                VolumeProgressPoint(
                    date = point.date,
                    totalVolume = point.volume
                )
            }
    }

    override suspend fun getExerciseProgressData(
        exerciseId: Int,
        startDate: Long,
        endDate: Long
    ): ExerciseProgressData? {
        val exercise = exerciseDao.getById(exerciseId) ?: return null

        val weightProgress = getWeightProgressForExercise(exerciseId, startDate, endDate)
        val volumeProgress = getVolumeProgressForExercise(exerciseId, startDate, endDate)
        val bestWeight = workoutDao.getMaxWeightForExercise(exerciseId)
        val bestVolume = volumeProgress.maxOfOrNull { it.totalVolume }

        return ExerciseProgressData(
            exerciseId = exerciseId,
            exerciseName = exercise.name,
            weightProgress = weightProgress,
            volumeProgress = volumeProgress,
            bestWeight = bestWeight,
            bestVolume = bestVolume,
            totalWorkouts = weightProgress.size
        )
    }

    override suspend fun getDailyVolume(
        startDate: Long,
        endDate: Long
    ): List<VolumeProgressPoint> {
        return workoutDao.getDailyVolumeInRange(startDate, endDate)
            .map { point ->
                VolumeProgressPoint(
                    date = point.date,
                    totalVolume = point.volume
                )
            }
    }

    override suspend fun getWorkoutCountByMonth(
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint> {
        val workouts = workoutDao.getWorkoutsInDateRange(startDate, endDate).first()

        val calendar = Calendar.getInstance()
        
        return workouts
            .groupBy { workout ->
                calendar.timeInMillis = workout.startTime
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            .map { (monthStart, workoutsInMonth) ->
                WorkoutCountPoint(
                    periodStart = monthStart,
                    count = workoutsInMonth.size
                )
            }
            .sortedBy { it.periodStart }
    }

    override suspend fun getWorkoutCountByWeek(
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint> {
        val workouts = workoutDao.getWorkoutsInDateRange(startDate, endDate).first()

        val calendar = Calendar.getInstance()
        
        return workouts
            .groupBy { workout ->
                calendar.timeInMillis = workout.startTime
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            .map { (weekStart, workoutsInWeek) ->
                WorkoutCountPoint(
                    periodStart = weekStart,
                    count = workoutsInWeek.size
                )
            }
            .sortedBy { it.periodStart }
    }

    override suspend fun getWorkoutDurations(
        startDate: Long,
        endDate: Long
    ): List<DurationProgressPoint> {
        return workoutDao.getWorkoutDurationsInRange(startDate, endDate)
            .map { point ->
                DurationProgressPoint(
                    date = point.date,
                    durationMinutes = point.durationMs / (1000 * 60) // Convert ms to minutes
                )
            }
    }

    override suspend fun getProgressSummary(): ProgressSummary {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()

        // Start of this week (Monday)
        calendar.timeInMillis = now
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val weekStart = calendar.timeInMillis

        // Start of this month
        calendar.timeInMillis = now
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val monthStart = calendar.timeInMillis

        val workoutsThisWeek = workoutDao.getWorkoutCountInRange(weekStart, now)
        val workoutsThisMonth = workoutDao.getWorkoutCountInRange(monthStart, now)
        val totalWorkouts = workoutDao.getCompletedWorkoutCount()
        val prsThisMonth = personalRecordDao.getRecordCountInRange(monthStart, now)
        val totalVolumeThisWeek = workoutDao.getTotalVolumeInRange(weekStart, now) ?: 0f
        val totalVolumeThisMonth = workoutDao.getTotalVolumeInRange(monthStart, now) ?: 0f
        val avgDuration = workoutDao.getAverageWorkoutDuration()?.let { it / (1000 * 60) } ?: 0L
        val currentStreak = getCurrentStreak()

        return ProgressSummary(
            workoutsThisWeek = workoutsThisWeek,
            workoutsThisMonth = workoutsThisMonth,
            totalWorkouts = totalWorkouts,
            prsThisMonth = prsThisMonth,
            totalVolumeThisWeek = totalVolumeThisWeek,
            totalVolumeThisMonth = totalVolumeThisMonth,
            averageWorkoutDurationMinutes = avgDuration,
            currentStreak = currentStreak
        )
    }

    override suspend fun getAverageWorkoutDuration(
        startDate: Long,
        endDate: Long
    ): Long? {
        return workoutDao.getAverageWorkoutDurationInRange(startDate, endDate)?.let {
            it / (1000 * 60) // Convert ms to minutes
        }
    }

    override suspend fun getCurrentStreak(): Int {
        val workoutDates = workoutDao.getWorkoutDates()
        if (workoutDates.isEmpty()) return 0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = dateFormat.format(Date())
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }.let { dateFormat.format(it.time) }

        // Check if streak is current (workout today or yesterday)
        val firstDate = workoutDates.firstOrNull() ?: return 0
        if (firstDate != today && firstDate != yesterday) return 0

        var streak = 0
        var previousDate: Calendar? = null

        for (dateStr in workoutDates) {
            try {
                val date = dateFormat.parse(dateStr) ?: continue
                val currentCal = Calendar.getInstance().apply { time = date }

                if (previousDate == null) {
                    streak = 1
                    previousDate = currentCal
                } else {
                    // Check if this date is exactly one day before the previous
                    previousDate.add(Calendar.DAY_OF_YEAR, -1)
                    if (currentCal.get(Calendar.YEAR) == previousDate.get(Calendar.YEAR) &&
                        currentCal.get(Calendar.DAY_OF_YEAR) == previousDate.get(Calendar.DAY_OF_YEAR)) {
                        streak++
                        previousDate = currentCal
                    } else {
                        break
                    }
                }
            } catch (e: Exception) {
                break
            }
        }

        return streak
    }

    override suspend fun getFirstWorkoutDate(): Long? {
        return workoutDao.getFirstWorkoutDate()
    }

    override fun getDateRangeForFilter(filter: DateRangeFilter): Pair<Long, Long> {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endDate = calendar.timeInMillis

        return when (filter) {
            DateRangeFilter.LAST_7_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.timeInMillis, endDate)
            }
            DateRangeFilter.LAST_30_DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, -30)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.timeInMillis, endDate)
            }
            DateRangeFilter.LAST_YEAR -> {
                calendar.add(Calendar.YEAR, -1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                Pair(calendar.timeInMillis, endDate)
            }
        }
    }

    override suspend fun getWorkoutCountByDay(
        startDate: Long,
        endDate: Long
    ): List<WorkoutCountPoint> {
        val workouts = workoutDao.getWorkoutsInDateRange(startDate, endDate).first()

        val calendar = Calendar.getInstance()
        
        return workouts
            .groupBy { workout ->
                calendar.timeInMillis = workout.startTime
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            .map { (dayStart, workoutsInDay) ->
                WorkoutCountPoint(
                    periodStart = dayStart,
                    count = workoutsInDay.size
                )
            }
            .sortedBy { it.periodStart }
    }
}
