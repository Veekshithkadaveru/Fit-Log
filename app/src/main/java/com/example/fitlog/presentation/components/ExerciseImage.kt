package com.example.fitlog.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun ExerciseImage(
    thumbnailRes: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    fallbackIconSize: androidx.compose.ui.unit.Dp = 32.dp
) {
    val context = LocalContext.current
    
    // Try to resolve the drawable resource ID from the string name
    val drawableId = remember(thumbnailRes) {
        if (!thumbnailRes.isNullOrBlank()) {
            context.resources.getIdentifier(
                thumbnailRes,
                "drawable",
                context.packageName
            )
        } else {
            0
        }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (drawableId != 0) {
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = contentDescription,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            // Fallback icon
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(fallbackIconSize)
            )
        }
    }
}
