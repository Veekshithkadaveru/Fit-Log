package app.krafted.fitlog.di

import android.content.Context
import app.krafted.fitlog.data.database.DatabaseInitializer
import app.krafted.fitlog.data.database.dao.ExerciseDao
import app.krafted.fitlog.data.database.dao.RoutineDao
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
        routineDao: RoutineDao,
        @ApplicationContext context: Context
    ): DatabaseInitializer {
        return DatabaseInitializer(exerciseDao, routineDao, context)
    }
}