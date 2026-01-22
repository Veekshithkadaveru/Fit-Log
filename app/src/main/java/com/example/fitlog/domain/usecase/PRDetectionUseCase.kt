package com.example.fitlog.domain.usecase

import com.example.fitlog.domain.model.PRCheckResult
import com.example.fitlog.domain.model.PersonalRecord
import com.example.fitlog.domain.repository.PersonalRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for detecting and managing personal records (PRs).
 * Handles automatic PR detection when completing sets.
 */
class PRDetectionUseCase @Inject constructor(
    private val personalRecordRepository: PersonalRecordRepository
) {

    /**
     * Check if a completed set represents a new personal record.
     * Returns detailed information about which types of PRs were achieved.
     */
    suspend fun checkForPR(
        exerciseId: Int,
        weight: Float,
        reps: Int
    ): PRCheckResult = withContext(Dispatchers.IO) {
        if (weight <= 0 || reps <= 0) {
            return@withContext PRCheckResult()
        }

        val existingRecord = personalRecordRepository.getByExerciseId(exerciseId)
        val volume = weight * reps

        if (existingRecord == null) {
            // First time doing this exercise - it's a PR!
            PRCheckResult(
                isNewWeightPR = true,
                isNewRepsPR = true,
                isNewVolumePR = true
            )
        } else {
            PRCheckResult(
                isNewWeightPR = weight > existingRecord.maxWeight,
                isNewRepsPR = reps > existingRecord.maxReps,
                isNewVolumePR = volume > existingRecord.maxVolume
            )
        }
    }

    /**
     * Update personal records if the set represents a new PR.
     * Returns the PR check result and updates the database if needed.
     */
    suspend fun checkAndUpdatePR(
        exerciseId: Int,
        weight: Float,
        reps: Int
    ): PRCheckResult = withContext(Dispatchers.IO) {
        val prResult = checkForPR(exerciseId, weight, reps)

        if (prResult.isAnyPR) {
            val existingRecord = personalRecordRepository.getByExerciseId(exerciseId)
            val volume = weight * reps
            val currentTime = System.currentTimeMillis()

            val newMaxWeight = if (prResult.isNewWeightPR) weight else (existingRecord?.maxWeight ?: 0f)
            val newMaxReps = if (prResult.isNewRepsPR) reps else (existingRecord?.maxReps ?: 0)
            val newMaxVolume = if (prResult.isNewVolumePR) volume else (existingRecord?.maxVolume ?: 0f)

            personalRecordRepository.upsertRecord(
                exerciseId = exerciseId,
                maxWeight = newMaxWeight,
                maxReps = newMaxReps,
                maxVolume = newMaxVolume,
                achievedDate = currentTime
            )
        }

        prResult
    }

    /**
     * Get the current personal record for an exercise.
     */
    suspend fun getPersonalRecord(exerciseId: Int): PersonalRecord? {
        return personalRecordRepository.getByExerciseId(exerciseId)
    }

    /**
     * Get personal record as a Flow for reactive updates.
     */
    fun getPersonalRecordFlow(exerciseId: Int): Flow<PersonalRecord?> {
        return personalRecordRepository.getByExerciseIdFlow(exerciseId)
    }

    /**
     * Get recent personal records.
     */
    fun getRecentPRs(limit: Int = 10): Flow<List<PersonalRecord>> {
        return personalRecordRepository.getRecentRecords(limit)
    }

    /**
     * Get all personal records.
     */
    fun getAllPRs(): Flow<List<PersonalRecord>> {
        return personalRecordRepository.getAllRecords()
    }

    /**
     * Get count of PRs achieved in a date range.
     */
    suspend fun getPRCountInRange(startDate: Long, endDate: Long): Int {
        return personalRecordRepository.getRecordCountInRange(startDate, endDate)
    }

    /**
     * Get total PR count.
     */
    suspend fun getTotalPRCount(): Int {
        return personalRecordRepository.getTotalRecordCount()
    }

    /**
     * Calculate estimated 1RM using Epley formula.
     * 1RM = weight × (1 + reps/30)
     */
    fun calculateEstimated1RM(weight: Float, reps: Int): Float {
        if (reps <= 0 || weight <= 0) return 0f
        if (reps == 1) return weight
        return weight * (1 + reps / 30f)
    }
}
