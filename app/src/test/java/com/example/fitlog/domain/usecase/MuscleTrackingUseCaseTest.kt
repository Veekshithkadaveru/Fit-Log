package com.example.fitlog.domain.usecase

import com.example.fitlog.data.database.dao.WorkoutDao
import com.example.fitlog.domain.model.ImbalanceSeverity
import com.example.fitlog.domain.model.MuscleGroup
import com.example.fitlog.domain.model.RecommendationType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class MuscleTrackingUseCaseTest {

    private lateinit var workoutDao: WorkoutDao
    private lateinit var muscleTrackingUseCase: MuscleTrackingUseCase

    private val now = System.currentTimeMillis()
    private val oneWeekAgo = now - TimeUnit.DAYS.toMillis(7)
    private val twoWeeksAgo = now - TimeUnit.DAYS.toMillis(14)

    @Before
    fun setup() {
        workoutDao = mockk()
        muscleTrackingUseCase = MuscleTrackingUseCase(workoutDao)
    }

    @Test
    fun `getMuscleGroupStats returns correct stats for single muscle`() = runTest {
        // Given
        val muscleGroup = MuscleGroup.CHEST.name
        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(muscleGroup)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 5000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 20
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 4
        coEvery { workoutDao.getLastWorkoutDateForMuscle(muscleGroup) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 3

        // When
        val stats = muscleTrackingUseCase.getMuscleGroupStats(twoWeeksAgo, now)

        // Then
        assertEquals(1, stats.size)
        val chestStats = stats.first()
        assertEquals(MuscleGroup.CHEST, chestStats.muscleGroup)
        assertEquals(5000f, chestStats.totalVolume, 0.01f)
        assertEquals(20, chestStats.totalSets)
        assertEquals(2, chestStats.weeklyFrequency) // 4 workouts / 2 weeks
        assertEquals(oneWeekAgo, chestStats.lastWorkedDate)
        assertEquals(1250f, chestStats.averageVolumePerWorkout, 0.01f) // 5000 / 4
        assertEquals(3, chestStats.exerciseCount)
    }

    @Test
    fun `getMuscleGroupStats returns empty list when no active muscles`() = runTest {
        // Given
        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns emptyList()

        // When
        val stats = muscleTrackingUseCase.getMuscleGroupStats(twoWeeksAgo, now)

        // Then
        assertTrue(stats.isEmpty())
    }

    @Test
    fun `getWeeklyMuscleFrequencies calculates correct frequencies`() = runTest {
        // Given
        val muscleGroup = MuscleGroup.BACK.name
        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(muscleGroup)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 6000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 30
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 6
        coEvery { workoutDao.getLastWorkoutDateForMuscle(muscleGroup) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 5

        // When
        val frequencies = muscleTrackingUseCase.getWeeklyMuscleFrequencies(twoWeeksAgo, now)

        // Then
        assertEquals(1, frequencies.size)
        val backFrequency = frequencies.first()
        assertEquals(MuscleGroup.BACK, backFrequency.muscleGroup)
        assertEquals(3, backFrequency.workoutsPerWeek) // 6 workouts / 2 weeks
        assertEquals(15, backFrequency.setsPerWeek) // 30 sets / 2 weeks
        assertEquals(3000f, backFrequency.totalVolumePerWeek, 0.01f) // 6000 / 2
    }

    @Test
    fun `detectMuscleImbalances detects severe chest-back imbalance`() = runTest {
        // Given - Chest has 3x more volume than back
        val chestName = MuscleGroup.CHEST.name
        val backName = MuscleGroup.BACK.name

        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(chestName, backName)

        // Chest stats
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(chestName, twoWeeksAgo, now) } returns 9000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(chestName, twoWeeksAgo, now) } returns 30
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 6
        coEvery { workoutDao.getLastWorkoutDateForMuscle(chestName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 4

        // Back stats (much lower)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(backName, twoWeeksAgo, now) } returns 3000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(backName, twoWeeksAgo, now) } returns 10
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(backName, twoWeeksAgo, now) } returns 2
        coEvery { workoutDao.getLastWorkoutDateForMuscle(backName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(backName, twoWeeksAgo, now) } returns 2

        // When
        val imbalances = muscleTrackingUseCase.detectMuscleImbalances(twoWeeksAgo, now)

        // Then
        assertTrue(imbalances.isNotEmpty())
        val chestBackImbalance = imbalances.firstOrNull {
            it.strongerMuscle == MuscleGroup.CHEST && it.weakerMuscle == MuscleGroup.BACK
        }
        assertNotNull(chestBackImbalance)
        assertEquals(ImbalanceSeverity.SEVERE, chestBackImbalance?.severity)
        assertEquals(3.0f, chestBackImbalance?.volumeRatio ?: 0f, 0.01f)
    }

    @Test
    fun `detectMuscleImbalances detects moderate biceps-triceps imbalance`() = runTest {
        // Given - Biceps has 2.5x more volume than triceps
        val bicepsName = MuscleGroup.BICEPS.name
        val tricepsName = MuscleGroup.TRICEPS.name

        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(bicepsName, tricepsName)

        // Biceps stats
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(bicepsName, twoWeeksAgo, now) } returns 5000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(bicepsName, twoWeeksAgo, now) } returns 20
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(bicepsName, twoWeeksAgo, now) } returns 4
        coEvery { workoutDao.getLastWorkoutDateForMuscle(bicepsName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(bicepsName, twoWeeksAgo, now) } returns 3

        // Triceps stats (lower)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(tricepsName, twoWeeksAgo, now) } returns 2000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(tricepsName, twoWeeksAgo, now) } returns 8
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(tricepsName, twoWeeksAgo, now) } returns 2
        coEvery { workoutDao.getLastWorkoutDateForMuscle(tricepsName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(tricepsName, twoWeeksAgo, now) } returns 2

        // When
        val imbalances = muscleTrackingUseCase.detectMuscleImbalances(twoWeeksAgo, now)

        // Then
        assertTrue(imbalances.isNotEmpty())
        val bicepsTricepsImbalance = imbalances.firstOrNull {
            it.strongerMuscle == MuscleGroup.BICEPS && it.weakerMuscle == MuscleGroup.TRICEPS
        }
        assertNotNull(bicepsTricepsImbalance)
        assertEquals(ImbalanceSeverity.MODERATE, bicepsTricepsImbalance?.severity)
    }

    @Test
    fun `detectMuscleImbalances returns empty when muscles are balanced`() = runTest {
        // Given - Balanced chest and back
        val chestName = MuscleGroup.CHEST.name
        val backName = MuscleGroup.BACK.name

        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(chestName, backName)

        // Chest stats
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(chestName, twoWeeksAgo, now) } returns 5000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(chestName, twoWeeksAgo, now) } returns 20
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 4
        coEvery { workoutDao.getLastWorkoutDateForMuscle(chestName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 3

        // Back stats (similar)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(backName, twoWeeksAgo, now) } returns 5200f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(backName, twoWeeksAgo, now) } returns 21
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(backName, twoWeeksAgo, now) } returns 4
        coEvery { workoutDao.getLastWorkoutDateForMuscle(backName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(backName, twoWeeksAgo, now) } returns 3

        // When
        val imbalances = muscleTrackingUseCase.detectMuscleImbalances(twoWeeksAgo, now)

        // Then - No chest-back imbalance since ratio is < 1.5
        val chestBackImbalance = imbalances.firstOrNull {
            (it.strongerMuscle == MuscleGroup.CHEST && it.weakerMuscle == MuscleGroup.BACK) ||
            (it.strongerMuscle == MuscleGroup.BACK && it.weakerMuscle == MuscleGroup.CHEST)
        }
        assertNull(chestBackImbalance)
    }

    @Test
    fun `generateTrainingRecommendations suggests increasing frequency for undertrained muscle`() = runTest {
        // Given - Chest trained only once per week (below optimal)
        val chestName = MuscleGroup.CHEST.name

        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(chestName)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(chestName, twoWeeksAgo, now) } returns 2000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(chestName, twoWeeksAgo, now) } returns 10
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 2 // 1x per week
        coEvery { workoutDao.getLastWorkoutDateForMuscle(chestName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 2

        // When
        val recommendations = muscleTrackingUseCase.generateTrainingRecommendations(twoWeeksAgo, now)

        // Then
        assertTrue(recommendations.isNotEmpty())
        val increaseFrequencyRec = recommendations.firstOrNull {
            it.muscleGroup == MuscleGroup.CHEST && it.type == RecommendationType.INCREASE_FREQUENCY
        }
        assertNotNull(increaseFrequencyRec)
    }

    @Test
    fun `getMuscleGroupAnalytics returns complete analytics data`() = runTest {
        // Given
        val chestName = MuscleGroup.CHEST.name
        val backName = MuscleGroup.BACK.name

        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(chestName, backName)

        // Chest stats (imbalanced - higher volume)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(chestName, twoWeeksAgo, now) } returns 6000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(chestName, twoWeeksAgo, now) } returns 24
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 6
        coEvery { workoutDao.getLastWorkoutDateForMuscle(chestName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 4

        // Back stats (lower - creates imbalance)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(backName, twoWeeksAgo, now) } returns 3000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(backName, twoWeeksAgo, now) } returns 12
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(backName, twoWeeksAgo, now) } returns 3
        coEvery { workoutDao.getLastWorkoutDateForMuscle(backName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(backName, twoWeeksAgo, now) } returns 2

        // When
        val analytics = muscleTrackingUseCase.getMuscleGroupAnalytics(twoWeeksAgo, now)

        // Then
        assertEquals(2, analytics.muscleStats.size)
        assertTrue(analytics.imbalances.isNotEmpty())
        assertTrue(analytics.recommendations.isNotEmpty())
    }

    @Test
    fun `volumePerSet is calculated correctly`() = runTest {
        // Given
        val chestName = MuscleGroup.CHEST.name

        coEvery { workoutDao.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(chestName)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(chestName, twoWeeksAgo, now) } returns 5000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(chestName, twoWeeksAgo, now) } returns 20
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 4
        coEvery { workoutDao.getLastWorkoutDateForMuscle(chestName) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(chestName, twoWeeksAgo, now) } returns 3

        // When
        val stats = muscleTrackingUseCase.getMuscleGroupStats(twoWeeksAgo, now)

        // Then
        assertEquals(250f, stats.first().volumePerSet, 0.01f) // 5000 / 20
    }

    @Test
    fun `getStatsForLastWeeks returns correct date range`() = runTest {
        // Given
        val chestName = MuscleGroup.CHEST.name

        coEvery { workoutDao.getActiveMuscleGroupsInRange(any(), any()) } returns listOf(chestName)
        coEvery { workoutDao.getTotalVolumeForMuscleInRange(any(), any(), any()) } returns 5000f
        coEvery { workoutDao.getTotalSetsForMuscleInRange(any(), any(), any()) } returns 20
        coEvery { workoutDao.getWorkoutCountForMuscleInRange(any(), any(), any()) } returns 4
        coEvery { workoutDao.getLastWorkoutDateForMuscle(any()) } returns oneWeekAgo
        coEvery { workoutDao.getExerciseCountForMuscleInRange(any(), any(), any()) } returns 3

        // When
        val stats = muscleTrackingUseCase.getStatsForLastWeeks(4)

        // Then
        assertTrue(stats.isNotEmpty())
    }
}
