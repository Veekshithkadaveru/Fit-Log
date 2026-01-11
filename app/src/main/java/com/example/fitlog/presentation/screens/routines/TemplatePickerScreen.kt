package com.example.fitlog.presentation.screens.routines

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.fitlog.domain.model.Routine
import com.example.fitlog.presentation.navigation.FitLogRoutes
import com.example.fitlog.presentation.screens.routines.components.RoutineItem
import com.example.fitlog.presentation.viewmodel.RoutineNavigationEvent
import com.example.fitlog.presentation.viewmodel.RoutineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplatePickerScreen(
    navController: NavController,
    onTemplateClick: (Int) -> Unit, // Kept for signature compatibility but we'll use internal logic
    viewModel: RoutineViewModel = hiltViewModel()
) {
    val templates by viewModel.templates.collectAsState()
    val groupedTemplates by viewModel.groupedTemplates.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Handle One-time Navigation Events
    LaunchedEffect(true) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is RoutineNavigationEvent.NavigateToEditor -> {
                    // Navigate to editor with new routine ID, replace current screen to avoid back stacking
                    navController.navigate(FitLogRoutes.routineEditor(event.routineId)) {
                        popUpTo(FitLogRoutes.routineEditor(0)) { inclusive = true } // Optional cleanup
                    }
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Routine") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Option 1: Start from Scratch
                    item {
                        StartFromScratchCard(
                            onClick = {
                                // Navigate to Editor with ID 0 (New Routine)
                                navController.navigate(FitLogRoutes.routineEditor(0))
                            }
                        )
                    }

                    item {
                        Text(
                            text = "Or choose a template",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    // Option 2: Templates
                    
                    items(groupedTemplates.keys.toList()) { groupName ->
                        val routines = groupedTemplates[groupName] ?: emptyList()
                        TemplateGroupItem(
                            groupName = groupName,
                            routines = routines,
                            onRoutineClick = { template ->
                                viewModel.createRoutineFromTemplate(template)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TemplateGroupItem(
    groupName: String,
    routines: List<Routine>,
    onRoutineClick: (Routine) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = groupName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand"
                )
            }

            // Expanded List
            if (expanded) {
                Column(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    routines.forEach { routine ->
                        // Strip the program name prefix for cleaner display
                        // e.g. "Push/Pull/Legs - Day 1: Push" -> "Day 1: Push"
                        val displayName = if (routine.name.startsWith("$groupName - ")) {
                            routine.name.removePrefix("$groupName - ")
                        } else {
                            routine.name
                        }
                        
                        // Using a simpler item layout for nested items or reusing RoutineItem with modified name
                        // Reusing RoutineItem but passing a copy with the display name
                        val displayRoutine = routine.copy(name = displayName)
                        
                        RoutineItem(
                            routine = displayRoutine,
                            onClick = { onRoutineClick(routine) }, // Pass original routine to logic
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StartFromScratchCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Start from Scratch",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Create a new empty routine",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
