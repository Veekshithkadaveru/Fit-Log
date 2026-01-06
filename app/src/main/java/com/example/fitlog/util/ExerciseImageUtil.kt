package com.example.fitlog.util

import android.content.Context
import androidx.annotation.DrawableRes
import com.example.fitlog.R

/**
 * Utility class for handling exercise images and thumbnails
 */
object ExerciseImageUtil {
    
    /**
     * Get drawable resource ID from thumbnail resource name
     * Falls back to placeholder if resource not found
     */
    @DrawableRes
    fun getExerciseImageRes(context: Context, thumbnailRes: String): Int {
        return try {
            val resourceId = context.resources.getIdentifier(
                thumbnailRes,
                "drawable",
                context.packageName
            )
            if (resourceId != 0) resourceId else R.drawable.ic_exercise_placeholder
        } catch (e: Exception) {
            R.drawable.ic_exercise_placeholder
        }
    }
    
    /**
     * Check if a drawable resource exists
     */
    fun hasCustomImage(context: Context, thumbnailRes: String): Boolean {
        return try {
            val resourceId = context.resources.getIdentifier(
                thumbnailRes,
                "drawable",
                context.packageName
            )
            resourceId != 0
        } catch (e: Exception) {
            false
        }
    }
}