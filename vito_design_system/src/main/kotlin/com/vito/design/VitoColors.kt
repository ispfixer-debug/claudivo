package com.vito.design

import androidx.compose.ui.graphics.Color

/**
 * Vito Color System - the single source of truth for all colors in Vito v1.
 * Per PLAN.md §3 and DESIGN.md §3 - no hex should appear elsewhere.
 * 
 * All colors follow the Vito dark-mode aesthetic with mint (#1AE694) as primary accent.
 */
object VitoColors {

    // Background hierarchy (DESIGN.md §3.1)
    val backgroundPrimary = Color(0xFF0A0E14)
    val backgroundSecondary = Color(0xFF10151C)
    val backgroundTertiary = Color(0xFF1A2028)
    val backgroundQuaternary = Color(0xFF232B36)

    // Primary accent (DESIGN.md §3.2)
    val primaryAccent = Color(0xFF1AE694)
    val primaryAccentDim = Color(0xFF0F8A57)
    val primaryAccentSubtle = Color(0x331AE694) // 20% opacity
    val onPrimary = Color(0xFF000000)

    // Semantic colors
    val destructive = Color(0xFFFF5C5C)
    val destructiveDim = Color(0xFFCC2B2B)
    val destructiveSubtle = Color(0x33FF5C5C)
    val warning = Color(0xFFFFD166)
    val warningSubtle = Color(0x33FFD166)
    val info = Color(0xFF5BA4FB)
    val infoSubtle = Color(0x335BA4FB)
    val success = Color(0xFF1AE694) // Same as primary in dark mode

    // Content / text (DESIGN.md §3.3)
    val contentPrimary = Color(0xFFF0F4F8)
    val contentSecondary = Color(0xFF8B9EB7)
    val contentTertiary = Color(0xFF4A5C70)
    val contentOnAccent = Color(0xFF000000)
    val contentInverted = Color(0xFF0A0E14)

    // Borders (DESIGN.md §3.4)
    val borderStrong = Color(0x1FF0F4F8) // 12% opacity
    val borderSubtle = Color(0x0FF0F4F8) // 6% opacity

    // Map-specific (DESIGN.md §3.5)
    val mapRoute = Color(0xFF1AE694)
    val mapPickup = Color(0xFF1AE694)
    val mapDestination = Color(0xFFFF5C5C)
    val mapDriver = Color(0xFFF0F4F8)
    val mapMart = Color(0xFF5BA4FB)
}

/**
 * Material3 ColorScheme mapping for Vito dark theme.
 * Per DESIGN.md §3.6
 */
val VitoColorScheme = androidx.compose.material3.darkColorScheme(
    primary = VitoColors.primaryAccent,
    onPrimary = VitoColors.onPrimary,
    primaryContainer = VitoColors.primaryAccentDim,
    onPrimaryContainer = VitoColors.contentPrimary,
    error = VitoColors.destructive,
    onError = VitoColors.onPrimary,
    errorContainer = VitoColors.destructiveDim,
    onErrorContainer = VitoColors.contentPrimary,
    background = VitoColors.backgroundPrimary,
    onBackground = VitoColors.contentPrimary,
    surface = VitoColors.backgroundSecondary,
    onSurface = VitoColors.contentPrimary,
    surfaceVariant = VitoColors.backgroundTertiary,
    onSurfaceVariant = VitoColors.contentSecondary,
    outline = VitoColors.borderStrong,
    outlineVariant = VitoColors.borderSubtle,
    scrim = Color(0xCC0A0E14),
    inverseSurface = VitoColors.contentPrimary,
    inverseOnSurface = VitoColors.backgroundPrimary,
    inversePrimary = VitoColors.primaryAccentDim,
    surfaceTint = VitoColors.primaryAccent
)