package com.example.fitlog.di

import android.content.Context
import com.example.fitlog.data.database.DatabaseInitializer
import com.example.fitlog.data.database.dao.ExerciseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Application-level Hilt module
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideDatabaseInitializer(
        exerciseDao: ExerciseDao,
        @ApplicationContext context: Context
    ): DatabaseInitializer {
        return DatabaseInitializer(exerciseDao, context)
    }
}