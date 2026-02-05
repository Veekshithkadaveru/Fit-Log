package app.krafted.fitlog.presentation.screens.exercises

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.krafted.fitlog.domain.model.Equipment
import app.krafted.fitlog.domain.model.Exercise
import app.krafted.fitlog.domain.model.ExerciseCategory
import app.krafted.fitlog.domain.model.MuscleGroup
import app.krafted.fitlog.presentation.screens.exercises.components.ExerciseCard
import app.krafted.fitlog.presentation.screens.exercises.components.ExerciseSearchBar
import app.krafted.fitlog.presentation.screens.exercises.components.FilterRow
import app.krafted.fitlog.presentation.theme.FitLogTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseLibraryScreen(
    onExerciseClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExerciseLibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showFilters by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Exercise Library",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.exercises.size} exercises",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleSearch() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search exercises"
                        )
                    }
                    IconButton(
                        onClick = { showFilters = !showFilters }
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter exercises"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            AnimatedVisibility(
                visible = uiState.isSearchActive,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))
                    ExerciseSearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::updateSearchQuery,
                        onClear = { viewModel.updateSearchQuery("") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Filters
            AnimatedVisibility(
                visible = showFilters,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    if (!uiState.isSearchActive) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    FilterRow(
                        availableMuscleGroups = uiState.availableMuscleGroups,
                        availableEquipment = uiState.availableEquipment,
                        selectedMuscleGroup = uiState.selectedMuscleGroup,
                        selectedEquipment = uiState.selectedEquipment,
                        selectedCategory = uiState.selectedCategory,
                        onMuscleGroupSelected = viewModel::selectMuscleGroup,
                        onEquipmentSelected = viewModel::selectEquipment,
                        onCategorySelected = viewModel::selectCategory,
                        onClearFilters = viewModel::clearAllFilters
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Exercise list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.exercises.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (uiState.searchQuery.isNotEmpty()) {
                                "No exercises found for \"${uiState.searchQuery}\""
                            } else {
                                "No exercises match the selected filters"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (uiState.selectedMuscleGroup != null || uiState.selectedEquipment != null || uiState.selectedCategory != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try clearing some filters",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.exercises) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = { onExerciseClick(exercise.id) }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ExerciseLibraryScreenPreview() {
    FitLogTheme {
        // Note: This won't work in preview because it needs Hilt
        // ExerciseLibraryScreen(onExerciseClick = {})
    }
}