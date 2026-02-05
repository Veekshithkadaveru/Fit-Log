package app.krafted.fitlog.presentation.screens.cardio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fitlog.domain.model.CardioSession
import app.krafted.fitlog.domain.model.CardioType
import app.krafted.fitlog.domain.model.Workout
import app.krafted.fitlog.domain.repository.CardioRepository
import app.krafted.fitlog.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * UI state for the Cardio logging screen
 */
data class CardioUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val selectedCardioType: CardioType = CardioType.RUNNING,
    val durationMinutes: Int = 30,
    val distance: String = "",
    val rpe: Int = 5,
    val notes: String = "",
    val error: String? = null,
    val recentSessions: List<CardioSessionWithDate> = emptyList()
)

/**
 * Wrapper to include workout date with cardio session
 */
data class CardioSessionWithDate(
    val session: CardioSession,
    val workoutDate: Long
)

/**
 * One-time events from the ViewModel
 */
sealed class CardioEvent {
    data object SaveSuccess : CardioEvent()
    data class SaveError(val message: String) : CardioEvent()
}

/**
 * ViewModel for the Cardio logging screen
 */
@HiltViewModel
class CardioViewModel @Inject constructor(
    private val cardioRepository: CardioRepository,
    private val workoutRepository: WorkoutRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CardioUiState())
    val uiState: StateFlow<CardioUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CardioEvent>()
    val events: SharedFlow<CardioEvent> = _events.asSharedFlow()

    init {
        loadRecentSessions()
    }

    private fun loadRecentSessions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Get recent cardio sessions from the last 30 days
                val thirtyDaysAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)
                val sessions = cardioRepository.getCardioSessionsByDateRange(
                    startDate = thirtyDaysAgo,
                    endDate = System.currentTimeMillis()
                )

                // Get workout dates for each session
                val sessionsWithDates = sessions.mapNotNull { session ->
                    val workout = workoutRepository.getWorkoutById(session.workoutId)
                    workout?.let {
                        CardioSessionWithDate(session, it.startTime)
                    }
                }.take(10) // Show last 10 sessions

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recentSessions = sessionsWithDates
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onCardioTypeSelected(type: CardioType) {
        _uiState.value = _uiState.value.copy(selectedCardioType = type)
    }

    fun onDurationChanged(minutes: Int) {
        _uiState.value = _uiState.value.copy(durationMinutes = minutes.coerceIn(1, 300))
    }

    fun onDistanceChanged(distance: String) {
        // Only allow valid decimal numbers
        if (distance.isEmpty() || distance.matches(Regex("^\\d*\\.?\\d*$"))) {
            _uiState.value = _uiState.value.copy(distance = distance)
        }
    }

    fun onRpeChanged(rpe: Int) {
        _uiState.value = _uiState.value.copy(rpe = rpe.coerceIn(1, 10))
    }

    fun onNotesChanged(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Save the cardio session by creating a new workout and adding the cardio session to it
     */
    fun saveCardioSession() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, error = null)

            try {
                val state = _uiState.value
                val currentTime = System.currentTimeMillis()
                val durationMs = state.durationMinutes * 60 * 1000L

                // Create a new workout for this cardio session
                val workout = Workout(
                    startTime = currentTime - durationMs,
                    endTime = currentTime,
                    isCardio = true,
                    notes = state.notes.takeIf { it.isNotBlank() }
                )

                val workoutId = workoutRepository.insertWorkout(workout)

                // Create the cardio session
                val cardioSession = CardioSession(
                    workoutId = workoutId.toInt(),
                    type = state.selectedCardioType,
                    durationMinutes = state.durationMinutes,
                    distance = state.distance.toFloatOrNull(),
                    rpe = state.rpe
                )

                cardioRepository.insert(cardioSession)

                _uiState.value = _uiState.value.copy(isSaving = false)
                _events.emit(CardioEvent.SaveSuccess)

                // Reset form after successful save
                resetForm()

                // Reload recent sessions
                loadRecentSessions()

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    error = e.message ?: "Failed to save cardio session"
                )
                _events.emit(CardioEvent.SaveError(e.message ?: "Failed to save cardio session"))
            }
        }
    }

    private fun resetForm() {
        _uiState.value = _uiState.value.copy(
            selectedCardioType = CardioType.RUNNING,
            durationMinutes = 30,
            distance = "",
            rpe = 5,
            notes = ""
        )
    }

    /**
     * Quick log a cardio session with the same type and duration as a previous session
     */
    fun quickLogSession(session: CardioSession) {
        _uiState.value = _uiState.value.copy(
            selectedCardioType = session.type,
            durationMinutes = session.durationMinutes,
            distance = session.distance?.toString() ?: "",
            rpe = session.rpe ?: 5
        )
    }
}
