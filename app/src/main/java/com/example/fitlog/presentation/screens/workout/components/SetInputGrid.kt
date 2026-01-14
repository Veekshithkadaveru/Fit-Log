package com.example.fitlog.presentation.screens.workout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitlog.domain.model.WorkoutSet

/**
 * Grid of set inputs for a single exercise
 */
@Composable
fun SetInputGrid(
    sets: List<WorkoutSet>,
    onWeightChange: (setId: Int, weight: String) -> Unit,
    onRepsChange: (setId: Int, reps: String) -> Unit,
    onSetCompleted: (setId: Int) -> Unit,
    onDeleteSet: (setId: Int) -> Unit,
    onAddSet: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Header
        if (sets.isNotEmpty()) {
            Text(
                text = "Sets",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Set Rows
        sets.forEachIndexed { index, set ->
            SetInputRow(
                setNumber = index + 1,
                workoutSet = set,
                onWeightChange = { weight -> onWeightChange(set.id, weight) },
                onRepsChange = { reps -> onRepsChange(set.id, reps) },
                onCompletedToggle = { onSetCompleted(set.id) },
                onDelete = { onDeleteSet(set.id) }
            )
        }

        // Add Set Button
        Button(
            onClick = onAddSet,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null
            )
            Text(
                text = if (sets.isEmpty()) "Add First Set" else "Add Set",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
