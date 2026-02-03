package com.example.fitlog.ui.theme

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment

/**
 * Centralized animation durations following Material Design 3 motion guidelines
 * https://m3.material.io/styles/motion/easing-and-duration/tokens-specs
 */
object AnimationDuration {
    const val extraShort = 100
    const val short = 200
    const val medium = 300
    const val long = 400
    const val extraLong = 500
}

/**
 * Reusable animation specs for consistent motion throughout the app
 */
object FitLogAnimationSpecs {
    // Standard easing specs
    val standardTween: AnimationSpec<Float> = tween(
        durationMillis = AnimationDuration.medium,
        easing = FastOutSlowInEasing
    )

    val fastTween: AnimationSpec<Float> = tween(
        durationMillis = AnimationDuration.short,
        easing = FastOutSlowInEasing
    )

    val slowTween: AnimationSpec<Float> = tween(
        durationMillis = AnimationDuration.long,
        easing = LinearOutSlowInEasing
    )

    // Spring specs for more natural motion
    val gentleSpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    val snappySpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val bouncySpring: AnimationSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMediumLow
    )
}

/**
 * Screen transition animations following Material Design 3 patterns
 */
object ScreenTransitions {
    // Forward navigation (going deeper into hierarchy)
    val enterForward: EnterTransition = fadeIn(
        animationSpec = tween(AnimationDuration.medium)
    ) + slideInHorizontally(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        initialOffsetX = { fullWidth -> fullWidth / 4 }
    )

    val exitForward: ExitTransition = fadeOut(
        animationSpec = tween(AnimationDuration.short)
    ) + slideOutHorizontally(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        targetOffsetX = { fullWidth -> -fullWidth / 4 }
    )

    // Backward navigation (going back up hierarchy)
    val enterBackward: EnterTransition = fadeIn(
        animationSpec = tween(AnimationDuration.medium)
    ) + slideInHorizontally(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        initialOffsetX = { fullWidth -> -fullWidth / 4 }
    )

    val exitBackward: ExitTransition = fadeOut(
        animationSpec = tween(AnimationDuration.short)
    ) + slideOutHorizontally(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        targetOffsetX = { fullWidth -> fullWidth / 4 }
    )

    // Bottom navigation transitions (crossfade with subtle scale)
    val enterBottomNav: EnterTransition = fadeIn(
        animationSpec = tween(AnimationDuration.medium)
    ) + scaleIn(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        initialScale = 0.92f
    )

    val exitBottomNav: ExitTransition = fadeOut(
        animationSpec = tween(AnimationDuration.short)
    ) + scaleOut(
        animationSpec = tween(AnimationDuration.short, easing = FastOutSlowInEasing),
        targetScale = 0.92f
    )

    // Vertical slide transitions (for modals/sheets)
    val enterFromBottom: EnterTransition = fadeIn(
        animationSpec = tween(AnimationDuration.medium)
    ) + slideInVertically(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        initialOffsetY = { fullHeight -> fullHeight / 3 }
    )

    val exitToBottom: ExitTransition = fadeOut(
        animationSpec = tween(AnimationDuration.short)
    ) + slideOutVertically(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        targetOffsetY = { fullHeight -> fullHeight / 3 }
    )

    // Expand/collapse for content
    val expandContent: EnterTransition = fadeIn(
        animationSpec = tween(AnimationDuration.medium)
    ) + expandVertically(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        expandFrom = Alignment.Top
    )

    val collapseContent: ExitTransition = fadeOut(
        animationSpec = tween(AnimationDuration.short)
    ) + shrinkVertically(
        animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
        shrinkTowards = Alignment.Top
    )
}

/**
 * List item stagger animation helper
 * Used to create cascading entrance effects for list items
 */
object ListAnimations {
    const val staggerDelayMillis = 50
    const val maxStaggerItems = 10 // Cap to prevent long delays

    fun calculateDelay(index: Int): Int {
        return (index.coerceAtMost(maxStaggerItems) * staggerDelayMillis)
    }
}

/**
 * Composable helper for staggered list item animations
 */
@Composable
fun rememberStaggeredAnimationState(
    index: Int,
    delayMillis: Int = ListAnimations.calculateDelay(index)
): Animatable<Float, *> {
    val animatable = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = AnimationDuration.medium,
                easing = FastOutSlowInEasing
            )
        )
    }

    return animatable
}
