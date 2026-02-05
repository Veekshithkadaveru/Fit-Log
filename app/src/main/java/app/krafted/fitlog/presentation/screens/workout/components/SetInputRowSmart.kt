package app.krafted.fitlog.presentation.screens.workout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.krafted.fitlog.domain.model.WorkoutSet

// PR Gold colors
private val PRGold = Color(0xFFFFD700)
private val PRGoldDark = Color(0xFFB8860B)

/**
 * Smart set input row with responsive sizing.
 * Uses weight-based layout and BoxWithConstraints for device-agnostic sizing.
 */
@Composable

fun SetInputRowSmart(
    setNumber: Int,
    workoutSet: WorkoutSet,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCompletedToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPR = workoutSet.isPR

    // Local state for inputs to prevent cursor jumping during async updates
    var weightInput by remember(workoutSet.weight) { 
        mutableStateOf(
            when {
                 workoutSet.weight == 0f -> "0"
                 workoutSet.weight % 1.0f == 0f -> workoutSet.weight.toInt().toString()
                 else -> workoutSet.weight.toString()
            }
        ) 
    }
    
    var repsInput by remember(workoutSet.reps) { 
        mutableStateOf(if (workoutSet.reps == 0) "" else workoutSet.reps.toString())
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        // Calculate responsive spacing based on available width
        val availableWidth = maxWidth
        val isCompact = availableWidth < 360.dp
        val horizontalSpacing = if (isCompact) 2.dp else 8.dp
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isPR) {
                        Modifier.background(
                            PRGold.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 4.dp, vertical = 4.dp), // Consistent padding
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Set number or PR badge
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.widthIn(min = 24.dp, max = 32.dp)
            ) {
                if (isPR) {
                    // PR Badge
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(PRGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Personal Record",
                            modifier = Modifier.size(14.dp),
                            tint = Color.White
                        )
                    }
                } else {
                    Text(
                        text = "$setNumber",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Weight input
            OutlinedTextField(
                value = weightInput,
                onValueChange = { 
                    weightInput = it
                    onWeightChange(it)
                },
                label = { Text("kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = if (isPR) {
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PRGold,
                        unfocusedBorderColor = PRGoldDark.copy(alpha = 0.5f)
                    )
                } else {
                    OutlinedTextFieldDefaults.colors()
                }
            )

            // Reps input
            OutlinedTextField(
                value = repsInput,
                onValueChange = { 
                    repsInput = it
                    onRepsChange(it) 
                },
                label = { Text("reps") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = if (isPR) {
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PRGold,
                        unfocusedBorderColor = PRGoldDark.copy(alpha = 0.5f)
                    )
                } else {
                    OutlinedTextFieldDefaults.colors()
                }
            )

            // Completed checkbox
            Checkbox(
                checked = workoutSet.isCompleted,
                onCheckedChange = { onCompletedToggle() },
                enabled = (workoutSet.reps > 0 || repsInput.isNotEmpty()), // Disable if no reps
                colors = if (isPR) {
                    CheckboxDefaults.colors(
                        checkedColor = PRGold,
                        checkmarkColor = Color.White
                    )
                } else {
                    CheckboxDefaults.colors()
                }
            )

            // Delete button
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(if (isCompact) 40.dp else 48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Set",
                    modifier = Modifier.size(if (isCompact) 20.dp else 24.dp),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
