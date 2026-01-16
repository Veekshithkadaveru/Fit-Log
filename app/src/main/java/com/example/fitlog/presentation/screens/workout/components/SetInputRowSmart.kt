package com.example.fitlog.presentation.screens.workout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.fitlog.domain.model.WorkoutSet

/**
 * Smart set input row with duplicate button
 */
@Composable
fun SetInputRowSmart(
    setNumber: Int,
    workoutSet: WorkoutSet,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCompletedToggle: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number
        Text(
            text = "$setNumber",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(24.dp)
        )

        // Weight Input
        OutlinedTextField(
            value = if (workoutSet.weight == 0f) "" else workoutSet.weight.toString(),
            onValueChange = onWeightChange,
            label = { Text("lbs") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        // Reps Input
        OutlinedTextField(
            value = if (workoutSet.reps == 0) "" else workoutSet.reps.toString(),
            onValueChange = onRepsChange,
            label = { Text("reps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )

        // Completion Checkbox
        Checkbox(
            checked = workoutSet.isCompleted,
            onCheckedChange = { onCompletedToggle() }
        )

        // Duplicate Button
        IconButton(onClick = onDuplicate) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Duplicate Set",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Delete Button
        IconButton(onClick = onDelete) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Set",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}
