package com.example.fitlog.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.fitlog.ui.theme.AnimationDuration
import com.example.fitlog.ui.theme.ListAnimations
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * A card with press animation (scale down on press)
 * Industry standard touch feedback animation
 */
@Composable
fun AnimatedPressCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    Card(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = shape,
        colors = colors,
        elevation = elevation
    ) {
        content()
    }
}

/**
 * Animated list item with staggered entrance animation
 * Creates a cascading effect when list items appear
 */
@Composable
fun AnimatedListItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    val alpha = remember { Animatable(0f) }
    val translationY = remember { Animatable(30f) }

    LaunchedEffect(Unit) {
        delay(ListAnimations.calculateDelay(index).toLong())
        isVisible = true
        // Animate alpha and translation together
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = AnimationDuration.medium,
                    easing = FastOutSlowInEasing
                )
            )
        }
        launch {
            translationY.animateTo(
                targetValue = 0f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha.value
                this.translationY = translationY.value
            }
    ) {
        content()
    }
}

/**
 * Animated card with staggered entrance for lists
 * Combines AnimatedPressCard with AnimatedListItem behavior
 */
@Composable
fun AnimatedListCard(
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    content: @Composable () -> Unit
) {
    AnimatedListItem(index = index, modifier = modifier) {
        AnimatedPressCard(
            onClick = onClick,
            shape = shape,
            colors = colors,
            elevation = elevation,
            content = content
        )
    }
}

/**
 * Expandable content with smooth animation
 */
@Composable
fun AnimatedExpandable(
    expanded: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = expanded,
        modifier = modifier,
        enter = fadeIn(
            animationSpec = tween(AnimationDuration.medium)
        ) + expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            expandFrom = Alignment.Top
        ),
        exit = fadeOut(
            animationSpec = tween(AnimationDuration.short)
        ) + shrinkVertically(
            animationSpec = tween(AnimationDuration.medium),
            shrinkTowards = Alignment.Top
        )
    ) {
        content()
    }
}

/**
 * Animated counter that smoothly transitions between numbers
 */
@Composable
fun AnimatedCounter(
    count: Int,
    modifier: Modifier = Modifier,
    content: @Composable (Int) -> Unit
) {
    var previousCount by remember { mutableStateOf(count) }
    var targetCount by remember { mutableStateOf(count) }

    LaunchedEffect(count) {
        previousCount = targetCount
        targetCount = count
    }

    val isIncreasing = targetCount > previousCount

    AnimatedVisibility(
        visible = true,
        modifier = modifier,
        enter = fadeIn(animationSpec = tween(AnimationDuration.short)) +
                slideInVertically(
                    animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
                    initialOffsetY = { if (isIncreasing) it else -it }
                ),
        exit = fadeOut(animationSpec = tween(AnimationDuration.short)) +
                slideOutVertically(
                    animationSpec = tween(AnimationDuration.medium, easing = FastOutSlowInEasing),
                    targetOffsetY = { if (isIncreasing) -it else it }
                )
    ) {
        content(count)
    }
}

/**
 * Shimmer loading placeholder animation
 */
@Composable
fun AnimatedShimmer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shimmerAlpha = remember { Animatable(0.3f) }

    LaunchedEffect(Unit) {
        while (true) {
            shimmerAlpha.animateTo(
                targetValue = 0.7f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
            shimmerAlpha.animateTo(
                targetValue = 0.3f,
                animationSpec = tween(
                    durationMillis = 800,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    Box(
        modifier = modifier.graphicsLayer { alpha = shimmerAlpha.value }
    ) {
        content()
    }
}

/**
 * Pulse animation for highlighting important elements
 */
@Composable
fun AnimatedPulse(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val scale = remember { Animatable(1f) }

    LaunchedEffect(enabled) {
        if (enabled) {
            while (true) {
                scale.animateTo(
                    targetValue = 1.05f,
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = 600,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        } else {
            scale.animateTo(1f)
        }
    }

    Box(
        modifier = modifier.scale(scale.value)
    ) {
        content()
    }
}

/**
 * Column with animated content size changes
 */
@Composable
fun AnimatedColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
    ) {
        content()
    }
}
