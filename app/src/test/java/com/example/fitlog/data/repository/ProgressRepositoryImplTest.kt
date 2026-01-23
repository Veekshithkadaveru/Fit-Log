package com.example.fitlog.data.repository

import com.example.fitlog.data.database.dao.*
import com.example.fitlog.data.database.entity.ExerciseEntity
import com.example.fitlog.domain.model.DateRangeFilter
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.TimeUnit

class ProgressRepositoryImplTest {

    private lateinit var workoutDao: WorkoutDao
    private lateinit var exerciseDao: ExerciseDao
    private lateinit var personalRecordDao: PersonalRecordDao
    private lateinit var progressRepository: ProgressRepositoryImpl

    private val now = System.currentTimeMillis()
    private val oneWeekAgo = now - TimeUnit.DAYS.toMillis(7)
    private val oneMonthAgo = now - TimeUnit.DAYS.toMillis(30)

    @Before
    fun setup() {
        workoutDao = mockk()
        exerciseDao = mockk()
        personalRecordDao = mockk()
        progressRepository = ProgressRepositoryImpl(workoutDao, exerciseDao, personalRecordDao)
    }

    // ==================== Weight Progress Tests ====================

    @Test
    fun `getWeightProgressForExercise returns mapped weight points`() = runTest {
        // Given
        val exerciseId = 1
        val weightPoints = listOf(
            WeightDatePoint(date = oneWeekAgo, maxWeight = 100f),
            WeightDatePoint(date = now, maxWeight = 110f)
        )
        coEvery {
            workoutDao.getMaxWeightProgressForExercise(exerciseId, oneMonthAgo, now)
        } returns weightPoints

        // When
        val result = progressRepository.getWeightProgressForExercise(exerciseId, oneMonthAgo, now)

        // Then
        assertEquals(2, result.size)
        assertEquals(100f, result[0].maxWeight, 0.01f)
        assertEquals(110f, result[1].maxWeight, 0.01f)
        assertEquals(exerciseId, result[0].exerciseId)
    }

    @Test
    fun `getWeightProgressForExercise returns empty list when no data`() = runTest {
        // Given
        val exerciseId = 1
        coEvery {
            workoutDao.getMaxWeightProgressForExercise(exerciseId, oneMonthAgo, now)
        } returns emptyList()

        // When
        val result = progressRepository.getWeightProgressForExercise(exerciseId, oneMonthAgo, now)

        // Then
        assertTrue(result.isEmpty())
    }

    // ==================== Volume Progress Tests ====================

    @Test
    fun `getVolumeProgressForExercise returns mapped volume points`() = runTest {
        // Given
        val exerciseId = 1
        val volumePoints = listOf(
            VolumeDatePoint(date = oneWeekAgo, volume = 5000f),
            VolumeDatePoint(date = now, volume = 6000f)
        )
        coEvery {
            workoutDao.getVolumeProgressForExercise(exerciseId, oneMonthAgo, now)
        } returns volumePoints

        // When
        val result = progressRepository.getVolumeProgressForExercise(exerciseId, oneMonthAgo, now)

        // Then
        assertEquals(2, result.size)
        assertEquals(5000f, result[0].totalVolume, 0.01f)
        assertEquals(6000f, result[1].totalVolume, 0.01f)
    }

    @Test
    fun `getDailyVolume returns overall volume progression`() = runTest {
        // Given
        val volumePoints = listOf(
            VolumeDatePoint(date = oneWeekAgo, volume = 10000f),
            VolumeDatePoint(date = now, volume = 12000f)
        )
        coEvery {
            workoutDao.getDailyVolumeInRange(oneMonthAgo, now)
        } returns volumePoints

        // When
        val result = progressRepository.getDailyVolume(oneMonthAgo, now)

        // Then
        assertEquals(2, result.size)
        assertEquals(10000f, result[0].totalVolume, 0.01f)
        assertEquals(12000f, result[1].totalVolume, 0.01f)
    }

    // ==================== Exercise Progress Data Tests ====================

    @Test
    fun `getExerciseProgressData returns complete data for valid exercise`() = runTest {
        // Given
        val exerciseId = 1
        val exercise = ExerciseEntity(
            id = exerciseId,
            name = "Bench Press",
            primaryMuscle = "CHEST",
            secondaryMuscles = "TRICEPS,SHOULDERS",
            category = "COMPOUND",
            equipment = "BARBELL"
        )
        val weightPoints = listOf(WeightDatePoint(date = now, maxWeight = 100f))
        val volumePoints = listOf(VolumeDatePoint(date = now, volume = 5000f))

        coEvery { exerciseDao.getById(exerciseId) } returns exercise
        coEvery { workoutDao.getMaxWeightProgressForExercise(exerciseId, oneMonthAgo, now) } returns weightPoints
        coEvery { workoutDao.getVolumeProgressForExercise(exerciseId, oneMonthAgo, now) } returns volumePoints
        coEvery { workoutDao.getMaxWeightForExercise(exerciseId) } returns 100f

        // When
        val result = progressRepository.getExerciseProgressData(exerciseId, oneMonthAgo, now)

        // Then
        assertNotNull(result)
        assertEquals("Bench Press", result?.exerciseName)
        assertEquals(exerciseId, result?.exerciseId)
        assertEquals(1, result?.weightProgress?.size)
        assertEquals(1, result?.volumeProgress?.size)
        assertEquals(100f, result?.bestWeight)
        assertEquals(5000f, result?.bestVolume)
        assertEquals(1, result?.totalWorkouts)
    }

    @Test
    fun `getExerciseProgressData returns null for invalid exercise`() = runTest {
        // Given
        val exerciseId = 999
        coEvery { exerciseDao.getById(exerciseId) } returns null

        // When
        val result = progressRepository.getExerciseProgressData(exerciseId, oneMonthAgo, now)

        // Then
        assertNull(result)
    }

    // ==================== Workout Count Tests ====================

    @Test
    fun `getWorkoutCountByMonth returns monthly counts`() = runTest {
        // Given
        val monthlyData = listOf(
            WorkoutCountByPeriod(monthStart = "2024-01-01", count = 10),
            WorkoutCountByPeriod(monthStart = "2024-02-01", count = 12)
        )
        coEvery { workoutDao.getWorkoutCountByMonth(oneMonthAgo, now) } returns monthlyData

        // When
        val result = progressRepository.getWorkoutCountByMonth(oneMonthAgo, now)

        // Then
        assertEquals(2, result.size)
        assertEquals(10, result[0].count)
        assertEquals(12, result[1].count)
    }

    @Test
    fun `getWorkoutCountByWeek returns weekly counts`() = runTest {
        // Given
        val weeklyData = listOf(
            WorkoutCountByWeek(weekStart = "2024-01", count = 3),
            WorkoutCountByWeek(weekStart = "2024-02", count = 4)
        )
        coEvery { workoutDao.getWorkoutCountByWeek(oneMonthAgo, now) } returns weeklyData

        // When
        val result = progressRepository.getWorkoutCountByWeek(oneMonthAgo, now)

        // Then
        assertEquals(2, result.size)
        assertEquals(3, result[0].count)
        assertEquals(4, result[1].count)
    }

    // ==================== Duration Tests ====================

    @Test
    fun `getWorkoutDurations converts milliseconds to minutes`() = runTest {
        // Given - Duration stored in ms (60 minutes = 3,600,000 ms)
        val durationData = listOf(
            DurationDatePoint(date = oneWeekAgo, durationMs = 3600000L),
            DurationDatePoint(date = now, durationMs = 4500000L)
        )
        coEvery { workoutDao.getWorkoutDurationsInRange(oneMonthAgo, now) } returns durationData

        // When
        val result = progressRepository.getWorkoutDurations(oneMonthAgo, now)

        // Then
        assertEquals(2, result.size)
        assertEquals(60L, result[0].durationMinutes) // 60 minutes
        assertEquals(75L, result[1].durationMinutes) // 75 minutes
    }

    @Test
    fun `getAverageWorkoutDuration converts milliseconds to minutes`() = runTest {
        // Given - 45 minutes average = 2,700,000 ms
        coEvery { workoutDao.getAverageWorkoutDurationInRange(oneMonthAgo, now) } returns 2700000L

        // When
        val result = progressRepository.getAverageWorkoutDuration(oneMonthAgo, now)

        // Then
        assertEquals(45L, result)
    }

    @Test
    fun `getAverageWorkoutDuration returns null when no workouts`() = runTest {
        // Given
        coEvery { workoutDao.getAverageWorkoutDurationInRange(oneMonthAgo, now) } returns null

        // When
        val result = progressRepository.getAverageWorkoutDuration(oneMonthAgo, now)

        // Then
        assertNull(result)
    }

    // ==================== Progress Summary Tests ====================

    @Test
    fun `getProgressSummary returns complete summary`() = runTest {
        // Given
        coEvery { workoutDao.getWorkoutCountInRange(any(), any()) } returns 4
        coEvery { workoutDao.getCompletedWorkoutCount() } returns 50
        coEvery { personalRecordDao.getRecordCountInRange(any(), any()) } returns 3
        coEvery { workoutDao.getTotalVolumeInRange(any(), any()) } returns 25000f
        coEvery { workoutDao.getAverageWorkoutDuration() } returns 3600000L // 60 min in ms
        coEvery { workoutDao.getWorkoutDates() } returns listOf("2024-01-23", "2024-01-22", "2024-01-21")

        // When
        val result = progressRepository.getProgressSummary()

        // Then
        assertEquals(4, result.workoutsThisWeek)
        assertEquals(4, result.workoutsThisMonth)
        assertEquals(50, result.totalWorkouts)
        assertEquals(3, result.prsThisMonth)
        assertEquals(25000f, result.totalVolumeThisWeek, 0.01f)
        assertEquals(60L, result.averageWorkoutDurationMinutes)
    }

    // ==================== Streak Tests ====================

    @Test
    fun `getCurrentStreak returns correct streak for consecutive days`() = runTest {
        // Given - Today and yesterday worked out
        val calendar = Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val today = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val dayBefore = dateFormat.format(calendar.time)

        coEvery { workoutDao.getWorkoutDates() } returns listOf(today, yesterday, dayBefore)

        // When
        val result = progressRepository.getCurrentStreak()

        // Then
        assertEquals(3, result)
    }

    @Test
    fun `getCurrentStreak returns zero when no recent workouts`() = runTest {
        // Given - Last workout was 3 days ago
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -3)
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val threeDaysAgo = dateFormat.format(calendar.time)

        coEvery { workoutDao.getWorkoutDates() } returns listOf(threeDaysAgo)

        // When
        val result = progressRepository.getCurrentStreak()

        // Then
        assertEquals(0, result)
    }

    @Test
    fun `getCurrentStreak returns zero when no workouts at all`() = runTest {
        // Given
        coEvery { workoutDao.getWorkoutDates() } returns emptyList()

        // When
        val result = progressRepository.getCurrentStreak()

        // Then
        assertEquals(0, result)
    }

    // ==================== Date Range Filter Tests ====================

    @Test
    fun `getDateRangeForFilter returns correct range for 30 days`() {
        // When
        val (start, end) = progressRepository.getDateRangeForFilter(DateRangeFilter.LAST_30_DAYS)

        // Then
        val daysDiff = TimeUnit.MILLISECONDS.toDays(end - start)
        assertEquals(30, daysDiff)
    }

    @Test
    fun `getDateRangeForFilter returns correct range for 90 days`() {
        // When
        val (start, end) = progressRepository.getDateRangeForFilter(DateRangeFilter.LAST_90_DAYS)

        // Then
        val daysDiff = TimeUnit.MILLISECONDS.toDays(end - start)
        assertEquals(90, daysDiff)
    }

    @Test
    fun `getDateRangeForFilter returns correct range for 1 year`() {
        // When
        val (start, end) = progressRepository.getDateRangeForFilter(DateRangeFilter.LAST_YEAR)

        // Then
        val daysDiff = TimeUnit.MILLISECONDS.toDays(end - start)
        assertTrue(daysDiff in 364..366) // Account for leap years
    }

    @Test
    fun `getDateRangeForFilter returns zero start for all time`() {
        // When
        val (start, _) = progressRepository.getDateRangeForFilter(DateRangeFilter.ALL_TIME)

        // Then
        assertEquals(0L, start)
    }

    // ==================== First Workout Date Tests ====================

    @Test
    fun `getFirstWorkoutDate returns first workout timestamp`() = runTest {
        // Given
        val firstWorkoutDate = oneMonthAgo
        coEvery { workoutDao.getFirstWorkoutDate() } returns firstWorkoutDate

        // When
        val result = progressRepository.getFirstWorkoutDate()

        // Then
        assertEquals(firstWorkoutDate, result)
    }

    @Test
    fun `getFirstWorkoutDate returns null when no workouts`() = runTest {
        // Given
        coEvery { workoutDao.getFirstWorkoutDate() } returns null

        // When
        val result = progressRepository.getFirstWorkoutDate()

        // Then
        assertNull(result)
    }
}
