package com.example.fitlog.data.database

import android.content.Context
import androidx.room.Room
import com.example.fitlog.data.database.dao.BodyweightDao
import com.example.fitlog.data.database.dao.ExerciseDao
import com.example.fitlog.data.database.dao.PersonalRecordDao
import com.example.fitlog.data.database.dao.RepRangeRecordDao
import com.example.fitlog.data.database.dao.RoutineDao
import com.example.fitlog.data.database.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideExerciseDao(database: AppDatabase): ExerciseDao {
        return database.exerciseDao()
    }

    @Provides
    @Singleton
    fun provideRoutineDao(database: AppDatabase): RoutineDao {
        return database.routineDao()
    }

    @Provides
    @Singleton
    fun provideWorkoutDao(database: AppDatabase): WorkoutDao {
        return database.workoutDao()
    }

    @Provides
    @Singleton
    fun provideBodyweightDao(database: AppDatabase): BodyweightDao {
        return database.bodyweightDao()
    }

    @Provides
    @Singleton
    fun providePersonalRecordDao(database: AppDatabase): PersonalRecordDao {
        return database.personalRecordDao()
    }

    @Provides
    @Singleton
    fun provideRepRangeRecordDao(database: AppDatabase): RepRangeRecordDao {
        return database.repRangeRecordDao()
    }
}


