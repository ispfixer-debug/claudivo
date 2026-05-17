package com.vito.design

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Vito Theme - wraps Material 3 with Vito's design tokens.
 * Per DESIGN.md §1 - uses Compose Material 3 as substrate, overridden with Vito tokens.
 */
@Composable
fun VitoTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = VitoColorScheme
    val typography = VitoTypography.typeScale
    val shapes = VitoShapes.material3

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

// Provide access to colors directly
object VitoTheme {
    val colorScheme: androidx.compose.material3.ColorScheme
        @Composable
        get() = MaterialTheme.colorScheme
    
    val colors: VitoColors
        @Composable
        get() = VitoColors

    val typography: VitoTypography
        @Composable
        get() = VitoTypography

    val spacing: VitoSpacing
        @Composable
        get() = VitoSpacing
}