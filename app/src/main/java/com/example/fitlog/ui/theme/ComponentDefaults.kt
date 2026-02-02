package com.example.fitlog.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * FitLog Design System - Component Defaults
 *
 * Pre-configured styles for consistent component appearance.
 */

// === Card Styles ===
object FitLogCardDefaults {

    /** Standard card elevation */
    @Composable
    fun elevation(): CardElevation = CardDefaults.cardElevation(
        defaultElevation = CardDimens.elevation,
        pressedElevation = CardDimens.elevationHigh,
        focusedElevation = CardDimens.elevationHigh,
        hoveredElevation = CardDimens.elevationHigh
    )

    /** Elevated card elevation */
    @Composable
    fun elevatedElevation(): CardElevation = CardDefaults.cardElevation(
        defaultElevation = CardDimens.elevationHigh,
        pressedElevation = 4.dp,
        focusedElevation = 4.dp,
        hoveredElevation = 4.dp
    )

    /** Flat card (no elevation) */
    @Composable
    fun flatElevation(): CardElevation = CardDefaults.cardElevation(
        defaultElevation = 0.dp
    )

    /** Standard card colors */
    @Composable
    fun colors() = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface
    )

    /** Variant card colors (slightly tinted) */
    @Composable
    fun variantColors() = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    )

    /** Primary tinted card colors */
    @Composable
    fun primaryColors() = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )

    /** Subtle border for cards on white backgrounds */
    @Composable
    fun border(): BorderStroke = BorderStroke(
        width = BorderWidth.thin,
        color = MaterialTheme.colorScheme.outlineVariant
    )

    /** Standard card padding */
    val padding = PaddingValues(CardDimens.paddingStandard)

    /** Compact card padding */
    val paddingCompact = PaddingValues(CardDimens.paddingCompact)
}

// === Button Styles ===
object FitLogButtonDefaults {

    /** Primary button (filled) - main CTAs */
    @Composable
    fun primaryColors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    )

    /** Secondary button (tonal) - secondary actions */
    @Composable
    fun secondaryColors() = ButtonDefaults.filledTonalButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
    )

    /** Tertiary button (outlined) - tertiary actions */
    @Composable
    fun tertiaryColors() = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.primary
    )

    /** Success button */
    @Composable
    fun successColors() = ButtonDefaults.buttonColors(
        containerColor = GreenSecondary,
        contentColor = Color.White
    )

    /** Danger/destructive button */
    @Composable
    fun dangerColors() = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.error,
        contentColor = MaterialTheme.colorScheme.onError
    )

    /** Danger outlined button */
    @Composable
    fun dangerOutlinedColors() = ButtonDefaults.outlinedButtonColors(
        contentColor = MaterialTheme.colorScheme.error
    )

    /** Standard button content padding */
    val contentPadding = PaddingValues(
        horizontal = ButtonDimens.horizontalPadding,
        vertical = Spacing.md
    )

    /** Compact button content padding */
    val contentPaddingCompact = PaddingValues(
        horizontal = ButtonDimens.horizontalPaddingCompact,
        vertical = Spacing.sm
    )
}

// === Chip Styles ===
object FitLogChipDefaults {

    /** Standard chip padding */
    val padding = PaddingValues(
        horizontal = ChipDimens.horizontalPadding,
        vertical = ChipDimens.verticalPadding
    )

    /** Primary chip background */
    @Composable
    fun primaryContainerColor() = MaterialTheme.colorScheme.primaryContainer

    /** Secondary chip background */
    @Composable
    fun secondaryContainerColor() = MaterialTheme.colorScheme.secondaryContainer

    /** Surface chip background */
    @Composable
    fun surfaceContainerColor() = MaterialTheme.colorScheme.surfaceVariant
}

// === Badge Styles ===
object FitLogBadgeDefaults {

    /** Small badge padding */
    val paddingSmall = PaddingValues(
        horizontal = 6.dp,
        vertical = 2.dp
    )

    /** Standard badge padding */
    val padding = PaddingValues(
        horizontal = Spacing.sm,
        vertical = Spacing.xs
    )

    /** PR badge colors */
    @Composable
    fun prColors() = Pair(
        PRGoldContainer,
        PRGoldDark
    )

    /** Success badge colors */
    @Composable
    fun successColors() = Pair(
        SuccessContainer,
        GreenSecondaryDark
    )

    /** Warning badge colors */
    @Composable
    fun warningColors() = Pair(
        WarningContainer,
        OrangeTertiaryDark
    )

    /** Error badge colors */
    @Composable
    fun errorColors() = Pair(
        ErrorContainer,
        Error
    )
}

// === List Item Styles ===
object FitLogListDefaults {

    /** Standard list item padding */
    val itemPadding = PaddingValues(
        horizontal = ListDimens.itemHorizontalPadding,
        vertical = Spacing.md
    )

    /** Compact list item padding */
    val itemPaddingCompact = PaddingValues(
        horizontal = ListDimens.itemHorizontalPadding,
        vertical = Spacing.sm
    )
}
