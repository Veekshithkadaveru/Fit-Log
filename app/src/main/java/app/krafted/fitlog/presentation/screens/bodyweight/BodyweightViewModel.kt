package app.krafted.fitlog.presentation.screens.bodyweight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fitlog.domain.model.Bodyweight
import app.krafted.fitlog.domain.repository.BodyweightRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Bodyweight tracking screen
 */
data class BodyweightUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val weightInput: String = "",
    val notes: String = "",
    val latestWeight: Float? = null,
    val weightChange: Float? = null,
    val entries: List<Bodyweight> = emptyList(),
    val todayEntry: Bodyweight? = null,
    val error: String? = null
)

/**
 * One-time events from the ViewModel
 */
sealed class BodyweightEvent {
    data object SaveSuccess : BodyweightEvent()
    data class SaveError(val message: String) : BodyweightEvent()
}

/**
 * ViewModel for the Bodyweight tracking screen
 */
@HiltViewModel
class BodyweightViewModel @Inject constructor(
    private val bodyweightRepository: BodyweightRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BodyweightUiState())
    val uiState: StateFlow<BodyweightUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<BodyweightEvent>()
    val events: SharedFlow<BodyweightEvent> = _events.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)


            bodyweightRepository.getAllEntries()
                .catch { e ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
                .collect { entries ->
                    val sortedEntries = entries.sortedByDescending { it.date }
                    val latestWeight = sortedEntries.firstOrNull()?.weight

                    // Calculate weight change from first to latest entry
                    val weightChange = if (sortedEntries.size >= 2) {
                        val oldest = sortedEntries.last().weight
                        val newest = sortedEntries.first().weight
                        newest - oldest
                    } else null


                    val todayStart = getTodayStartMillis()
                    val todayEnd = todayStart + 24 * 60 * 60 * 1000
                    val todayEntry = sortedEntries.find { it.date in todayStart until todayEnd }

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        entries = sortedEntries,
                        latestWeight = latestWeight,
                        weightChange = weightChange,
                        todayEntry = todayEntry,

                        weightInput = todayEntry?.weight?.toString() ?: "",
                        notes = todayEntry?.notes ?: ""
                    )
                }
        }
    }

    fun onWeightInputChanged(input: String) {

        if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.value = _uiState.value.copy(weightInput = input)
        }
    }

    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Save or update today's bodyweight entry
     */
    fun saveWeight() {
        val weightValue = _uiState.value.weightInput.toFloatOrNull()
        if (weightValue == null || weightValue <= 0) {
            viewModelScope.launch {
                _events.emit(BodyweightEvent.SaveError("Please enter a valid weight"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            try {
                val todayEntry = _uiState.value.todayEntry
                val currentTime = System.currentTimeMillis()

                if (todayEntry != null) {

                    val updatedEntry = todayEntry.copy(
                        weight = weightValue,
                        notes = _uiState.value.notes.takeIf { it.isNotBlank() }
                    )
                    bodyweightRepository.update(updatedEntry)
                } else {

                    val newEntry = Bodyweight(
                        date = currentTime,
                        weight = weightValue,
                        notes = _uiState.value.notes.takeIf { it.isNotBlank() }
                    )
                    bodyweightRepository.insert(newEntry)
                }

                _uiState.value = _uiState.value.copy(isSaving = false)
                _events.emit(BodyweightEvent.SaveSuccess)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save weight"
                )
                _events.emit(BodyweightEvent.SaveError(e.message ?: "Failed to save weight"))
            }
        }
    }

    /**
     * Delete a bodyweight entry
     */
    fun deleteEntry(entry: Bodyweight) {
        viewModelScope.launch {
            try {
                bodyweightRepository.delete(entry)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    /**
     * Refresh data
     */
    fun refresh() {
        loadData()
    }

    private fun getTodayStartMillis(): Long {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        return (now / dayMillis) * dayMillis
    }
}
