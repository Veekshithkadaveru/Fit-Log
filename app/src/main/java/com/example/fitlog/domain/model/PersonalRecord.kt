package com.example.fitlog.domain.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Domain model representing a personal record for an exercise
 */
data class PersonalRecord(
    val id: Int = 0,
    val exerciseId: Int,
    val exercise: Exercise? = null,
    val maxWeight: Float = 0f,
    val maxReps: Int = 0,
    val maxVolume: Float = 0f,
    val achievedDate: Long
) {
    val achievedLocalDate: LocalDate
        @RequiresApi(Build.VERSION_CODES.O)
        get() = Instant.ofEpochMilli(achievedDate).atZone(ZoneId.systemDefault()).toLocalDate()
}

/**
 * Result of a PR check
 */
data class PRCheckResult(
    val isNewWeightPR: Boolean = false,
    val isNewRepsPR: Boolean = false,
    val isNewVolumePR: Boolean = false
) {
    val isAnyPR: Boolean
        get() = isNewWeightPR || isNewRepsPR || isNewVolumePR
}

