package com.vito.design

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Vito Typography System - Geist Sans + Mono type scale.
 * Per DESIGN.md §4 - Geist is the primary typeface.
 * 
 * Font files should be loaded at runtime from res/font/
 * Falls back to system default if unavailable.
 */
object VitoTypography {

    // Font families - use Geist when loaded
    val sans = FontFamily.Default // Should be replaced with Geist when available
    val mono = FontFamily.Monospace // Should be replaced with Geist Mono when available

    // Direct access to type scale properties
    val displaySmall: TextStyle get() = typeScale.displaySmall
    val displayMedium: TextStyle get() = typeScale.displayMedium
    val displayLarge: TextStyle get() = typeScale.displayLarge
    val headlineMedium: TextStyle get() = typeScale.headlineMedium
    val headlineSmall: TextStyle get() = typeScale.headlineSmall
    val headlineLarge: TextStyle get() = typeScale.headlineLarge
    val titleMedium: TextStyle get() = typeScale.titleMedium
    val titleSmall: TextStyle get() = typeScale.titleSmall
    val titleLarge: TextStyle get() = typeScale.titleLarge
    val bodyMedium: TextStyle get() = typeScale.bodyMedium
    val bodySmall: TextStyle get() = typeScale.bodySmall
    val bodyLarge: TextStyle get() = typeScale.bodyLarge

    // Type scale (DESIGN.md §4.2)
    val typeScale = Typography(
        displayLarge = TextStyle(
            fontFamily = mono,
            fontSize = 57.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 64.sp,
            letterSpacing = (-0.25).sp
        ),
        displayMedium = TextStyle(
            fontFamily = mono,
            fontSize = 45.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 52.sp,
            letterSpacing = (-0.15).sp
        ),
        displaySmall = TextStyle(
            fontFamily = sans,
            fontSize = 36.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 44.sp,
            letterSpacing = (-0.125).sp
        ),
        headlineLarge = TextStyle(
            fontFamily = sans,
            fontSize = 32.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 40.sp,
            letterSpacing = (-0.25).sp
        ),
        headlineMedium = TextStyle(
            fontFamily = sans,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = sans,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 32.sp,
            letterSpacing = 0.sp
        ),
        titleLarge = TextStyle(
            fontFamily = sans,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontFamily = sans,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        titleSmall = TextStyle(
            fontFamily = sans,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = sans,
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = sans,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 20.sp,
            letterSpacing = 0.25.sp
        ),
        bodySmall = TextStyle(
            fontFamily = sans,
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 16.sp,
            letterSpacing = 0.4.sp
        ),
        labelLarge = TextStyle(
            fontFamily = sans,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp,
            letterSpacing = 0.1.sp
        ),
        labelMedium = TextStyle(
            fontFamily = sans,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        ),
        labelSmall = TextStyle(
            fontFamily = sans,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            lineHeight = 16.sp,
            letterSpacing = 0.5.sp
        )
    )

    // Convenience methods for mono usage
    fun displayLargeMono() = typeScale.displayLarge.copy(fontFamily = mono)
    fun displayMediumMono() = typeScale.displayMedium.copy(fontFamily = mono)
    fun headlineMediumMono() = typeScale.headlineMedium.copy(fontFamily = mono)
    fun balance() = typeScale.displayLarge.copy(
        fontFamily = mono,
        fontWeight = FontWeight.Bold,
        color = VitoColors.primaryAccent
    )
    fun fare() = typeScale.headlineMedium.copy(
        fontFamily = mono,
        color = VitoColors.primaryAccent
    )
}