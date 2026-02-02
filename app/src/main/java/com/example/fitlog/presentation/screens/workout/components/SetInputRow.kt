package com.example.fitlog.presentation.screens.workout.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.domain.model.WorkoutSet
import com.example.fitlog.ui.theme.FitLogColors

/**
 * Single row for inputting set data (weight, reps, completion)
 * Shows PR badge when the set is a personal record
 */
@Composable
fun SetInputRow(
    setNumber: Int,
    workoutSet: WorkoutSet,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    onCompletedToggle: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPR = workoutSet.isPR
    val borderColor = if (isPR) FitLogColors.prGold else MaterialTheme.colorScheme.outline

    Row(
        modifier = modifier
            .padding(vertical = 4.dp)
            .then(
                if (isPR) {
                    Modifier.background(
                        FitLogColors.prGold.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    )
                } else {
                    Modifier
                }
            )
            .padding(horizontal = if (isPR) 4.dp else 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set Number with PR indicator
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.width(32.dp)
        ) {
            if (isPR) {
                // PR Badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(FitLogColors.prGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Personal Record",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            } else {
                Text(
                    text = "$setNumber",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Weight Input
        OutlinedTextField(
            value = if (workoutSet.weight == 0f) "" else workoutSet.weight.toString(),
            onValueChange = onWeightChange,
            label = { Text("kg") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = if (isPR) {
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FitLogColors.prGold,
                    unfocusedBorderColor = FitLogColors.prGoldDark.copy(alpha = 0.5f)
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            }
        )

        // Reps Input
        OutlinedTextField(
            value = if (workoutSet.reps == 0) "" else workoutSet.reps.toString(),
            onValueChange = onRepsChange,
            label = { Text("reps") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.weight(1f),
            colors = if (isPR) {
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FitLogColors.prGold,
                    unfocusedBorderColor = FitLogColors.prGoldDark.copy(alpha = 0.5f)
                )
            } else {
                OutlinedTextFieldDefaults.colors()
            }
        )

        // Completion Checkbox
        Checkbox(
            checked = workoutSet.isCompleted,
            onCheckedChange = { onCompletedToggle() },
            colors = if (isPR) {
                CheckboxDefaults.colors(
                    checkedColor = FitLogColors.prGold,
                    checkmarkColor = Color.White
                )
            } else {
                CheckboxDefaults.colors()
            }
        )

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
