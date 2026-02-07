package app.krafted.fitlog.presentation.screens.workout

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.krafted.fitlog.presentation.viewmodel.ActiveWorkoutViewModel

/**
 * Encapsulated actions for the Active Workout screen.
 * Groups callbacks to avoid exploding parameter lists and ensures stability.
 */
data class ActiveWorkoutActions(
    val onEndWorkout: () -> Unit,
    val onDiscardWorkout: () -> Unit,
    val onAddSet: (exerciseId: Int) -> Unit,
    val onAddSetWithAutoFill: (exerciseId: Int) -> Unit,
    val onUpdateSetWeight: (setId: Int, weight: String) -> Unit,
    val onUpdateSetReps: (setId: Int, reps: String) -> Unit,
    val onToggleSetCompleted: (setId: Int) -> Unit,
    val onDeleteSet: (setId: Int) -> Unit,
    val onDuplicateSet: (setId: Int) -> Unit,
    val onAddExercise: () -> Unit,
    val onExerciseClick: (exerciseId: Int) -> Unit
)

@Composable
fun rememberActiveWorkoutActions(
    viewModel: ActiveWorkoutViewModel,
    onNavigateToAddExercise: () -> Unit
): ActiveWorkoutActions {
    return remember(viewModel, onNavigateToAddExercise) {
        ActiveWorkoutActions(
            onEndWorkout = { viewModel.endWorkout() },
            onDiscardWorkout = { viewModel.discardWorkout() },
            onAddSet = { exerciseId -> viewModel.addSet(exerciseId, autoFill = false) },
            onAddSetWithAutoFill = { exerciseId -> viewModel.addSet(exerciseId, autoFill = true) },
            onUpdateSetWeight = { setId, weight ->
                viewModel.updateSet(setId, weight = weight.toFloatOrNull() ?: 0f)
            },
            onUpdateSetReps = { setId, reps ->
                viewModel.updateSet(setId, reps = reps.toIntOrNull() ?: 0)
            },
            onToggleSetCompleted = { setId -> viewModel.toggleSetCompleted(setId) },
            onDeleteSet = { setId -> viewModel.deleteSet(setId) },
            onDuplicateSet = { setId -> viewModel.duplicateSet(setId) },
            onAddExercise = onNavigateToAddExercise,
            onExerciseClick = { exerciseId -> viewModel.navigateToExerciseDetail(exerciseId) }
        )
    }
}
