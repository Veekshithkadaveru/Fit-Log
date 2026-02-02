package com.example.fitlog.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * FitLog Design System - Shapes
 *
 * Standardized corner radii for consistent visual language.
 */

// === Corner Radius Values ===
object CornerRadius {
    /** 4.dp - Extra small (small badges, indicators) */
    val xs = 4.dp

    /** 8.dp - Small (chips, small cards) */
    val sm = 8.dp

    /** 12.dp - Medium (standard cards, buttons) */
    val md = 12.dp

    /** 16.dp - Large (dialogs, bottom sheets) */
    val lg = 16.dp

    /** 20.dp - Extra large (modals, large cards) */
    val xl = 20.dp

    /** 24.dp - 2X large (hero cards) */
    val xxl = 24.dp

    /** Full round (pills, circular buttons) */
    val full = 999.dp
}

// === Pre-defined Shapes ===
object FitLogShapes {
    /** Extra small rounded corners */
    val extraSmall = RoundedCornerShape(CornerRadius.xs)

    /** Small rounded corners - chips, badges */
    val small = RoundedCornerShape(CornerRadius.sm)

    /** Medium rounded corners - standard cards, inputs */
    val medium = RoundedCornerShape(CornerRadius.md)

    /** Large rounded corners - dialogs, modals */
    val large = RoundedCornerShape(CornerRadius.lg)

    /** Extra large rounded corners - bottom sheets */
    val extraLarge = RoundedCornerShape(CornerRadius.xl)

    /** Pill shape - full rounded */
    val pill = RoundedCornerShape(CornerRadius.full)

    // === Specific Use Cases ===

    /** Card shape */
    val card = RoundedCornerShape(CornerRadius.md)

    /** Button shape */
    val button = RoundedCornerShape(CornerRadius.md)

    /** Chip shape */
    val chip = RoundedCornerShape(CornerRadius.sm)

    /** Badge shape */
    val badge = RoundedCornerShape(CornerRadius.xs)

    /** Input field shape */
    val input = RoundedCornerShape(CornerRadius.sm)

    /** Bottom sheet shape (top corners only) */
    val bottomSheet = RoundedCornerShape(
        topStart = CornerRadius.xl,
        topEnd = CornerRadius.xl,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    /** Top app bar shape (bottom corners only) */
    val topAppBar = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = CornerRadius.md,
        bottomEnd = CornerRadius.md
    )

    /** FAB shape */
    val fab = RoundedCornerShape(CornerRadius.lg)
}

// === Material 3 Shapes Override ===
val AppShapes = Shapes(
    extraSmall = FitLogShapes.extraSmall,
    small = FitLogShapes.small,
    medium = FitLogShapes.medium,
    large = FitLogShapes.large,
    extraLarge = FitLogShapes.extraLarge
)
