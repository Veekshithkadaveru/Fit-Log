package app.krafted.fitlog.presentation.screens.workout.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.krafted.fitlog.domain.model.WorkoutSet
import app.krafted.fitlog.presentation.viewmodel.WorkoutExerciseWithDetails

/**
 * Component for displaying exercises in active workout with set logging
 */
@Composable
fun ExerciseSection(
    exercise: WorkoutExerciseWithDetails,
    sets: List<WorkoutSet>,
    onWeightChange: (setId: Int, weight: String) -> Unit,
    onRepsChange: (setId: Int, reps: String) -> Unit,
    onSetCompleted: (setId: Int) -> Unit,
    onDeleteSet: (setId: Int) -> Unit,
    onDuplicateSet: (setId: Int) -> Unit,
    onAddSet: () -> Unit,
    onAddSetWithAutoFill: () -> Unit,
    onExerciseClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Exercise Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.exercise.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${exercise.exercise.primaryMuscle.displayName} • ${exercise.exercise.equipment.displayName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Info button to view exercise details
                if (onExerciseClick != null) {
                    IconButton(onClick = onExerciseClick) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "View exercise details",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Target info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Target: ${exercise.targetSets} sets",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (exercise.targetReps.isNotEmpty()) {
                    Text(
                        text = "${exercise.targetReps} reps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Divider
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Set Input Grid
            SetInputGrid(
                sets = sets,
                onWeightChange = onWeightChange,
                onRepsChange = onRepsChange,
                onSetCompleted = onSetCompleted,
                onDeleteSet = onDeleteSet,
                onDuplicateSet = onDuplicateSet,
                
                onAddSet = onAddSet,
                onAddSetWithAutoFill = onAddSetWithAutoFill
            )
        }
    }
}
