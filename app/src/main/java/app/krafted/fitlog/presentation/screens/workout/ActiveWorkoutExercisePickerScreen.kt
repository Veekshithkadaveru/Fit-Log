package app.krafted.fitlog.presentation.screens.workout


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.krafted.fitlog.domain.model.Exercise
import app.krafted.fitlog.presentation.navigation.FitLogDestinations
import app.krafted.fitlog.presentation.viewmodel.ActiveWorkoutViewModel
import app.krafted.fitlog.presentation.viewmodel.ExerciseViewModel

/**
 * Exercise picker specifically for adding exercises to an active workout
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutExercisePickerScreen(
    navController: NavController,
    exerciseViewModel: ExerciseViewModel = hiltViewModel(),
    workoutViewModel: ActiveWorkoutViewModel = hiltViewModel(
        navController.getBackStackEntry(FitLogDestinations.WORKOUT)
    )
) {
    val exerciseState by exerciseViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Exercise") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                value = exerciseState.searchQuery,
                onValueChange = { exerciseViewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search exercises") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )

            // Muscle Filters
            LazyRow(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val muscles = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Abs")
                items(muscles) { muscle ->
                    FilterChip(
                        selected = exerciseState.selectedMuscle == if (muscle == "All") null else muscle,
                        onClick = { exerciseViewModel.updateMuscleFilter(if (muscle == "All") null else muscle) },
                        label = { Text(muscle) }
                    )
                }
            }

            if (exerciseState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )
            } else {
                LazyColumn(
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exerciseState.exercises) { exercise ->
                        WorkoutExercisePickerItem(
                            exercise = exercise,
                            onAdd = {
                                workoutViewModel.addExerciseToWorkout(exercise.id)
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single exercise item in the picker for active workout
 */
@Composable
fun WorkoutExercisePickerItem(
    exercise: Exercise,
    onAdd: () -> Unit
) {
    Card(
        onClick = onAdd,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = exercise.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${exercise.primaryMuscle.displayName} • ${exercise.equipment.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
