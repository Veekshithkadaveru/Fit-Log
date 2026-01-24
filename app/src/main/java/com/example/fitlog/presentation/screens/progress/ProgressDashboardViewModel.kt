package com.example.fitlog.presentation.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.domain.model.DateRangeFilter
import com.example.fitlog.domain.model.ProgressSummary
import com.example.fitlog.domain.repository.ProgressRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressDashboardUiState(
    val isLoading: Boolean = true,
    val progressSummary: ProgressSummary? = null,
    val selectedDateRange: DateRangeFilter = DateRangeFilter.LAST_30_DAYS,
    val error: String? = null
)

@HiltViewModel
class ProgressDashboardViewModel @Inject constructor(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressDashboardUiState())
    val uiState: StateFlow<ProgressDashboardUiState> = _uiState.asStateFlow()

    init {
        loadProgressSummary()
    }

    fun loadProgressSummary() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val summary = progressRepository.getProgressSummary()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    progressSummary = summary
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load progress data"
                )
            }
        }
    }

    fun selectDateRange(filter: DateRangeFilter) {
        _uiState.value = _uiState.value.copy(selectedDateRange = filter)
        // Date range selection will be used more in PR 3 for charts
    }

    fun refresh() {
        loadProgressSummary()
    }
}
