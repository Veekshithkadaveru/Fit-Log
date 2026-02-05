package app.krafted.fitlog.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Domain model representing a bodyweight entry
 */
data class Bodyweight(
    val id: Int = 0,
    val date: Long,
    val weight: Float,
    val notes: String? = null
) {
    val localDate: LocalDate
        @RequiresApi(Build.VERSION_CODES.O)
        get() = Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
}

