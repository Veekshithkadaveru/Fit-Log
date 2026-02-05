package app.krafted.fitlog.presentation.screens.workout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.krafted.fitlog.domain.model.WorkoutSet

/**
 * Grid of set inputs for a single exercise with smart features
 */
@Composable
fun SetInputGrid(
    sets: List<WorkoutSet>,
    onWeightChange: (setId: Int, weight: String) -> Unit,
    onRepsChange: (setId: Int, reps: String) -> Unit,
    onSetCompleted: (setId: Int) -> Unit,
    onDeleteSet: (setId: Int) -> Unit,
    onDuplicateSet: (setId: Int) -> Unit,
    onAddSet: () -> Unit,
    onAddSetWithAutoFill: () -> Unit,
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
            SetInputRowSmart(
                setNumber = index + 1,
                workoutSet = set,
                onWeightChange = { weight -> onWeightChange(set.id, weight) },
                onRepsChange = { reps -> onRepsChange(set.id, reps) },
                onCompletedToggle = { onSetCompleted(set.id) },
                onDelete = { onDeleteSet(set.id) }
            )
        }

        // Add Set Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Regular Add Set
            Button(
                onClick = onAddSet,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
                Text(
                    text = if (sets.isEmpty()) "Add Set" else "Add",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Auto-Fill Add Set
            if (sets.isNotEmpty()) {
                OutlinedButton(
                    onClick = onAddSetWithAutoFill,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null
                    )
                    Text(
                        text = "Auto-Fill",
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }
    }
}
