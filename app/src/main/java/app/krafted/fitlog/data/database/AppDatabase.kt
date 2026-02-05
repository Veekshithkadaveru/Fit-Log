package app.krafted.fitlog.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import app.krafted.fitlog.data.database.dao.BodyweightDao
import app.krafted.fitlog.data.database.dao.ExerciseDao
import app.krafted.fitlog.data.database.dao.PersonalRecordDao
import app.krafted.fitlog.data.database.dao.RepRangeRecordDao
import app.krafted.fitlog.data.database.dao.RoutineDao
import app.krafted.fitlog.data.database.dao.WorkoutDao
import app.krafted.fitlog.data.database.entity.BodyweightEntity
import app.krafted.fitlog.data.database.entity.CardioSessionEntity
import app.krafted.fitlog.data.database.entity.ExerciseEntity
import app.krafted.fitlog.data.database.entity.PersonalRecordEntity
import app.krafted.fitlog.data.database.entity.RepRangeRecordEntity
import app.krafted.fitlog.data.database.entity.RoutineEntity
import app.krafted.fitlog.data.database.entity.RoutineExerciseEntity
import app.krafted.fitlog.data.database.entity.WorkoutEntity
import app.krafted.fitlog.data.database.entity.WorkoutSetEntity

@Database(
    entities = [
        ExerciseEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutSetEntity::class,
        CardioSessionEntity::class,
        BodyweightEntity::class,
        PersonalRecordEntity::class,
        RepRangeRecordEntity::class
    ],
    version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun bodyweightDao(): BodyweightDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun repRangeRecordDao(): RepRangeRecordDao

    companion object {
        const val DATABASE_NAME = "fitlog_database"
    }
}


