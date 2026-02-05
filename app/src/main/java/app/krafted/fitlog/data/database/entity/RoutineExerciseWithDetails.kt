package app.krafted.fitlog.data.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class RoutineExerciseWithDetails(
    @Embedded val routineExercise: RoutineExerciseEntity,
    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "id"
    )
    val exercise: ExerciseEntity?
)
