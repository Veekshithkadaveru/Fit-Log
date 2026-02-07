package app.krafted.fitlog.domain.repository

import app.krafted.fitlog.domain.model.ThemeMode
import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {

    val themeMode: Flow<ThemeMode>

    suspend fun setThemeMode(themeMode: ThemeMode)
}
