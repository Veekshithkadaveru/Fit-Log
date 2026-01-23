package com.example.fitlog.domain.usecase

import com.example.fitlog.domain.model.PRCheckResult
import com.example.fitlog.domain.model.PersonalRecord
import com.example.fitlog.domain.model.RepRange
import com.example.fitlog.domain.model.RepRangePRResult
import com.example.fitlog.domain.model.RepRangeRecord
import com.example.fitlog.domain.repository.PersonalRecordRepository
import com.example.fitlog.domain.repository.RepRangeRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for detecting and managing personal records (PRs).
 * Handles automatic PR detection when completing sets.
 * Supports both legacy overall PRs and new rep-range specific PRs.
 */
class PRDetectionUseCase @Inject constructor(
    private val personalRecordRepository: PersonalRecordRepository,
    private val repRangeRecordRepository: RepRangeRecordRepository
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
     * Check for rep-range specific PR.
     * This is the new granular system that tracks PRs per rep range (1RM, 3RM, 5RM, etc.)
     */
    suspend fun checkForRepRangePR(
        exerciseId: Int,
        weight: Float,
        reps: Int
    ): RepRangePRResult = withContext(Dispatchers.IO) {
        if (weight <= 0 || reps <= 0) {
            return@withContext RepRangePRResult(
                repRange = RepRange.fromReps(1),
                isNewRepRangePR = false,
                isNew1RMPR = false,
                previousBestWeight = null,
                newWeight = 0f,
                previousEstimated1RM = null,
                newEstimated1RM = 0f
            )
        }

        val repRange = RepRange.fromReps(reps)
        val estimated1RM = calculateEstimated1RM(weight, reps)

        // Get existing record for this rep range
        val existingRepRangeRecord = repRangeRecordRepository.getByExerciseIdAndRepRange(exerciseId, repRange)

        // Get best estimated 1RM across all rep ranges
        val bestEstimated1RM = repRangeRecordRepository.getBestEstimated1RM(exerciseId) ?: 0f

        val isNewRepRangePR = existingRepRangeRecord == null || weight > existingRepRangeRecord.bestWeight
        val isNew1RMPR = estimated1RM > bestEstimated1RM

        RepRangePRResult(
            repRange = repRange,
            isNewRepRangePR = isNewRepRangePR,
            isNew1RMPR = isNew1RMPR,
            previousBestWeight = existingRepRangeRecord?.bestWeight,
            newWeight = weight,
            previousEstimated1RM = if (bestEstimated1RM > 0) bestEstimated1RM else null,
            newEstimated1RM = estimated1RM
        )
    }

    /**
     * Update personal records if the set represents a new PR.
     * Returns the PR check result and updates the database if needed.
     * Also updates rep-range specific records.
     */
    suspend fun checkAndUpdatePR(
        exerciseId: Int,
        weight: Float,
        reps: Int
    ): PRCheckResult = withContext(Dispatchers.IO) {
        val prResult = checkForPR(exerciseId, weight, reps)
        val repRangePRResult = checkForRepRangePR(exerciseId, weight, reps)

        // Update legacy overall PR record
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

        // Update rep-range specific record
        if (repRangePRResult.isNewRepRangePR) {
            val currentTime = System.currentTimeMillis()
            repRangeRecordRepository.upsertRecord(
                exerciseId = exerciseId,
                repRange = repRangePRResult.repRange,
                bestWeight = weight,
                repsAtBestWeight = reps,
                estimated1RM = repRangePRResult.newEstimated1RM,
                achievedDate = currentTime
            )
        }

        // Return a PRCheckResult that also reflects rep-range PR
        // If it's a new rep-range PR, treat it as a weight PR for the badge
        prResult.copy(
            isNewWeightPR = prResult.isNewWeightPR || repRangePRResult.isNewRepRangePR
        )
    }

    /**
     * Check and update PR, returning the detailed rep-range result.
     * Use this when you need to show which specific rep range PR was achieved.
     */
    suspend fun checkAndUpdatePRWithRepRange(
        exerciseId: Int,
        weight: Float,
        reps: Int
    ): Pair<PRCheckResult, RepRangePRResult> = withContext(Dispatchers.IO) {
        val prResult = checkForPR(exerciseId, weight, reps)
        val repRangePRResult = checkForRepRangePR(exerciseId, weight, reps)

        // Update legacy overall PR record
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

        // Update rep-range specific record
        if (repRangePRResult.isNewRepRangePR) {
            val currentTime = System.currentTimeMillis()
            repRangeRecordRepository.upsertRecord(
                exerciseId = exerciseId,
                repRange = repRangePRResult.repRange,
                bestWeight = weight,
                repsAtBestWeight = reps,
                estimated1RM = repRangePRResult.newEstimated1RM,
                achievedDate = currentTime
            )
        }

        Pair(prResult, repRangePRResult)
    }

    /**
     * Get the current personal record for an exercise.
     */
    suspend fun getPersonalRecord(exerciseId: Int): PersonalRecord? {
        return personalRecordRepository.getByExerciseId(exerciseId)
    }

    /**
     * Get all rep-range records for an exercise.
     */
    suspend fun getRepRangeRecords(exerciseId: Int): List<RepRangeRecord> {
        return repRangeRecordRepository.getByExerciseId(exerciseId)
    }

    /**
     * Get rep-range records as a Flow for reactive updates.
     */
    fun getRepRangeRecordsFlow(exerciseId: Int): Flow<List<RepRangeRecord>> {
        return repRangeRecordRepository.getByExerciseIdFlow(exerciseId)
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
     * Get recent rep-range records.
     */
    fun getRecentRepRangePRs(limit: Int = 10): Flow<List<RepRangeRecord>> {
        return repRangeRecordRepository.getRecentRecords(limit)
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
     * Get best estimated 1RM for an exercise across all rep ranges.
     */
    suspend fun getBestEstimated1RM(exerciseId: Int): Float? {
        return repRangeRecordRepository.getBestEstimated1RM(exerciseId)
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
