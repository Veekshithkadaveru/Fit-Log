package com.example.fitlog.presentation.screens.workout.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Bottom controls for workout screen with End/Discard actions
 */
@Composable
fun WorkoutControls(
    onEndWorkout: () -> Unit,
    onDiscardWorkout: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEndConfirmation by remember { mutableStateOf(false) }
    var showDiscardConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // End Workout Button (Primary)
        Button(
            onClick = { showEndConfirmation = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null
            )
            Text(
                text = "End Workout",
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Discard Workout Button (Destructive)
        OutlinedButton(
            onClick = { showDiscardConfirmation = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
            Text(
                text = "Discard Workout",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    // End Workout Confirmation Dialog
    if (showEndConfirmation) {
        AlertDialog(
            onDismissRequest = { showEndConfirmation = false },
            title = { Text("End Workout?") },
            text = { Text("This will save your workout and mark it as complete.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showEndConfirmation = false
                        onEndWorkout()
                    }
                ) {
                    Text("End Workout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Discard Workout Confirmation Dialog
    if (showDiscardConfirmation) {
        AlertDialog(
            onDismissRequest = { showDiscardConfirmation = false },
            title = { Text("Discard Workout?") },
            text = { Text("This will delete all workout data. This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardConfirmation = false
                        onDiscardWorkout()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardConfirmation = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
