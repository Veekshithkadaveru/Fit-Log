package com.example.fitlog.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * FitLog Pro Theme - Light Professional Edition
 *
 * Clean, bright, professional appearance with:
 * - Blue primary for trust and professionalism
 * - Green for success and progress
 * - Soft whites and grays for comfort
 */

// Light Color Scheme - Professional & Clean
private val FitLogLightColorScheme = lightColorScheme(
    // Primary - Professional Blue
    primary = BluePrimary,
    onPrimary = TextOnPrimaryLight,
    primaryContainer = BluePrimaryContainer,
    onPrimaryContainer = BluePrimaryDark,

    // Secondary - Fresh Green (Success)
    secondary = GreenSecondary,
    onSecondary = TextOnSecondaryLight,
    secondaryContainer = GreenSecondaryContainer,
    onSecondaryContainer = GreenSecondaryDark,

    // Tertiary - Warm Orange (Energy)
    tertiary = OrangeTertiary,
    onTertiary = TextOnPrimaryLight,
    tertiaryContainer = OrangeTertiaryContainer,
    onTertiaryContainer = OrangeTertiaryDark,

    // Background and surfaces - Light & Clean
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,

    // Error states
    error = Error,
    onError = TextOnPrimaryLight,
    errorContainer = ErrorContainer,
    onErrorContainer = Color(0xFFB91C1C),

    // Outlines - Subtle borders
    outline = OutlineLight,
    outlineVariant = OutlineLightVariant,

    // Inverse colors (for snackbars, etc.)
    inverseSurface = Color(0xFF1F2937),
    inverseOnSurface = Color(0xFFF9FAFB),
    inversePrimary = BluePrimarySoft,

    // Scrim for modals
    scrim = Color.Black.copy(alpha = 0.32f),

    // Surface tint
    surfaceTint = BluePrimary
)

// Dark Color Scheme - For dark mode support
private val FitLogDarkColorScheme = darkColorScheme(
    primary = BluePrimaryLight,
    onPrimary = TextOnPrimary,
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = BluePrimarySoft,

    secondary = GreenSecondaryLight,
    onSecondary = TextOnSecondary,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = GreenSecondaryLight,

    tertiary = OrangeTertiaryLight,
    onTertiary = TextOnSecondary,
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = OrangeTertiaryLight,

    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = ErrorLight,
    onError = TextOnPrimary,
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = ErrorLight,

    outline = OutlineDefault,
    outlineVariant = OutlineVariant,

    inverseSurface = Color(0xFFF9FAFB),
    inverseOnSurface = Color(0xFF1F2937),
    inversePrimary = BluePrimaryDark,

    scrim = Color.Black.copy(alpha = 0.7f),
    surfaceTint = BluePrimaryLight
)

@Composable
fun FitLogTheme(
    // Default to LIGHT theme for professional look
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) FitLogDarkColorScheme else FitLogLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val backgroundColor = if (darkTheme) DarkBackground else LightBackground
            @Suppress("DEPRECATION")
            window.statusBarColor = backgroundColor.toArgb()
            @Suppress("DEPRECATION")
            window.navigationBarColor = backgroundColor.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Extended color scheme for semantic colors not in Material 3
 */
object FitLogColors {
    // Primary variants
    val primaryBright = BluePrimary
    val primaryLight = BluePrimaryLight
    val primarySoft = BluePrimarySoft
    val primaryContainer = BluePrimaryContainer

    // Secondary variants
    val secondaryBright = GreenSecondary
    val secondaryLight = GreenSecondaryLight
    val secondaryContainer = GreenSecondaryContainer

    // Tertiary
    val tertiaryBright = OrangeTertiary
    val tertiaryLight = OrangeTertiaryLight
    val tertiaryContainer = OrangeTertiaryContainer

    // Accent colors
    val accentPink = RoseAccent
    val accentPinkLight = RoseAccentLight
    val accentPinkContainer = RoseAccentContainer
    val accentPurple = PurpleAccent
    val accentPurpleLight = PurpleAccentLight
    val accentPurpleContainer = PurpleAccentContainer

    // Success states
    val success = Success
    val successLight = SuccessLight
    val successContainer = SuccessContainer
    val onSuccess = TextOnSecondaryLight

    // Warning states
    val warning = Warning
    val warningLight = WarningLight
    val warningContainer = WarningContainer
    val onWarning = TextOnSecondaryLight

    // Info states
    val info = Info
    val infoLight = InfoLight
    val infoContainer = InfoContainer

    // Personal Records
    val prGold = PRGold
    val prGoldLight = PRGoldLight
    val prGoldDark = PRGoldDark
    val prGoldContainer = PRGoldContainer

    // Cardio activities
    val cardio = CardioOrange
    val cardioLight = CardioOrangeLight
    val cardioContainer = CardioContainer

    // Muscle group colors for analytics
    val muscleChest = MuscleChest
    val muscleBack = MuscleBack
    val muscleShoulders = MuscleShoulders
    val muscleArms = MuscleArms
    val muscleLegs = MuscleLegs
    val muscleCore = MuscleCore

    // Chart colors
    val chartLine1 = ChartLine1
    val chartLine2 = ChartLine2
    val chartLine3 = ChartLine3
    val chartLine4 = ChartLine4
    val chartFill = ChartFill
    val chartFillSecondary = ChartFillSecondary
    val chartGrid = ChartGrid

    // Surface elevations
    val surfaceHigh = LightSurfaceHigh
    val surfaceBright = LightSurfaceBright

    // Interactive states
    val disabled = Disabled
    val disabledContent = DisabledContent

    // Gradient
    val gradientStart = GradientStart
    val gradientMid = GradientMid
    val gradientEnd = GradientEnd
}
