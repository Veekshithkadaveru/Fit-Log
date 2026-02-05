package app.krafted.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
 * Entity for tracking personal records per rep range.
 * Each exercise can have multiple records - one for each rep range.
 */
@Entity(
    tableName = "rep_range_records",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["exerciseId", "repRange"], unique = true),
        Index(value = ["exerciseId"])
    ]
)
data class RepRangeRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val exerciseId: Int,
    val repRange: String, // Stored as enum name (ONE_RM, THREE_RM, etc.)
    val bestWeight: Float, // Best weight lifted in this rep range
    val repsAtBestWeight: Int, // Exact reps done at the best weight
    val estimated1RM: Float, // Calculated using Epley formula
    val achievedDate: Long // When this PR was achieved
)
