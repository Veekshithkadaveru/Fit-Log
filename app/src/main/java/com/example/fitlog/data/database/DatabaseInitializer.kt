package com.example.fitlog.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.example.fitlog.data.database.dao.ExerciseDao
import com.example.fitlog.data.database.dao.RoutineDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

/**
 * Handles database initialization and seeding
 */
@Singleton
class DatabaseInitializer @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val routineDao: RoutineDao,
    @ApplicationContext private val context: Context
) {
    
    companion object {
        private val DATABASE_INITIALIZED = booleanPreferencesKey("database_initialized_v4")
    }
    
    /**
     * Initialize database with seed data if not already done
     */
    suspend fun initializeDatabase() {
        val isInitialized = context.dataStore.data
            .map { preferences ->
                preferences[DATABASE_INITIALIZED] ?: false
            }.first()
            
        if (!isInitialized) {
            seedExercises()
            seedRoutineTemplates()
            markDatabaseAsInitialized()
        }
    }
    
    /**
     * Force re-initialize database (useful for testing or data updates)
     */
    suspend fun forceInitializeDatabase() {
        clearDatabase()
        seedExercises()
        seedRoutineTemplates()
        markDatabaseAsInitialized()
    }
    
    /**
     * Seed the database with exercise data
     */
    private suspend fun seedExercises() {
        try {
            exerciseDao.insertAll(SeedData.exercises)
        } catch (e: Exception) {
            // Log error but don't crash the app
            e.printStackTrace()
        }
    }
    
    /**
     * Seed the database with routine template data
     */
    private suspend fun seedRoutineTemplates() {
        try {
            // Insert routine templates first
            for (routine in SeedData.routineTemplates) {
                routineDao.insertRoutine(routine)
                // Clear existing exercises for this template to avoid duplicates on re-seed
                routineDao.deleteAllExercisesFromRoutine(routine.id)
            }
            
            // Then insert routine exercises
            routineDao.insertRoutineExercises(SeedData.routineExercises)
        } catch (e: Exception) {
            // Log error but don't crash the app
            e.printStackTrace()
        }
    }
    
    /**
     * Clear all database tables (for re-seeding)
     */
    private suspend fun clearDatabase() {
        try {
            // Clear routine tables first (due to foreign key constraints)
            routineDao.deleteAllRoutineExercises()
            routineDao.deleteAllRoutines()
            
            // Clear exercise table
            exerciseDao.deleteAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Mark database as initialized in DataStore
     */
    private suspend fun markDatabaseAsInitialized() {
        context.dataStore.edit { preferences ->
            preferences[DATABASE_INITIALIZED] = true
        }
    }
    
    /**
     * Check if database has been initialized
     */
    suspend fun isDatabaseInitialized(): Boolean {
        return context.dataStore.data
            .map { preferences ->
                preferences[DATABASE_INITIALIZED] ?: false
            }.first()
    }
    
    /**
     * Reset initialization status (for testing)
     */
    suspend fun resetInitializationStatus() {
        context.dataStore.edit { preferences ->
            preferences[DATABASE_INITIALIZED] = false
        }
    }
}