package com.example.fitlog.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * FitLog Pro Color Palette - Light Professional Theme
 *
 * Design Philosophy:
 * - Clean, bright, professional appearance
 * - Blue as primary for trust and professionalism
 * - Green for success and progress
 * - Warm whites and soft grays for comfort
 */

// === Light Background Colors ===
val LightBackground = Color(0xFFF8F9FA)      // Soft white background
val LightSurface = Color(0xFFFFFFFF)         // Pure white cards
val LightSurfaceVariant = Color(0xFFF1F3F5)  // Slightly gray surface
val LightSurfaceHigh = Color(0xFFE9ECEF)     // Elevated/pressed
val LightSurfaceBright = Color(0xFFFFFFFF)   // Bright surface

// === Dark Background Colors (for dark mode support) ===
val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF1E1E1E)
val DarkSurfaceVariant = Color(0xFF2C2C2C)
val DarkSurfaceHigh = Color(0xFF3A3A3A)
val DarkSurfaceBright = Color(0xFF454545)

// === Primary - Professional Blue ===
val BluePrimary = Color(0xFF2563EB)          // Vibrant blue - trust, professional
val BluePrimaryLight = Color(0xFF3B82F6)     // Lighter blue
val BluePrimaryDark = Color(0xFF1D4ED8)      // Darker blue - pressed
val BluePrimaryContainer = Color(0xFFDBEAFE) // Light blue container
val BluePrimarySoft = Color(0xFF93C5FD)      // Soft blue

// === Secondary - Fresh Green (Success, Progress) ===
val GreenSecondary = Color(0xFF10B981)       // Emerald green - success
val GreenSecondaryLight = Color(0xFF34D399)  // Lighter green
val GreenSecondaryDark = Color(0xFF059669)   // Darker green
val GreenSecondaryContainer = Color(0xFFD1FAE5) // Light green container

// === Tertiary - Warm Orange (Energy, Highlights) ===
val OrangeTertiary = Color(0xFFF59E0B)       // Amber orange
val OrangeTertiaryLight = Color(0xFFFBBF24)  // Lighter orange
val OrangeTertiaryDark = Color(0xFFD97706)   // Darker orange
val OrangeTertiaryContainer = Color(0xFFFEF3C7) // Light orange container

// === Accent - Purple (Premium, Special) ===
val PurpleAccent = Color(0xFF8B5CF6)         // Violet purple
val PurpleAccentLight = Color(0xFFA78BFA)    // Lighter purple
val PurpleAccentContainer = Color(0xFFEDE9FE) // Light purple container

// === Accent - Rose (Highlights) ===
val RoseAccent = Color(0xFFF43F5E)           // Rose red
val RoseAccentLight = Color(0xFFFB7185)      // Lighter rose
val RoseAccentContainer = Color(0xFFFFE4E6)  // Light rose container

// === Text Colors - Light Theme ===
val TextPrimaryLight = Color(0xFF111827)     // Near black - primary text
val TextSecondaryLight = Color(0xFF4B5563)   // Dark gray - secondary
val TextTertiaryLight = Color(0xFF9CA3AF)    // Medium gray - hints
val TextOnPrimaryLight = Color(0xFFFFFFFF)   // White on blue buttons
val TextOnSecondaryLight = Color(0xFFFFFFFF) // White on green

// === Text Colors - Dark Theme ===
val TextPrimary = Color(0xFFFFFFFF)
val TextSecondary = Color(0xFFB8B8B8)
val TextTertiary = Color(0xFF808080)
val TextOnPrimary = Color(0xFFFFFFFF)
val TextOnSecondary = Color(0xFF000000)
val TextOnSurfaceVariant = Color(0xFFA0A0A0)

// === Outline Colors ===
val OutlineLight = Color(0xFFE5E7EB)         // Light gray border
val OutlineLightVariant = Color(0xFFD1D5DB)  // Slightly darker
val OutlineDefault = Color(0xFF4A4A4A)
val OutlineVariant = Color(0xFF333333)
val OutlineFocused = Color(0xFF666666)

// === Semantic Colors ===
val Success = GreenSecondary
val SuccessLight = GreenSecondaryLight
val SuccessContainer = GreenSecondaryContainer
val Error = Color(0xFFEF4444)                // Red for errors
val ErrorLight = Color(0xFFF87171)
val ErrorContainer = Color(0xFFFEE2E2)       // Light red container
val Warning = OrangeTertiary
val WarningLight = OrangeTertiaryLight
val WarningContainer = OrangeTertiaryContainer
val Info = BluePrimary
val InfoLight = BluePrimaryLight
val InfoContainer = BluePrimaryContainer

// === Special Purpose Colors ===
val PRGold = Color(0xFFF59E0B)               // Gold for PRs
val PRGoldLight = Color(0xFFFBBF24)
val PRGoldDark = Color(0xFFD97706)
val PRGoldContainer = Color(0xFFFEF3C7)

val CardioOrange = Color(0xFFFF6B35)         // Vibrant orange for cardio
val CardioOrangeLight = Color(0xFFFF8A5C)
val CardioContainer = Color(0xFFFFEDD5)

// === Muscle Group Colors (Bright & Distinct) ===
val MuscleChest = Color(0xFFEF4444)          // Red
val MuscleBack = Color(0xFF3B82F6)           // Blue
val MuscleShoulders = Color(0xFFF59E0B)      // Orange
val MuscleArms = Color(0xFF8B5CF6)           // Purple
val MuscleLegs = Color(0xFF10B981)           // Green
val MuscleCore = Color(0xFFF59E0B)           // Gold/Orange

// === Chart Colors ===
val ChartLine1 = BluePrimary
val ChartLine2 = GreenSecondary
val ChartLine3 = OrangeTertiary
val ChartLine4 = PurpleAccent
val ChartFill = BluePrimary.copy(alpha = 0.1f)
val ChartFillSecondary = GreenSecondary.copy(alpha = 0.1f)
val ChartGrid = Color(0xFFE5E7EB)

// === Interactive States ===
val Ripple = Color(0xFF000000)
val Disabled = Color(0xFFE5E7EB)
val DisabledContent = Color(0xFF9CA3AF)

// === Gradient Colors ===
val GradientStart = BluePrimary
val GradientMid = PurpleAccent
val GradientEnd = RoseAccent

// End of Color Palette
