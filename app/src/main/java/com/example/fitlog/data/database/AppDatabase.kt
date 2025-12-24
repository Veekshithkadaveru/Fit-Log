package com.example.fitlog.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fitlog.data.database.dao.BodyweightDao
import com.example.fitlog.data.database.dao.ExerciseDao
import com.example.fitlog.data.database.dao.PersonalRecordDao
import com.example.fitlog.data.database.dao.RoutineDao
import com.example.fitlog.data.database.dao.WorkoutDao
import com.example.fitlog.data.database.entity.BodyweightEntity
import com.example.fitlog.data.database.entity.CardioSessionEntity
import com.example.fitlog.data.database.entity.ExerciseEntity
import com.example.fitlog.data.database.entity.PersonalRecordEntity
import com.example.fitlog.data.database.entity.RoutineEntity
import com.example.fitlog.data.database.entity.RoutineExerciseEntity
import com.example.fitlog.data.database.entity.WorkoutEntity
import com.example.fitlog.data.database.entity.WorkoutSetEntity

@Database(
    entities = [
        ExerciseEntity::class,
        RoutineEntity::class,
        RoutineExerciseEntity::class,
        WorkoutEntity::class,
        WorkoutSetEntity::class,
        CardioSessionEntity::class,
        BodyweightEntity::class,
        PersonalRecordEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun bodyweightDao(): BodyweightDao
    abstract fun personalRecordDao(): PersonalRecordDao

    companion object {
        const val DATABASE_NAME = "fitlog_database"
    }
}


