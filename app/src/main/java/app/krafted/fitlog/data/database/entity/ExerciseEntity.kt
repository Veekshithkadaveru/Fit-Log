package app.krafted.fitlog.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val primaryMuscle: String,
    val secondaryMuscles: String, // Comma-separated list
    val category: String, // e.g., "Compound", "Isolation"
    val equipment: String, // e.g., "Barbell", "Dumbbell", "Cable", "Bodyweight"
    val thumbnailRes: String? = null // Drawable resource name
)

