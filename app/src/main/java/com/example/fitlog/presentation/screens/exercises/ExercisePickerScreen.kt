package com.example.fitlog.presentation.screens.exercises

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitlog.presentation.viewmodel.ExerciseViewModel
import com.example.fitlog.presentation.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerScreen(
    navController: NavController,
    routineId: Int,
    exerciseViewModel: ExerciseViewModel = hiltViewModel(),
    routineViewModel: RoutineViewModel = hiltViewModel() // Shared or separate instance?
    // Note: Calling hiltViewModel() gives a scoped instance. 
    // RoutineViewModel is typically scoped to the Graph or Activity, or if we are just calling 'add', we can get a new one or use scoped.
    // However, since we are not in a nested graph, this gets a fresh RoutineViewModel or the activity scoped one?
    // Hilt defaults to NavBackStackEntry scope. This might be a DIFFERENT instance than the Editor.
    // BUT, we are interacting with the Repository/Database. So calling a method that updates DB is fine even if VM is different instance.
    // The Editor subscribes to DB flow, so it will update automatically.
) {
    val searchQuery by exerciseViewModel.searchQuery.collectAsState()
    val exercises by exerciseViewModel.filteredExercises.collectAsState()
    val selectedMuscle by exerciseViewModel.selectedMuscle.collectAsState()
    val muscleGroups by exerciseViewModel.muscleGroups.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Exercise") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { exerciseViewModel.onSearchQueryChange(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search exercises...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { exerciseViewModel.onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )

            // Muscle Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(muscleGroups) { muscle ->
                    FilterChip(
                        selected = selectedMuscle == muscle,
                        onClick = { exerciseViewModel.onMuscleSelect(muscle) },
                        label = { Text(muscle.displayName) }
                    )
                }
            }

            // Exercise List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(exercises) { exercise ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // Add to routine and close
                                routineViewModel.addExerciseToRoutine(routineId, exercise.id)
                                navController.popBackStack()
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = exercise.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = exercise.primaryMuscle.displayName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
