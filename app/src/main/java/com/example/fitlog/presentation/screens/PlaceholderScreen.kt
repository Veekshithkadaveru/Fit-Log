package com.example.fitlog.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fitlog.presentation.navigation.FitLogRoutes

/**
 * Placeholder screen for development and testing navigation
 */
@Composable
fun PlaceholderScreen(
    title: String,
    subtitle: String? = null,
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium
        )
        
        subtitle?.let { sub ->
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = sub,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Demo navigation buttons for testing
        if (title == "Exercise Library") {
            Button(
                onClick = {
                    navController.navigate(FitLogRoutes.exerciseDetail(1))
                }
            ) {
                Text("View Exercise Detail (ID: 1)")
            }
        }
        
        if (title == "History") {
            Button(
                onClick = {
                    navController.navigate(FitLogRoutes.workoutDetail(1))
                }
            ) {
                Text("View Workout Detail (ID: 1)")
            }
        }

        if (title == "Workout") {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate("routine_list") }) {
                Text("Go to Routine List")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate(FitLogRoutes.routineEditor(0)) }) {
                Text("Go to Routine Editor (New)")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { navController.navigate("template_picker") }) {
                Text("Go to Template Picker")
            }
        }
    }
}