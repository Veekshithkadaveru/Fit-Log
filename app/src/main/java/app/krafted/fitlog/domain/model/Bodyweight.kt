package app.krafted.fitlog.domain.model

data class Bodyweight(
    val id: Int = 0,
    val date: Long,
    val weight: Float,
    val notes: String? = null
) {
    // Removed android.os.Build dependency for pure domain model
    // Date conversions should happen in presentation layer or using ThreeTenAbp if needed
}

