package com.vito.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Vito Shapes System.
 * Per DESIGN.md §7
 */
object VitoShapes {
    // Corner radius values as Dp
    val extraSmall: Dp = 4.dp
    val small: Dp = 8.dp
    val medium: Dp = 12.dp
    val large: Dp = 16.dp
    val extraLarge: Dp = 24.dp

    // Pre-created shapes (non-composable)
    val extraSmallShape = RoundedCornerShape(extraSmall)
    val smallShape = RoundedCornerShape(small)
    val mediumShape = RoundedCornerShape(medium)
    val largeShape = RoundedCornerShape(large)
    val extraLargeShape = RoundedCornerShape(extraLarge)

    // Bottom sheet specific (rounded top corners only)
    val bottomSheet = RoundedCornerShape(
        topStart = 20.dp,
        topEnd = 20.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    // M3 Shapes mapping
    val material3 = Shapes(
        extraSmall = extraSmallShape,
        small = smallShape,
        medium = mediumShape,
        large = largeShape,
        extraLarge = extraLargeShape
    )
}