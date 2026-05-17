package com.vito.design

import androidx.compose.ui.unit.dp

/**
 * Vito Spacing System - 4dp base unit.
 * Per DESIGN.md §5 - all spacing is a multiple of 4.
 */
object VitoSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    val huge = 48.dp
    val giant = 64.dp

    // Semantic spacing
    val screenHorizontal = md
    val cardGutter = xs
    val sectionGap = xl
    val touchTarget = 48.dp // Minimum per Material guidelines
}