package com.example.fitlog.presentation.screens.exercises.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitlog.domain.model.Equipment
import com.example.fitlog.domain.model.Exercise
import com.example.fitlog.domain.model.ExerciseCategory
import com.example.fitlog.domain.model.MuscleGroup
import com.example.fitlog.presentation.components.ExerciseImage
import com.example.fitlog.ui.theme.CardDimens
import com.example.fitlog.ui.theme.FitLogCardDefaults
import com.example.fitlog.ui.theme.FitLogShapes
import com.example.fitlog.ui.theme.FitLogTheme
import com.example.fitlog.ui.theme.Spacing

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = FitLogShapes.card,
        elevation = FitLogCardDefaults.elevation(),
        colors = FitLogCardDefaults.colors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(CardDimens.paddingCompact),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Exercise thumbnail/icon
            ExerciseImage(
                thumbnailRes = exercise.thumbnailRes,
                contentDescription = exercise.name,
                modifier = Modifier
                    .size(48.dp)
                    .clip(FitLogShapes.small),
                fallbackIconSize = 32.dp
            )

            Spacer(modifier = Modifier.width(Spacing.md))

            // Exercise details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(Spacing.xs))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MuscleGroupChip(
                        muscleGroup = exercise.primaryMuscle,
                        isPrimary = true
                    )

                    Text(
                        text = exercise.equipment.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (exercise.secondaryMuscles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(Spacing.xs))
                    Text(
                        text = "Secondary: ${exercise.secondaryMuscles.joinToString { it.displayName }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Category badge
            CategoryBadge(category = exercise.category)
        }
    }
}

@Preview
@Composable
private fun ExerciseCardPreview() {
    FitLogTheme {
        ExerciseCard(
            exercise = Exercise(
                id = 1,
                name = "Bench Press",
                primaryMuscle = MuscleGroup.CHEST,
                secondaryMuscles = listOf(MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS),
                category = ExerciseCategory.COMPOUND,
                equipment = Equipment.BARBELL
            ),
            onClick = {}
        )
    }
}
