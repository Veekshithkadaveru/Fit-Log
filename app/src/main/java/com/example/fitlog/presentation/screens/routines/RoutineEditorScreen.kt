package com.example.fitlog.presentation.screens.routines

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitlog.domain.model.RoutineExercise
import com.example.fitlog.presentation.viewmodel.RoutineNavigationEvent
import com.example.fitlog.presentation.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineEditorScreen(
    navController: NavController,
    routineId: Int,
    viewModel: RoutineViewModel = hiltViewModel()
) {
    val editorState by viewModel.editorUiState.collectAsState()

    // Initialize Editor
    LaunchedEffect(routineId) {
        viewModel.loadRoutine(routineId)
    }

    // Handle Navigation Events
    LaunchedEffect(true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is RoutineNavigationEvent.NavigateBack -> {
                    navController.popBackStack()
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (routineId == 0) "Create Routine" else "Edit Routine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.saveRoutine() }) {
                        if (editorState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        } else {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (editorState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                val routine = editorState.routine
                if (routine != null) {
                    LazyColumn(
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Routine Name and Day Input
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                OutlinedTextField(
                                    value = routine.name,
                                    onValueChange = { viewModel.updateRoutineName(it) },
                                    label = { Text("Routine Name") },
                                    modifier = Modifier.weight(0.7f),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = if (routine.dayOrder > 0) routine.dayOrder.toString() else "",
                                    onValueChange = { 
                                        // Simple day updates - handled via VM (need to add function)
                                        // For now just allow editing if function exists, else read-only visuals or ignore
                                        // I will add updateDayOrder to VM next.
                                        viewModel.updateRoutineDayOrder(it.toIntOrNull() ?: 0)
                                    },
                                    label = { Text("Day #") },
                                    modifier = Modifier.weight(0.3f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }
                        }

                        item {
                            Text(
                                text = "Exercises",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        // Exercise List
                        items(routine.exercises) { routineExercise ->
                            RoutineExerciseEditorItem(
                                routineExercise = routineExercise,
                                onUpdateTarget = { sets, reps ->
                                    viewModel.updateExerciseTarget(routineExercise.id, sets, reps)
                                }
                            )
                        }

                        // Add Exercise Button
                        item {
                            Button(
                                onClick = { 
                                    if (routineId != 0) {
                                        navController.navigate(com.example.fitlog.presentation.navigation.FitLogRoutes.exercisePicker(routineId))
                                    } else {
                                        // If creating new routine, we must save first? 
                                        // Or better, we save draft immediately?
                                        // For now, let's just show a toast or require save.
                                        // Actually, user likely expects it to just work. 
                                        // Saving a draft "New Routine" is a common pattern.
                                        // I'll skip complex draft logic for now and disable button if ID is 0?
                                        // Or better: Auto-save the routine structure first? 
                                        // Let's Disable for ID 0 with a note to "Save first to add exercises" if convenient, 
                                        // or better, implement auto-save.
                                        
                                        // Current approach: Disable button or show alert. 
                                        // Ideally passing ID 0 to picker is invalid because we need a real FK.
                                        // So, simply:
                                        /* Must save routine first */
                                        // I'll leave the TODO comment.
                                    }
                                },
                                enabled = routineId != 0, // Require generic save first
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (routineId == 0) "Save first to add exercises" else "Add Exercise")
                            }
                        }
                    }
                } else {
                    Text("Error loading routine", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
fun RoutineExerciseEditorItem(
    routineExercise: RoutineExercise,
    onUpdateTarget: (Int?, String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Exercise Name Header
            Text(
                text = routineExercise.exercise?.name ?: "Unknown Exercise",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Inputs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sets Input
                OutlinedTextField(
                    value = routineExercise.targetSets.toString(),
                    onValueChange = {
                        val sets = it.toIntOrNull()
                        onUpdateTarget(sets, null)
                    },
                    label = { Text("Sets") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )

                // Reps Input (String to allow "8-12" ranges)
                OutlinedTextField(
                    value = routineExercise.targetReps,
                    onValueChange = {
                        onUpdateTarget(null, it)
                    },
                    label = { Text("Reps") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
    }
}
