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
import app.krafted.fitlog.presentation.screens.workout.WorkoutConstants

/**
 * Single row for inputting set data (weight, reps, completion).
 * Features:
 * - Local state management for smooth typing (debounces upstream updates).
 * - Responsive layout using BoxWithConstraints.
 * - Centralized constants for styling.
 * - Accessibility support (min touch targets).
 */
@Composable
fun SetInputRow(
    setNumber: Int,
    workoutSet: WorkoutSet,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCompletedToggle: () -> Unit,
    onDelete: () -> Unit,
    weightUnitLabel: String = "kg",
    modifier: Modifier = Modifier
) {
    val isPR = workoutSet.isPR
    val prGoldColor = WorkoutConstants.PR_GOLD
    val prGoldDarkColor = WorkoutConstants.PR_GOLD_DARK

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
        val availableWidth = maxWidth
        val isCompact = availableWidth < 360.dp
        val horizontalSpacing = if (isCompact) 2.dp else 8.dp
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (isPR) {
                        Modifier.background(
                            prGoldColor.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        )
                    } else {
                        Modifier
                    }
                )
                .padding(
                    horizontal = WorkoutConstants.INPUT_ROW_PADDING_HORIZONTAL, 
                    vertical = WorkoutConstants.INPUT_ROW_PADDING_VERTICAL
                ),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.widthIn(min = 24.dp, max = 32.dp)
            ) {
                if (isPR) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(prGoldColor),
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

            OutlinedTextField(
                value = weightInput,
                onValueChange = { 
                    weightInput = it
                    onWeightChange(it)
                },
                label = { Text(weightUnitLabel) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = MaterialTheme.typography.bodyMedium,
                colors = if (isPR) {
                    OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = prGoldColor,
                        unfocusedBorderColor = prGoldDarkColor.copy(alpha = 0.5f)
                    )
                } else {
                    OutlinedTextFieldDefaults.colors()
                }
            )

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
                        focusedBorderColor = prGoldColor,
                        unfocusedBorderColor = prGoldDarkColor.copy(alpha = 0.5f)
                    )
                } else {
                    OutlinedTextFieldDefaults.colors()
                }
            )

            Checkbox(
                checked = workoutSet.isCompleted,
                onCheckedChange = { onCompletedToggle() },
                enabled = (workoutSet.reps > 0 || repsInput.isNotEmpty()), // Disable if no reps
                colors = if (isPR) {
                    CheckboxDefaults.colors(
                        checkedColor = prGoldColor,
                        checkmarkColor = Color.White
                    )
                } else {
                    CheckboxDefaults.colors()
                }
            )

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(WorkoutConstants.TOUCH_TARGET_MIN)
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
