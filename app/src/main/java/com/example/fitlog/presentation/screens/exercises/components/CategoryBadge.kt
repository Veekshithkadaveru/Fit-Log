package com.example.fitlog.presentation.screens.exercises.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.fitlog.domain.model.ExerciseCategory
import com.example.fitlog.ui.theme.FitLogTheme

@Composable
fun CategoryBadge(
    category: ExerciseCategory,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when (category) {
        ExerciseCategory.COMPOUND -> {
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        }
        ExerciseCategory.ISOLATION -> {
            MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        }
        ExerciseCategory.CARDIO -> {
            MaterialTheme.colorScheme.tertiary to MaterialTheme.colorScheme.onTertiary
        }
        ExerciseCategory.STRETCHING -> {
            MaterialTheme.colorScheme.outline to MaterialTheme.colorScheme.onSurface
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = category.displayName.take(4).uppercase(), // Show first 4 chars
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Preview
@Composable
private fun CategoryBadgePreview() {
    FitLogTheme {
        CategoryBadge(category = ExerciseCategory.COMPOUND)
    }
}