package com.example.fitlog.presentation.screens.workout.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.domain.model.MuscleGroup
import com.example.fitlog.presentation.viewmodel.WorkoutExerciseWithDetails

/**
 * Panel showing a summary of muscles trained in the current workout
 * Displays muscles color-coded by intensity/focus
 */
@Composable
fun MusclesTrainedPanel(
    exercises: List<WorkoutExerciseWithDetails>,
    modifier: Modifier = Modifier
) {
    val muscleStats = remember(exercises) {
        calculateMuscleStats(exercises)
    }

    if (muscleStats.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = "Muscles Trained Today",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(muscleStats) { stats ->
                        MuscleTag(
                            muscleName = stats.muscle.displayName,
                            isPrimary = stats.isPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MuscleTag(
    muscleName: String,
    isPrimary: Boolean
) {
    Surface(
        color = if (isPrimary) 
            MaterialTheme.colorScheme.primaryContainer 
        else 
            MaterialTheme.colorScheme.secondaryContainer,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(28.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            // Indicator dot
            if (isPrimary) {
                StartDot(color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
            }
            
            Text(
                text = muscleName,
                style = MaterialTheme.typography.labelMedium,
                color = if (isPrimary) 
                    MaterialTheme.colorScheme.onPrimaryContainer 
                else 
                    MaterialTheme.colorScheme.onSecondaryContainer,
                fontWeight = if (isPrimary) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

@Composable
private fun StartDot(color: Color) {
    Text(
        text = "•",
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(bottom = 2.dp) // Optical alignment
    )
}

private data class MuscleStats(
    val muscle: MuscleGroup,
    val score: Float
) {
    val isPrimary: Boolean
        get() = score >= 1.0f
}

private fun calculateMuscleStats(exercises: List<WorkoutExerciseWithDetails>): List<MuscleStats> {
    val scores = mutableMapOf<MuscleGroup, Float>()
    
    exercises.forEach { details ->
        // Only count if sets are performed (e.g. at least 1 set) or it's in the plan?
        // Let's count all added exercises for now to show intent
        
        // Primary muscle gets 1.0
        val primary = details.exercise.primaryMuscle
        scores[primary] = (scores[primary] ?: 0f) + 1.0f
        
        // Secondary muscles get 0.5
        details.exercise.secondaryMuscles.forEach { secondary ->
            scores[secondary] = (scores[secondary] ?: 0f) + 0.5f
        }
    }
    
    // Sort by score descending and take top 5 to avoid overcrowding
    return scores.entries
        .sortedByDescending { it.value }
        .take(6)
        .map { MuscleStats(it.key, it.value) }
}
