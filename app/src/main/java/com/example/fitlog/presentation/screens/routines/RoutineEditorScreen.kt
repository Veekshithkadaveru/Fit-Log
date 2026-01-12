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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
                    if (routineId != 0) {
                        IconButton(onClick = { viewModel.deleteRoutine() }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Routine")
                        }
                    }
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
                                    singleLine = true,
                                    isError = editorState.nameError != null,
                                    supportingText = {
                                        if (editorState.nameError != null) {
                                            Text(editorState.nameError!!)
                                        }
                                    }
                                )
                                OutlinedTextField(
                                    value = if (routine.dayOrder > 0) routine.dayOrder.toString() else "",
                                    onValueChange = { 
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
                        itemsIndexed(routine.exercises) { index, routineExercise ->
                            RoutineExerciseEditorItem(
                                routineExercise = routineExercise,
                                isFirst = index == 0,
                                isLast = index == routine.exercises.lastIndex,
                                onUpdateTarget = { sets, reps ->
                                    viewModel.updateExerciseTarget(routineExercise.id, sets, reps)
                                },
                                onMoveUp = { viewModel.moveExercise(index, index - 1) },
                                onMoveDown = { viewModel.moveExercise(index, index + 1) },
                                onRemove = { viewModel.removeExercise(routineExercise) }
                            )
                        }

                        // Add Exercise Button
                        item {
                            val canAdd = routine.id != 0
                            Button(
                                onClick = { 
                                    if (canAdd) navController.navigate(com.example.fitlog.presentation.navigation.FitLogRoutes.exercisePicker(routine.id))
                                },
                                enabled = canAdd,
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (canAdd) "Add Exercise" else "Save routine first to add exercises")
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
    isFirst: Boolean,
    isLast: Boolean,
    onUpdateTarget: (Int?, String?) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Name + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = routineExercise.exercise?.name ?: "Unknown Exercise",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                // Actions
                IconButton(onClick = onMoveUp, enabled = !isFirst) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                }
                IconButton(onClick = onMoveDown, enabled = !isLast) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                }
                IconButton(onClick = onRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Remove")
                }
            }
            
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
