package app.krafted.fitlog.domain.repository

import app.krafted.fitlog.domain.model.ThemeMode
import app.krafted.fitlog.domain.model.WeightUnit
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(themeMode: ThemeMode)

    val weightUnit: Flow<WeightUnit>

    suspend fun setWeightUnit(weightUnit: WeightUnit)

    val restTimerDuration: Flow<Int>

    suspend fun setRestTimerDuration(seconds: Int)
}
