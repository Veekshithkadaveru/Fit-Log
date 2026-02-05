package app.krafted.fitlog.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Represents the rep ranges we track PRs for
 */
enum class RepRange(val minReps: Int, val maxReps: Int, val displayName: String) {
    ONE_RM(1, 1, "1RM"),
    THREE_RM(2, 3, "3RM"),
    FIVE_RM(4, 5, "5RM"),
    EIGHT_RM(6, 8, "8RM"),
    TEN_RM(9, 10, "10RM"),
    TWELVE_PLUS(11, Int.MAX_VALUE, "12+RM");

    companion object {
        fun fromReps(reps: Int): RepRange {
            return entries.first { reps >= it.minReps && reps <= it.maxReps }
        }
    }
}

/**
 * Domain model for rep-range specific personal records
 */
data class RepRangeRecord(
    val id: Int = 0,
    val exerciseId: Int,
    val exercise: Exercise? = null,
    val repRange: RepRange,
    val bestWeight: Float,
    val repsAtBestWeight: Int,
    val estimated1RM: Float,
    val achievedDate: Long
) {
    val achievedLocalDate: LocalDate
        @RequiresApi(Build.VERSION_CODES.O)
        get() = Instant.ofEpochMilli(achievedDate).atZone(ZoneId.systemDefault()).toLocalDate()
}

/**
 * Result of checking for rep-range specific PRs
 */
data class RepRangePRResult(
    val repRange: RepRange,
    val isNewRepRangePR: Boolean, // New best weight for this rep range
    val isNew1RMPR: Boolean, // New estimated 1RM record
    val previousBestWeight: Float?,
    val newWeight: Float,
    val previousEstimated1RM: Float?,
    val newEstimated1RM: Float
) {
    val isAnyPR: Boolean
        get() = isNewRepRangePR || isNew1RMPR
}
