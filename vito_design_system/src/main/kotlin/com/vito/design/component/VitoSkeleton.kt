package com.vito.design.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors

/**
 * Vito Skeleton - shimmer loading placeholder.
 * Per DESIGN.md §12.6 and §14.1
 */
@Composable
fun VitoSkeleton(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.unit.Dp = com.vito.design.VitoShapes.medium,
    width: Dp? = null
) {
    val shimmerColors = listOf(
        VitoColors.backgroundQuaternary.copy(alpha = 0.3f),
        VitoColors.backgroundQuaternary.copy(alpha = 0.7f),
        VitoColors.backgroundQuaternary.copy(alpha = 0.3f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerOffset"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(shimmerOffset * 1000f - 300f, 0f),
        end = Offset(shimmerOffset * 1000f, 0f)
    )

    Box(
        modifier = modifier
            .then(
                if (width != null) Modifier
                    .fillMaxWidth(width.value / 100f)
                    .height(20.dp)
                else Modifier.fillMaxWidth()
            )
            .height(20.dp)
            .clip(RoundedCornerShape(shape))
            .background(brush = brush)
    )
}

/**
 * Standard skeleton shapes for common use cases
 */
@Composable
fun VitoJobCardSkeleton(modifier: Modifier = Modifier) {
    VitoSkeleton(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    )
}

@Composable
fun VitoTextLineSkeleton(
    modifier: Modifier = Modifier,
    widthFraction: Float = 0.6f
) {
    VitoSkeleton(
        modifier = modifier
            .fillMaxWidth(widthFraction)
            .height(16.dp)
    )
}