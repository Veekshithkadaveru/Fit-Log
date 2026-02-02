package com.example.fitlog.ui.theme

import androidx.compose.ui.unit.dp

/**
 * FitLog Design System - Dimensions
 *
 * Standardized spacing, sizing, and layout values for consistency.
 * Based on an 4dp grid system.
 */

// === Spacing Scale ===
object Spacing {
    /** 2.dp - Minimal spacing (hairline gaps) */
    val xxs = 2.dp

    /** 4.dp - Extra small (tight text spacing) */
    val xs = 4.dp

    /** 8.dp - Small (component internal gaps) */
    val sm = 8.dp

    /** 12.dp - Medium (section dividers) */
    val md = 12.dp

    /** 16.dp - Large (standard padding, card margins) */
    val lg = 16.dp

    /** 20.dp - Extra large (major section breaks) */
    val xl = 20.dp

    /** 24.dp - 2X large (screen margins) */
    val xxl = 24.dp

    /** 32.dp - 3X large (hero spacing) */
    val xxxl = 32.dp
}

// === Icon Sizes ===
object IconSize {
    /** 12.dp - Tiny icons (indicators) */
    val tiny = 12.dp

    /** 16.dp - Small icons (inline with text) */
    val small = 16.dp

    /** 20.dp - Medium icons (buttons, chips) */
    val medium = 20.dp

    /** 24.dp - Standard icons (navigation, actions) */
    val standard = 24.dp

    /** 28.dp - Large icons (cards, headers) */
    val large = 28.dp

    /** 32.dp - Extra large icons (empty states) */
    val xl = 32.dp

    /** 48.dp - Hero icons (splash, onboarding) */
    val hero = 48.dp
}

// === Card Dimensions ===
object CardDimens {
    /** Standard card elevation */
    val elevation = 1.dp

    /** Elevated card elevation */
    val elevationHigh = 2.dp

    /** Card minimum height */
    val minHeight = 56.dp

    /** Compact card padding */
    val paddingCompact = 12.dp

    /** Standard card padding */
    val paddingStandard = 16.dp
}

// === Button Dimensions ===
object ButtonDimens {
    /** Standard button height */
    val height = 48.dp

    /** Compact button height */
    val heightCompact = 40.dp

    /** Large button height */
    val heightLarge = 56.dp

    /** Horizontal padding for buttons */
    val horizontalPadding = 24.dp

    /** Compact horizontal padding */
    val horizontalPaddingCompact = 16.dp

    /** Icon button size */
    val iconButtonSize = 48.dp

    /** Small icon button size */
    val iconButtonSizeSmall = 40.dp
}

// === Chip Dimensions ===
object ChipDimens {
    /** Small chip height */
    val heightSmall = 24.dp

    /** Standard chip height */
    val heightStandard = 32.dp

    /** Horizontal padding */
    val horizontalPadding = 8.dp

    /** Vertical padding */
    val verticalPadding = 4.dp

    /** Icon size in chips */
    val iconSize = 16.dp
}

// === Input Dimensions ===
object InputDimens {
    /** Standard text field height */
    val height = 56.dp

    /** Compact text field height */
    val heightCompact = 48.dp

    /** Horizontal padding */
    val horizontalPadding = 16.dp

    /** Set input field width (for weight/reps) */
    val setInputWidth = 72.dp

    /** Set number width */
    val setNumberWidth = 36.dp
}

// === List Dimensions ===
object ListDimens {
    /** Standard list item height */
    val itemHeight = 56.dp

    /** Compact list item height */
    val itemHeightCompact = 48.dp

    /** List item horizontal padding */
    val itemHorizontalPadding = 16.dp

    /** Divider thickness */
    val dividerThickness = 1.dp
}

// === Border Widths ===
object BorderWidth {
    /** Hairline border */
    val hairline = 0.5.dp

    /** Thin border */
    val thin = 1.dp

    /** Medium border */
    val medium = 1.5.dp

    /** Thick border (focus states) */
    val thick = 2.dp
}

// === Touch Targets ===
object TouchTarget {
    /** Minimum touch target size (accessibility) */
    val minimum = 48.dp

    /** Recommended touch target */
    val recommended = 56.dp
}

// === Screen Margins ===
object ScreenMargins {
    /** Horizontal screen padding */
    val horizontal = 16.dp

    /** Vertical screen padding (from edges) */
    val vertical = 16.dp

    /** Bottom navigation clearance */
    val bottomNavClearance = 80.dp
}
