package app.krafted.fitlog.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * FitLog Pro Color Palette - Professional Light Theme
 *
 * Design Philosophy:
 * - Clean, minimal, modern aesthetic
 * - Refined color palette with muted tones
 * - Consistent visual hierarchy
 * - Inspired by top fitness apps like Strava, Nike Training
 */

// === Light Background Colors ===
val LightBackground = Color(0xFFFAFAFA)      // Warm off-white
val LightSurface = Color(0xFFFFFFFF)         // Pure white cards
val LightSurfaceVariant = Color(0xFFF5F5F5)  // Light gray surface
val LightSurfaceHigh = Color(0xFFEEEEEE)     // Pressed/elevated
val LightSurfaceBright = Color(0xFFFFFFFF)   // Bright surface

// === Dark Background Colors (for dark mode) ===
val DarkBackground = Color(0xFF0A0A0A)
val DarkSurface = Color(0xFF141414)
val DarkSurfaceVariant = Color(0xFF1F1F1F)
val DarkSurfaceHigh = Color(0xFF2A2A2A)
val DarkSurfaceBright = Color(0xFF353535)

// === Primary - Deep Indigo (Trust, Premium) ===
val IndigoPrimary = Color(0xFF4F46E5)        // Rich indigo
val IndigoPrimaryLight = Color(0xFF6366F1)   // Lighter indigo
val IndigoPrimaryDark = Color(0xFF4338CA)    // Darker indigo
val IndigoPrimaryContainer = Color(0xFFE0E7FF) // Soft indigo bg
val IndigoPrimarySoft = Color(0xFF818CF8)    // Soft indigo

// === Secondary - Emerald Green (Success, Progress) ===
val EmeraldSecondary = Color(0xFF059669)     // Rich emerald
val EmeraldSecondaryLight = Color(0xFF10B981) // Lighter emerald
val EmeraldSecondaryDark = Color(0xFF047857) // Darker emerald
val EmeraldSecondaryContainer = Color(0xFFD1FAE5) // Soft green bg

// === Tertiary - Warm Amber (Energy, Highlights) ===
val AmberTertiary = Color(0xFFD97706)        // Rich amber
val AmberTertiaryLight = Color(0xFFF59E0B)   // Lighter amber
val AmberTertiaryDark = Color(0xFFB45309)    // Darker amber
val AmberTertiaryContainer = Color(0xFFFEF3C7) // Soft amber bg

// === Accent - Coral (Cardio, Energy) ===
val CoralAccent = Color(0xFFEF4444)          // Vibrant coral
val CoralAccentLight = Color(0xFFF87171)     // Lighter coral
val CoralAccentContainer = Color(0xFFFEE2E2) // Soft coral bg

// === Accent - Violet (Premium, Special) ===
val VioletAccent = Color(0xFF7C3AED)         // Rich violet
val VioletAccentLight = Color(0xFF8B5CF6)    // Lighter violet
val VioletAccentContainer = Color(0xFFEDE9FE) // Soft violet bg

// === Text Colors - Light Theme ===
val TextPrimaryLight = Color(0xFF1A1A1A)     // Near black
val TextSecondaryLight = Color(0xFF525252)   // Medium gray
val TextTertiaryLight = Color(0xFF9CA3AF)    // Light gray
val TextOnPrimaryLight = Color(0xFFFFFFFF)   // White
val TextOnSecondaryLight = Color(0xFFFFFFFF) // White

// === Text Colors - Dark Theme ===
val TextPrimary = Color(0xFFF5F5F5)
val TextSecondary = Color(0xFFA3A3A3)
val TextTertiary = Color(0xFF737373)
val TextOnPrimary = Color(0xFFFFFFFF)
val TextOnSecondary = Color(0xFF000000)
val TextOnSurfaceVariant = Color(0xFFA3A3A3)

// === Outline Colors ===
val OutlineLight = Color(0xFFE5E5E5)         // Subtle border
val OutlineLightVariant = Color(0xFFD4D4D4)  // Slightly darker
val OutlineDefault = Color(0xFF404040)
val OutlineVariant = Color(0xFF2E2E2E)
val OutlineFocused = Color(0xFF525252)

// === Semantic Colors ===
val Success = EmeraldSecondary
val SuccessLight = EmeraldSecondaryLight
val SuccessContainer = EmeraldSecondaryContainer
val Error = CoralAccent
val ErrorLight = CoralAccentLight
val ErrorContainer = CoralAccentContainer
val Warning = AmberTertiary
val WarningLight = AmberTertiaryLight
val WarningContainer = AmberTertiaryContainer
val Info = IndigoPrimary
val InfoLight = IndigoPrimaryLight
val InfoContainer = IndigoPrimaryContainer

// === Special Purpose Colors ===
val PRGold = Color(0xFFEAB308)               // Gold for PRs
val PRGoldLight = Color(0xFFFACC15)
val PRGoldDark = Color(0xFFCA8A04)
val PRGoldContainer = Color(0xFFFEF9C3)

val CardioOrange = Color(0xFFEA580C)         // Orange for cardio
val CardioOrangeLight = Color(0xFFF97316)
val CardioContainer = Color(0xFFFFEDD5)

// === Muscle Group Colors (Refined) ===
val MuscleChest = Color(0xFFDC2626)          // Red
val MuscleBack = Color(0xFF2563EB)           // Blue
val MuscleShoulders = Color(0xFFD97706)      // Amber
val MuscleArms = Color(0xFF7C3AED)           // Violet
val MuscleLegs = Color(0xFF059669)           // Emerald
val MuscleCore = Color(0xFFCA8A04)           // Gold

// === Chart Colors ===
val ChartLine1 = IndigoPrimary
val ChartLine2 = EmeraldSecondary
val ChartLine3 = AmberTertiary
val ChartLine4 = VioletAccent
val ChartFill = IndigoPrimary.copy(alpha = 0.08f)
val ChartFillSecondary = EmeraldSecondary.copy(alpha = 0.08f)
val ChartGrid = Color(0xFFE5E5E5)

// === Interactive States ===
val Ripple = Color(0xFF000000)
val Disabled = Color(0xFFE5E5E5)
val DisabledContent = Color(0xFF9CA3AF)

// === Gradient Colors ===
val GradientStart = IndigoPrimary
val GradientMid = VioletAccent
val GradientEnd = CoralAccent

// === Legacy Aliases ===
val BluePrimary = IndigoPrimary
val BluePrimaryLight = IndigoPrimaryLight
val BluePrimaryDark = IndigoPrimaryDark
val BluePrimaryContainer = IndigoPrimaryContainer
val BluePrimarySoft = IndigoPrimarySoft

val GreenSecondary = EmeraldSecondary
val GreenSecondaryLight = EmeraldSecondaryLight
val GreenSecondaryDark = EmeraldSecondaryDark
val GreenSecondaryContainer = EmeraldSecondaryContainer

val OrangeTertiary = AmberTertiary
val OrangeTertiaryLight = AmberTertiaryLight
val OrangeTertiaryDark = AmberTertiaryDark
val OrangeTertiaryContainer = AmberTertiaryContainer

val RoseAccent = CoralAccent
val RoseAccentLight = CoralAccentLight
val RoseAccentContainer = CoralAccentContainer

val PurpleAccent = VioletAccent
val PurpleAccentLight = VioletAccentLight
val PurpleAccentContainer = VioletAccentContainer

// More legacy aliases for backward compatibility
val PrimaryBright = IndigoPrimary
val PrimaryLight = IndigoPrimaryLight
val PrimaryDark = IndigoPrimaryDark
val PrimaryContainer = IndigoPrimaryContainer
val PrimarySoft = IndigoPrimarySoft
val SecondaryBright = EmeraldSecondary
val SecondaryLight = EmeraldSecondaryLight
val SecondaryDark = EmeraldSecondaryDark
val SecondaryContainer = EmeraldSecondaryContainer
val SecondarySoft = EmeraldSecondaryLight
val TertiaryBright = AmberTertiary
val TertiaryLight = AmberTertiaryLight
val TertiaryDark = AmberTertiaryDark
val TertiaryContainer = AmberTertiaryContainer
val AccentPink = CoralAccent
val AccentPinkLight = CoralAccentLight
val AccentPurple = VioletAccent
val AccentPurpleLight = VioletAccentLight
val CoralPrimary = IndigoPrimary
val CoralPrimaryLight = IndigoPrimaryLight
val CoralPrimaryDark = IndigoPrimaryDark
val CoralPrimaryContainer = IndigoPrimaryContainer
val TealSecondary = EmeraldSecondary
val TealSecondaryLight = EmeraldSecondaryLight
val TealSecondaryDark = EmeraldSecondaryDark
val TealSecondaryContainer = EmeraldSecondaryContainer
val AmberTertiaryOld = AmberTertiary
val CardioBlue = CardioOrange
val CardioBlueLight = CardioOrangeLight
val CardioBlueContainer = CardioContainer
val RedPrimary = IndigoPrimary
val RedPrimaryVariant = IndigoPrimaryLight
val RedPrimaryDark = IndigoPrimaryDark
val RedPrimaryContainer = IndigoPrimaryContainer
val GreenSecondaryVariant = EmeraldSecondaryLight
val OrangeTertiaryVariant = AmberTertiaryLight
val CoralPrimaryMuted = TextSecondary
val TealSecondaryMuted = EmeraldSecondaryDark
