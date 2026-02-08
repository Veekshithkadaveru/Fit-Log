package app.krafted.fitlog.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import app.krafted.fitlog.domain.model.ThemeMode
import app.krafted.fitlog.domain.model.WeightUnit
import app.krafted.fitlog.domain.repository.UserPreferencesRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings_preferences"
)

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserPreferencesRepository {

    private object PreferenceKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val WEIGHT_UNIT = stringPreferencesKey("weight_unit")
        val REST_TIMER_DURATION = intPreferencesKey("rest_timer_duration")
    }

    override val themeMode: Flow<ThemeMode> = context.settingsDataStore.data
        .map { preferences ->
            val themeName = preferences[PreferenceKeys.THEME_MODE]
            if (themeName != null) ThemeMode.fromString(themeName) else ThemeMode.SYSTEM
        }

    override suspend fun setThemeMode(themeMode: ThemeMode) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = themeMode.name
        }
    }

    override val weightUnit: Flow<WeightUnit> = context.settingsDataStore.data
        .map { preferences ->
            val unitName = preferences[PreferenceKeys.WEIGHT_UNIT]
            if (unitName != null) WeightUnit.fromString(unitName) else WeightUnit.KG
        }

    override suspend fun setWeightUnit(weightUnit: WeightUnit) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferenceKeys.WEIGHT_UNIT] = weightUnit.name
        }
    }

    override val restTimerDuration: Flow<Int> = context.settingsDataStore.data
        .map { preferences ->
            preferences[PreferenceKeys.REST_TIMER_DURATION] ?: DEFAULT_REST_TIMER_SECONDS
        }

    override suspend fun setRestTimerDuration(seconds: Int) {
        context.settingsDataStore.edit { preferences ->
            preferences[PreferenceKeys.REST_TIMER_DURATION] = seconds
        }
    }

    companion object {
        const val DEFAULT_REST_TIMER_SECONDS = 90
    }
}
