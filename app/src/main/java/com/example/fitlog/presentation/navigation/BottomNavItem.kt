package com.example.fitlog.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Bottom navigation item data class
 */
data class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

/**
 * Bottom navigation items for the app
 */
val bottomNavItems = listOf(
    BottomNavItem(
        route = FitLogDestinations.HOME,
        icon = Icons.Default.Home,
        label = "Home"
    ),
    BottomNavItem(
        route = FitLogDestinations.WORKOUT,
        icon = Icons.Default.PlayArrow,
        label = "Workout"
    ),
    BottomNavItem(
        route = FitLogDestinations.EXERCISES,
        icon = Icons.Default.FitnessCenter,
        label = "Exercises"
    ),
    BottomNavItem(
        route = FitLogDestinations.HISTORY,
        icon = Icons.Default.History,
        label = "History"
    ),
    BottomNavItem(
        route = FitLogDestinations.SETTINGS,
        icon = Icons.Default.Settings,
        label = "Settings"
    )
)