package app.krafted.fitlog.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fitlog.domain.model.ThemeMode
import app.krafted.fitlog.domain.model.WeightUnit
import app.krafted.fitlog.domain.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val weightUnit: WeightUnit = WeightUnit.KG,
    val restTimerSeconds: Int = 90,
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeThemeMode()
        observeWeightUnit()
        observeRestTimerDuration()
    }

    private fun observeThemeMode() {
        viewModelScope.launch {
            userPreferencesRepository.themeMode
                .catch { /* keep current state on error */ }
                .collect { themeMode ->
                    _uiState.value = _uiState.value.copy(
                        themeMode = themeMode,
                        isLoading = false
                    )
                }
        }
    }

    private fun observeWeightUnit() {
        viewModelScope.launch {
            userPreferencesRepository.weightUnit
                .catch { /* keep current state on error */ }
                .collect { weightUnit ->
                    _uiState.value = _uiState.value.copy(weightUnit = weightUnit)
                }
        }
    }

    private fun observeRestTimerDuration() {
        viewModelScope.launch {
            userPreferencesRepository.restTimerDuration
                .catch { /* keep current state on error */ }
                .collect { seconds ->
                    _uiState.value = _uiState.value.copy(restTimerSeconds = seconds)
                }
        }
    }

    fun onThemeModeSelected(themeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(themeMode)
        }
    }

    fun onWeightUnitSelected(weightUnit: WeightUnit) {
        viewModelScope.launch {
            userPreferencesRepository.setWeightUnit(weightUnit)
        }
    }

    fun onRestTimerDurationSelected(seconds: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setRestTimerDuration(seconds)
        }
    }
}
