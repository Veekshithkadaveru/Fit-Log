package app.krafted.fitlog.domain.usecase

import app.krafted.fitlog.domain.model.PRCheckResult
import app.krafted.fitlog.domain.model.PersonalRecord
import app.krafted.fitlog.domain.model.RepRange
import app.krafted.fitlog.domain.model.RepRangePRResult
import app.krafted.fitlog.domain.model.RepRangeRecord
import app.krafted.fitlog.domain.repository.PersonalRecordRepository
import app.krafted.fitlog.domain.repository.RepRangeRecordRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Use case for detecting and managing personal records (PRs).
 * 
 * Implements professional-standard PR detection similar to apps like Strong, Hevy, and JEFIT:
 * - **Weight PR**: New max weight lifted for this exercise
 * - **Volume PR**: New best set volume (weight × reps) for a single set
 * - **Estimated 1RM PR**: New best estimated one-rep max
 * - **Rep Range PR**: New best weight within a specific rep range (only if it also improves or matches overall 1RM)
 * 
 * Key principle: A set is only marked as PR if it represents an ACTUAL IMPROVEMENT in performance,
 * not just a new entry in a different rep range bucket.
 */
class PRDetectionUseCase @Inject constructor(
    private val personalRecordRepository: PersonalRecordRepository,
    private val repRangeRecordRepository: RepRangeRecordRepository
) {

    /**
     * Check if a completed set represents a new personal record.
     * Uses professional-standard PR detection:
     * - Weight PR: Heavier than ever before
     * - Volume PR: Higher set volume (weight × reps) than ever before
     * - Estimated 1RM PR: Higher estimated 1RM than any previous set
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
        val estimated1RM = calculateEstimated1RM(weight, reps)
        
        // Get best estimated 1RM from rep range records
        val bestExisting1RM = repRangeRecordRepository.getBestEstimated1RM(exerciseId) ?: 0f

        if (existingRecord == null) {
            // First time doing this exercise - it's a PR!
            PRCheckResult(
                isNewWeightPR = true,
                isNewRepsPR = true,
                isNewVolumePR = true,
                isNew1RMPR = true,
                estimated1RM = estimated1RM
            )
        } else {
            PRCheckResult(
                isNewWeightPR = weight > existingRecord.maxWeight,
                isNewRepsPR = reps > existingRecord.maxReps,
                isNewVolumePR = volume > existingRecord.maxVolume,
                isNew1RMPR = estimated1RM > bestExisting1RM,
                estimated1RM = estimated1RM
            )
        }
    }

    /**
     * Check for rep-range specific PR using professional standards.
     * 
     * A rep-range PR is awarded when:
     * 1. It's a new best weight for this specific rep range (e.g., best 5RM), AND
     * 2. The estimated 1RM is at least as good as the overall best 1RM
     * 
     * This ensures that a lower rep count at the same weight (which produces a lower 1RM)
     * is NOT incorrectly marked as a PR.
     * 
     * Example:
     * - 22kg × 12 reps → Est 1RM = 30.8kg → PR ✓ (new best)
     * - 22kg × 8 reps → Est 1RM = 27.9kg → NOT PR (lower 1RM than existing best)
     * - 25kg × 8 reps → Est 1RM = 31.7kg → PR ✓ (beats previous best 1RM)
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

        // Get best estimated 1RM across ALL rep ranges (the overall best performance)
        val bestEstimated1RM = repRangeRecordRepository.getBestEstimated1RM(exerciseId) ?: 0f

        // Professional PR detection:
        // A rep-range PR requires the set to represent ACTUAL improvement
        val isWeightBetterForRepRange = existingRepRangeRecord == null || weight > existingRepRangeRecord.bestWeight
        
        // Critical check: The new 1RM must be at least as good as the best ever
        // This prevents 22kg×8 (1RM=27.9) from being a PR when 22kg×12 (1RM=30.8) exists
        val isEstimated1RMGoodEnough = estimated1RM >= bestEstimated1RM
        
        // Only count as rep-range PR if BOTH conditions are met
        val isNewRepRangePR = isWeightBetterForRepRange && isEstimated1RMGoodEnough
        
        // This is a new overall 1RM if it strictly beats the previous best
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

        // Update legacy overall PR record if any metric improved
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

        // Update rep-range specific record ONLY if it's truly a PR
        // (weight is better AND estimated 1RM is at least as good as overall best)
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

        // Return a PRCheckResult that reflects true PR achievement
        // A PR badge should show if: new weight PR, new volume PR, new 1RM PR, OR valid rep-range PR
        prResult.copy(
            isNewWeightPR = prResult.isNewWeightPR || repRangePRResult.isNewRepRangePR,
            isNew1RMPR = prResult.isNew1RMPR || repRangePRResult.isNew1RMPR
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

        // Update rep-range specific record only if it's a valid PR
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
     * Calculate estimated 1RM using the Epley formula (industry standard).
     * 1RM = weight × (1 + reps/30)
     * 
     * This formula is accurate for rep ranges up to ~12 reps.
     * For higher rep ranges, it may overestimate.
     */
    fun calculateEstimated1RM(weight: Float, reps: Int): Float {
        if (reps <= 0 || weight <= 0) return 0f
        if (reps == 1) return weight
        return weight * (1 + reps / 30f)
    }
}
