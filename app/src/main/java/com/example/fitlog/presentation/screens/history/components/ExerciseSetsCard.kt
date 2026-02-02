package com.example.fitlog.presentation.screens.history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.fitlog.domain.model.WorkoutSet
import com.example.fitlog.presentation.screens.history.ExerciseWithSets

/**
 * Card component displaying an exercise with all its sets from a workout
 * Supports both read-only and edit modes
 */
@Composable
fun ExerciseSetsCard(
    exerciseWithSets: ExerciseWithSets,
    modifier: Modifier = Modifier,
    isEditMode: Boolean = false,
    getWeight: ((Int) -> Float)? = null,
    getReps: ((Int) -> Int)? = null,
    onWeightChange: ((Int, Float) -> Unit)? = null,
    onRepsChange: ((Int, Int) -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ExerciseHeader(exerciseWithSets = exerciseWithSets)

            Spacer(modifier = Modifier.height(12.dp))

            SetsTableHeader(isEditMode = isEditMode)

            Spacer(modifier = Modifier.height(8.dp))

            exerciseWithSets.sets.forEachIndexed { index, set ->
                if (isEditMode) {
                    EditableSetRow(
                        setNumber = index + 1,
                        set = set,
                        weight = getWeight?.invoke(set.id) ?: set.weight,
                        reps = getReps?.invoke(set.id) ?: set.reps,
                        onWeightChange = { onWeightChange?.invoke(set.id, it) },
                        onRepsChange = { onRepsChange?.invoke(set.id, it) }
                    )
                } else {
                    SetRow(
                        setNumber = index + 1,
                        set = set
                    )
                }
                if (index < exerciseWithSets.sets.size - 1) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            if (exerciseWithSets.sets.size > 1 && !isEditMode) {
                Spacer(modifier = Modifier.height(12.dp))
                ExerciseSummary(exerciseWithSets = exerciseWithSets)
            }
        }
    }
}

@Composable
private fun ExerciseHeader(
    exerciseWithSets: ExerciseWithSets,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = exerciseWithSets.exercise.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${exerciseWithSets.exercise.primaryMuscle.displayName} • ${exerciseWithSets.exercise.equipment.displayName}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (exerciseWithSets.hasPR) {
            PRBadge(prCount = exerciseWithSets.prCount)
        }
    }
}

@Composable
private fun PRBadge(
    prCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (prCount > 1) "$prCount PRs" else "PR",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SetsTableHeader(
    isEditMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Set",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(36.dp)
        )
        Text(
            text = "Weight",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(if (isEditMode) 80.dp else 72.dp)
        )
        Text(
            text = "Reps",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(if (isEditMode) 60.dp else 48.dp)
        )
        if (!isEditMode) {
            Text(
                text = "Volume",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(72.dp)
            )
            Spacer(modifier = Modifier.width(32.dp))
        }
    }
}

@Composable
private fun SetRow(
    setNumber: Int,
    set: WorkoutSet,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        set.isPR -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        set.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (set.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$setNumber",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (set.isCompleted) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        Text(
            text = formatWeight(set.weight),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(72.dp)
        )

        Text(
            text = "${set.reps}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(48.dp)
        )

        Text(
            text = formatVolume(set.volume),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(72.dp)
        )

        Box(
            modifier = Modifier.width(32.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                set.isPR -> {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Personal Record",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                set.isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Default.RadioButtonUnchecked,
                        contentDescription = "Not Completed",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}

@Composable
private fun EditableSetRow(
    setNumber: Int,
    set: WorkoutSet,
    weight: Float,
    reps: Int,
    onWeightChange: (Float) -> Unit,
    onRepsChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        set.isPR -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        set.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Set number badge
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(
                    if (set.isCompleted) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outline
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$setNumber",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (set.isCompleted) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }

        // Editable weight field
        EditableNumberField(
            value = weight,
            onValueChange = onWeightChange,
            suffix = "kg",
            modifier = Modifier.width(80.dp)
        )

        // Editable reps field
        EditableIntField(
            value = reps,
            onValueChange = onRepsChange,
            modifier = Modifier.width(60.dp)
        )
    }
}

@Composable
private fun EditableNumberField(
    value: Float,
    onValueChange: (Float) -> Unit,
    suffix: String,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) {
        mutableStateOf(
            if (value == value.toInt().toFloat()) {
                value.toInt().toString()
            } else {
                String.format("%.1f", value)
            }
        )
    }

    OutlinedTextField(
        value = textValue,
        onValueChange = { newValue ->
            textValue = newValue
            val parsed = newValue.toFloatOrNull()
            if (parsed != null && parsed >= 0) {
                onValueChange(parsed)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.End
        ),
        suffix = {
            Text(
                text = suffix,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun EditableIntField(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var textValue by remember(value) { mutableStateOf(value.toString()) }

    OutlinedTextField(
        value = textValue,
        onValueChange = { newValue ->
            textValue = newValue
            val parsed = newValue.toIntOrNull()
            if (parsed != null && parsed >= 0) {
                onValueChange(parsed)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        ),
        modifier = modifier,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
private fun ExerciseSummary(
    exerciseWithSets: ExerciseWithSets,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(
                label = "Sets",
                value = "${exerciseWithSets.completedSets}/${exerciseWithSets.sets.size}"
            )
            SummaryItem(
                label = "Total Volume",
                value = formatVolume(exerciseWithSets.totalVolume)
            )
            if (exerciseWithSets.sets.isNotEmpty()) {
                val maxWeight = exerciseWithSets.sets.maxOf { it.weight }
                SummaryItem(
                    label = "Max Weight",
                    value = formatWeight(maxWeight)
                )
            }
        }
    }
}

@Composable
private fun SummaryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Utility functions

private fun formatWeight(weight: Float): String {
    return if (weight == weight.toInt().toFloat()) {
        "${weight.toInt()} kg"
    } else {
        String.format("%.1f kg", weight)
    }
}

private fun formatVolume(volume: Float): String {
    return when {
        volume >= 10000 -> String.format("%.1fk", volume / 1000)
        volume >= 1000 -> String.format("%,.0f", volume)
        volume == volume.toInt().toFloat() -> "${volume.toInt()}"
        else -> String.format("%.0f", volume)
    }
}
