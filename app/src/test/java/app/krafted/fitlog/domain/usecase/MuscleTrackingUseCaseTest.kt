package app.krafted.fitlog.domain.usecase

import app.krafted.fitlog.domain.model.ImbalanceSeverity
import app.krafted.fitlog.domain.model.MuscleGroup
import app.krafted.fitlog.domain.model.RecommendationType
import app.krafted.fitlog.domain.repository.WorkoutRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class MuscleTrackingUseCaseTest {

    private lateinit var workoutRepository: WorkoutRepository
    private lateinit var muscleTrackingUseCase: MuscleTrackingUseCase

    private val now = System.currentTimeMillis()
    private val oneWeekAgo = now - TimeUnit.DAYS.toMillis(7)
    private val twoWeeksAgo = now - TimeUnit.DAYS.toMillis(14)

    @Before
    fun setup() {
        workoutRepository = mockk()
        muscleTrackingUseCase = MuscleTrackingUseCase(workoutRepository)
    }

    @Test
    fun `getMuscleGroupStats returns correct stats for single muscle`() = runTest {
        // Given
        val muscleGroup = MuscleGroup.CHEST
        coEvery { workoutRepository.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(muscleGroup)
        coEvery { workoutRepository.getTotalVolumeForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 5000f
        coEvery { workoutRepository.getTotalSetsForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 20
        coEvery { workoutRepository.getWorkoutCountForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 4
        coEvery { workoutRepository.getLastWorkoutDateForMuscle(muscleGroup) } returns oneWeekAgo
        coEvery { workoutRepository.getExerciseCountForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 3

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
        coEvery { workoutRepository.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns emptyList()

        // When
        val stats = muscleTrackingUseCase.getMuscleGroupStats(twoWeeksAgo, now)

        // Then
        assertTrue(stats.isEmpty())
    }

    @Test
    fun `getWeeklyMuscleFrequencies calculates correct frequencies`() = runTest {
        // Given
        val muscleGroup = MuscleGroup.BACK
        coEvery { workoutRepository.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(muscleGroup)
        coEvery { workoutRepository.getTotalVolumeForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 6000f
        coEvery { workoutRepository.getTotalSetsForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 30
        coEvery { workoutRepository.getWorkoutCountForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 6
        // Needed for getMuscleGroupStats internal call
        coEvery { workoutRepository.getLastWorkoutDateForMuscle(muscleGroup) } returns oneWeekAgo
        coEvery { workoutRepository.getExerciseCountForMuscleInRange(muscleGroup, twoWeeksAgo, now) } returns 5

        // When
        val frequencies = muscleTrackingUseCase.getWeeklyMuscleFrequencies(twoWeeksAgo, now)

        // Then
        assertEquals(1, frequencies.size)
        val backFrequency = frequencies.first()
        assertEquals(MuscleGroup.BACK, backFrequency.muscleGroup)
        assertEquals(3, backFrequency.workoutsPerWeek) // 6 workouts / 2 weeks
        assertEquals(15, backFrequency.setsPerWeek) // 30 sets / 2 weeks (30 / 2 = 15)
        assertEquals(3000f, backFrequency.totalVolumePerWeek, 0.01f) // 6000 / 2
    }

    @Test
    fun `getMuscleGroupAnalytics detects severe chest-back imbalance`() = runTest {
        // Given - Chest has 3x more volume than back
        val chest = MuscleGroup.CHEST
        val back = MuscleGroup.BACK

        // Setup active muscles
        coEvery { workoutRepository.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(chest, back)

        // Chest stats
        setupMockStats(chest, 9000f, 30, 6, oneWeekAgo, 4)

        // Back stats (much lower)
        setupMockStats(back, 3000f, 10, 2, oneWeekAgo, 2)

        // When
        val analytics = muscleTrackingUseCase.getMuscleGroupAnalytics(twoWeeksAgo, now)
        val imbalances = analytics.imbalances

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
    fun `getMuscleGroupAnalytics generates recommendations for undertrained muscle`() = runTest {
        // Given - Chest trained only once per week (below optimal)
        val chest = MuscleGroup.CHEST
        coEvery { workoutRepository.getActiveMuscleGroupsInRange(twoWeeksAgo, now) } returns listOf(chest)
        setupMockStats(chest, 2000f, 10, 2, oneWeekAgo, 2) // 2 workouts in 2 weeks = 1x/week

        // When
        val analytics = muscleTrackingUseCase.getMuscleGroupAnalytics(twoWeeksAgo, now)
        val recommendations = analytics.recommendations

        // Then
        assertTrue(recommendations.isNotEmpty())
        val increaseFrequencyRec = recommendations.firstOrNull {
            it.muscleGroup == MuscleGroup.CHEST && it.type == RecommendationType.INCREASE_FREQUENCY
        }
        assertNotNull(increaseFrequencyRec)
    }

    // Helper to reduce boilerplate
    private fun setupMockStats(
        muscle: MuscleGroup,
        volume: Float,
        sets: Int,
        workouts: Int,
        lastDate: Long,
        exercises: Int
    ) {
        coEvery { workoutRepository.getTotalVolumeForMuscleInRange(muscle, twoWeeksAgo, now) } returns volume
        coEvery { workoutRepository.getTotalSetsForMuscleInRange(muscle, twoWeeksAgo, now) } returns sets
        coEvery { workoutRepository.getWorkoutCountForMuscleInRange(muscle, twoWeeksAgo, now) } returns workouts
        coEvery { workoutRepository.getLastWorkoutDateForMuscle(muscle) } returns lastDate
        coEvery { workoutRepository.getExerciseCountForMuscleInRange(muscle, twoWeeksAgo, now) } returns exercises
    }
}
