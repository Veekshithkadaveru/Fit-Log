package app.krafted.fitlog.presentation.screens.exercises.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.krafted.fitlog.domain.model.Equipment
import app.krafted.fitlog.domain.model.ExerciseCategory
import app.krafted.fitlog.domain.model.MuscleGroup
import app.krafted.fitlog.presentation.theme.FitLogTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterRow(
    availableMuscleGroups: List<MuscleGroup>,
    availableEquipment: List<Equipment>,
    selectedMuscleGroup: MuscleGroup?,
    selectedEquipment: Equipment?,
    selectedCategory: ExerciseCategory?,
    onMuscleGroupSelected: (MuscleGroup?) -> Unit,
    onEquipmentSelected: (Equipment?) -> Unit,
    onCategorySelected: (ExerciseCategory?) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasActiveFilters = selectedMuscleGroup != null || selectedEquipment != null || selectedCategory != null

    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        // Header with clear button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            
            if (hasActiveFilters) {
                TextButton(onClick = onClearFilters) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("Clear All")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Muscle Groups
        if (availableMuscleGroups.isNotEmpty()) {
            Text(
                text = "Muscle Groups",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                availableMuscleGroups.forEach { muscleGroup ->
                    FilterChip(
                        selected = selectedMuscleGroup == muscleGroup,
                        onClick = {
                            onMuscleGroupSelected(
                                if (selectedMuscleGroup == muscleGroup) null else muscleGroup
                            )
                        },
                        label = { Text(muscleGroup.displayName) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Equipment
        if (availableEquipment.isNotEmpty()) {
            Text(
                text = "Equipment",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                availableEquipment.forEach { equipment ->
                    FilterChip(
                        selected = selectedEquipment == equipment,
                        onClick = {
                            onEquipmentSelected(
                                if (selectedEquipment == equipment) null else equipment
                            )
                        },
                        label = { Text(equipment.displayName) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Category
        Text(
            text = "Category",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ExerciseCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategorySelected(
                            if (selectedCategory == category) null else category
                        )
                    },
                    label = { Text(category.displayName) }
                )
            }
        }
    }
}

@Preview
@Composable
private fun FilterRowPreview() {
    FitLogTheme {
        FilterRow(
            availableMuscleGroups = listOf(MuscleGroup.CHEST, MuscleGroup.BACK, MuscleGroup.LEGS),
            availableEquipment = listOf(Equipment.BARBELL, Equipment.DUMBBELL, Equipment.CABLE),
            selectedMuscleGroup = MuscleGroup.CHEST,
            selectedEquipment = null,
            selectedCategory = null,
            onMuscleGroupSelected = {},
            onEquipmentSelected = {},
            onCategorySelected = {},
            onClearFilters = {}
        )
    }
}