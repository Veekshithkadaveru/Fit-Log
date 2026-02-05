package app.krafted.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = RoutineEntity::class,
            parentColumns = ["id"],
            childColumns = ["routineId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("routineId")]
)
data class WorkoutEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val startTime: Long, // Epoch milliseconds
    val endTime: Long? = null, // Null if workout is in progress
    val routineId: Int? = null, // Optional - can be a free-form workout
    val notes: String? = null,
    val isCardio: Boolean = false // True if primarily a cardio session
)

