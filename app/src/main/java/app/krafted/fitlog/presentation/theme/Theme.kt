package app.krafted.fitlog.presentation.theme

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
 * FitLog Pro Theme - Professional Edition
 *
 * Clean, minimal, modern aesthetic inspired by:
 * - Strava, Nike Training Club, Strong
 * - Premium SaaS apps like Linear, Notion
 */

// Light Color Scheme - Clean & Professional
private val FitLogLightColorScheme = lightColorScheme(
    // Primary - Deep Indigo
    primary = IndigoPrimary,
    onPrimary = TextOnPrimaryLight,
    primaryContainer = IndigoPrimaryContainer,
    onPrimaryContainer = IndigoPrimaryDark,

    // Secondary - Emerald Green
    secondary = EmeraldSecondary,
    onSecondary = TextOnSecondaryLight,
    secondaryContainer = EmeraldSecondaryContainer,
    onSecondaryContainer = EmeraldSecondaryDark,

    // Tertiary - Warm Amber
    tertiary = AmberTertiary,
    onTertiary = TextOnPrimaryLight,
    tertiaryContainer = AmberTertiaryContainer,
    onTertiaryContainer = AmberTertiaryDark,

    // Background and surfaces
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,

    // Error states
    error = CoralAccent,
    onError = TextOnPrimaryLight,
    errorContainer = CoralAccentContainer,
    onErrorContainer = Color(0xFFB91C1C),

    // Outlines
    outline = OutlineLight,
    outlineVariant = OutlineLightVariant,

    // Inverse colors
    inverseSurface = Color(0xFF1A1A1A),
    inverseOnSurface = Color(0xFFF5F5F5),
    inversePrimary = IndigoPrimarySoft,

    // Scrim
    scrim = Color.Black.copy(alpha = 0.32f),

    // Surface tint
    surfaceTint = IndigoPrimary
)

// Dark Color Scheme
private val FitLogDarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryLight,
    onPrimary = TextOnPrimary,
    primaryContainer = Color(0xFF312E81),
    onPrimaryContainer = IndigoPrimarySoft,

    secondary = EmeraldSecondaryLight,
    onSecondary = Color(0xFF003822),
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = EmeraldSecondaryLight,

    tertiary = AmberTertiaryLight,
    onTertiary = Color(0xFF422006),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = AmberTertiaryLight,

    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = CoralAccentLight,
    onError = Color(0xFF601410),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = CoralAccentLight,

    outline = OutlineDefault,
    outlineVariant = OutlineVariant,

    inverseSurface = Color(0xFFF5F5F5),
    inverseOnSurface = Color(0xFF1A1A1A),
    inversePrimary = IndigoPrimaryDark,

    scrim = Color.Black.copy(alpha = 0.6f),
    surfaceTint = IndigoPrimaryLight
)

@Composable
fun FitLogTheme(
    darkTheme: Boolean = false,  // Default to light
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
 * Extended colors for FitLog-specific use cases
 */
object FitLogColors {
    // Primary
    val primary = IndigoPrimary
    val primaryLight = IndigoPrimaryLight
    val primaryDark = IndigoPrimaryDark
    val primaryContainer = IndigoPrimaryContainer
    val primarySoft = IndigoPrimarySoft

    // Secondary
    val secondary = EmeraldSecondary
    val secondaryLight = EmeraldSecondaryLight
    val secondaryContainer = EmeraldSecondaryContainer

    // Tertiary
    val tertiary = AmberTertiary
    val tertiaryLight = AmberTertiaryLight
    val tertiaryContainer = AmberTertiaryContainer

    // Accents
    val coral = CoralAccent
    val coralLight = CoralAccentLight
    val coralContainer = CoralAccentContainer
    val violet = VioletAccent
    val violetLight = VioletAccentLight
    val violetContainer = VioletAccentContainer

    // Semantic
    val success = Success
    val successLight = SuccessLight
    val successContainer = SuccessContainer
    val warning = Warning
    val warningLight = WarningLight
    val warningContainer = WarningContainer
    val error = Error
    val errorLight = ErrorLight
    val errorContainer = ErrorContainer
    val info = Info
    val infoLight = InfoLight
    val infoContainer = InfoContainer

    // PR Gold
    val prGold = PRGold
    val prGoldLight = PRGoldLight
    val prGoldDark = PRGoldDark
    val prGoldContainer = PRGoldContainer

    // Cardio
    val cardio = CardioOrange
    val cardioLight = CardioOrangeLight
    val cardioContainer = CardioContainer

    // Muscles
    val muscleChest = MuscleChest
    val muscleBack = MuscleBack
    val muscleShoulders = MuscleShoulders
    val muscleArms = MuscleArms
    val muscleLegs = MuscleLegs
    val muscleCore = MuscleCore

    // Charts
    val chartLine1 = ChartLine1
    val chartLine2 = ChartLine2
    val chartLine3 = ChartLine3
    val chartLine4 = ChartLine4
    val chartFill = ChartFill
    val chartFillSecondary = ChartFillSecondary
    val chartGrid = ChartGrid

    // Surfaces
    val surfaceHigh = LightSurfaceHigh
    val surfaceBright = LightSurfaceBright

    // States
    val disabled = Disabled
    val disabledContent = DisabledContent

    // Gradients
    val gradientStart = GradientStart
    val gradientMid = GradientMid
    val gradientEnd = GradientEnd

    // Legacy aliases
    val accentPink = CoralAccent
    val accentPinkLight = CoralAccentLight
    val accentPinkContainer = CoralAccentContainer
    val accentPurple = VioletAccent
    val accentPurpleLight = VioletAccentLight
    val accentPurpleContainer = VioletAccentContainer
    val primaryBright = IndigoPrimary
    val secondaryBright = EmeraldSecondary
    val tertiaryBright = AmberTertiary
    val onSuccess = TextOnSecondaryLight
    val onWarning = TextOnSecondaryLight
}
