package com.example.fitlog.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.fitlog.presentation.components.FitLogBottomBar
import com.example.fitlog.presentation.navigation.FitLogNavGraph

/**
 * Main app composable that sets up the navigation and bottom bar
 */
@Composable
fun FitLogApp() {
    val navController = rememberNavController()
    
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            FitLogBottomBar(navController = navController)
        }
    ) { innerPadding ->
        FitLogNavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}