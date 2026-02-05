package app.krafted.fitlog.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Domain model representing a personal record for an exercise
 */
data class PersonalRecord(
    val id: Int = 0,
    val exerciseId: Int,
    val exercise: Exercise? = null,
    val maxWeight: Float = 0f,
    val maxReps: Int = 0,
    val maxVolume: Float = 0f,
    val achievedDate: Long
) {
    val achievedLocalDate: LocalDate
        @RequiresApi(Build.VERSION_CODES.O)
        get() = Instant.ofEpochMilli(achievedDate).atZone(ZoneId.systemDefault()).toLocalDate()
}

/**
 * Result of a PR check.
 * Tracks multiple types of PRs as per professional fitness app standards:
 * - Weight PR: New max weight
 * - Reps PR: New max reps
 * - Volume PR: New max set volume (weight × reps)
 * - 1RM PR: New best estimated one-rep max
 */
data class PRCheckResult(
    val isNewWeightPR: Boolean = false,
    val isNewRepsPR: Boolean = false,
    val isNewVolumePR: Boolean = false,
    val isNew1RMPR: Boolean = false,
    val estimated1RM: Float = 0f
) {
    val isAnyPR: Boolean
        get() = isNewWeightPR || isNewRepsPR || isNewVolumePR || isNew1RMPR
}

