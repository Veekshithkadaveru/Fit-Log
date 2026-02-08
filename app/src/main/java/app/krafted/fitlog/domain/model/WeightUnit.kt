package app.krafted.fitlog.domain.model

enum class WeightUnit(val displayName: String, val label: String) {
    KG("Kilograms", "kg"),
    LBS("Pounds", "lbs");

    /**
     * Convert a weight value stored in KG (the database unit) to this display unit.
     */
    fun fromKg(kg: Float): Float = when (this) {
        KG -> kg
        LBS -> kg * KG_TO_LBS
    }

    /**
     * Convert a value entered in this display unit back to KG for storage.
     */
    fun toKg(value: Float): Float = when (this) {
        KG -> value
        LBS -> value / KG_TO_LBS
    }

    companion object {
        private const val KG_TO_LBS = 2.20462f

        fun fromString(value: String): WeightUnit {
            return entries.find {
                it.name.equals(value, ignoreCase = true)
            } ?: KG
        }
    }
}
