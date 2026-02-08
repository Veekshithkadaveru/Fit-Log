package app.krafted.fitlog.domain.model

enum class ThemeMode(val displayName: String) {
    LIGHT("Light"),
    DARK("Dark"),
    SYSTEM("System");

    companion object {
        fun fromString(value: String): ThemeMode {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: SYSTEM
        }
    }
}
