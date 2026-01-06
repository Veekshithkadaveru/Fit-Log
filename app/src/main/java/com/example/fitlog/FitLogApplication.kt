package com.example.fitlog

import android.app.Application
import com.example.fitlog.data.database.DatabaseInitializer
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FitLogApplication : Application() {
    
    @Inject
    lateinit var databaseInitializer: DatabaseInitializer
    
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize database with seed data on app startup
        applicationScope.launch {
            databaseInitializer.initializeDatabase()
        }
    }
}

