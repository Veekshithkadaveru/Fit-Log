package app.krafted.fitlog.presentation.screens.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.krafted.fitlog.domain.model.*
import app.krafted.fitlog.domain.usecase.MuscleTrackingUseCase
import timber.log.Timber
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MuscleAnalyticsViewModel @Inject constructor(
    private val muscleTrackingUseCase: MuscleTrackingUseCase
) : ViewModel() {

    private val _selectedWeeks = MutableStateFlow(4) // Default to 4 weeks
    private val _isLoading = MutableStateFlow(false)
    private val _analytics = MutableStateFlow<MuscleGroupAnalytics?>(null)
    private val _weeklyFrequencies = MutableStateFlow<List<WeeklyMuscleFrequency>>(emptyList())

    val uiState: StateFlow<MuscleAnalyticsUiState> = combine(
        _selectedWeeks,
        _isLoading,
        _analytics,
        _weeklyFrequencies
    ) { weeks, loading, analytics, frequencies ->
        MuscleAnalyticsUiState(
            selectedWeeks = weeks,
            isLoading = loading,
            muscleStats = analytics?.muscleStats ?: emptyList(),
            imbalances = analytics?.imbalances ?: emptyList(),
            recommendations = analytics?.recommendations ?: emptyList(),
            weeklyFrequencies = frequencies
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MuscleAnalyticsUiState(isLoading = true)
    )

    init {
        loadAnalytics()
    }

    fun updateWeeksFilter(weeks: Int) {
        _selectedWeeks.value = weeks
        loadAnalytics()
    }

    fun refreshAnalytics() {
        loadAnalytics()
    }

    private fun loadAnalytics() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val weeks = _selectedWeeks.value
                val endDate = System.currentTimeMillis()
                val startDate = endDate - (weeks * 7 * 24 * 60 * 60 * 1000L)
                
                // Fetch stats ONCE
                val stats = muscleTrackingUseCase.getMuscleGroupStats(startDate, endDate)
                
                // Use the fetched stats for both analytics and frequencies
                val analytics = muscleTrackingUseCase.generateAnalyticsFromStats(stats)
                val frequencies = muscleTrackingUseCase.calculateWeeklyFrequenciesFromStats(stats, startDate, endDate)

                _analytics.value = analytics
                _weeklyFrequencies.value = frequencies
            } catch (e: Exception) {
                Timber.e(e)
                _analytics.value = null
                _weeklyFrequencies.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

data class MuscleAnalyticsUiState(
    val selectedWeeks: Int = 4,
    val isLoading: Boolean = false,
    val muscleStats: List<MuscleGroupStats> = emptyList(),
    val imbalances: List<MuscleGroupImbalance> = emptyList(),
    val recommendations: List<TrainingRecommendation> = emptyList(),
    val weeklyFrequencies: List<WeeklyMuscleFrequency> = emptyList()
)
