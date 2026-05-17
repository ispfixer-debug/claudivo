package com.vito.design.component.input

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import com.vito.design.VitoSpacing
import com.vito.design.VitoShapes
import com.vito.design.VitoTheme
import kotlin.math.roundToInt

@Composable
fun VitoPinField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    length: Int = 6,
    isError: Boolean = false,
    enabled: Boolean = true,
    onComplete: ((String) -> Unit)? = null,
) {
    val haptic = LocalHapticFeedback.current

    // Shake animation on error
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(isError) {
        if (isError) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            repeat(3) {
                shakeOffset.animateTo(8f, tween(50, easing = LinearEasing))
                shakeOffset.animateTo(-8f, tween(50, easing = LinearEasing))
            }
            shakeOffset.animateTo(0f, tween(50))
        }
    }

    // Auto-complete when all digits filled
    LaunchedEffect(value) {
        if (value.length == length) onComplete?.invoke(value)
    }

    Box(modifier = modifier) {
        // Hidden real input — captures keyboard input
        BasicTextField(
            value = value,
            onValueChange = { input ->
                if (!enabled) return@BasicTextField
                val filtered = input.filter { it.isDigit() }.take(length)
                if (filtered != value) onValueChange(filtered)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
            ),
            modifier = Modifier
                .size(1.dp)
                .alpha(0.001f),
        )

        // Visual cells
        Row(
            modifier = Modifier.offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
            horizontalArrangement = Arrangement.spacedBy(VitoSpacing.xs),
        ) {
            repeat(length) { index ->
                PinCell(
                    isFilled = index < value.length,
                    isActive = index == value.length && enabled,
                    isError = isError,
                    enabled = enabled,
                )
            }
        }
    }
}

@Composable
private fun PinCell(
    isFilled: Boolean,
    isActive: Boolean,
    isError: Boolean,
    enabled: Boolean,
) {
    val borderColor = when {
        !enabled -> VitoTheme.colorScheme.outlineVariant
        isError  -> VitoTheme.colorScheme.error
        isActive -> VitoTheme.colorScheme.primary
        isFilled -> VitoTheme.colorScheme.outline
        else     -> VitoTheme.colorScheme.outlineVariant
    }
    val borderWidth = if (isActive || isError) 2.dp else 1.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(width = 48.dp, height = 60.dp)
            .clip(VitoShapes.smallShape)
            .background(VitoTheme.colorScheme.surfaceVariant)
            .border(borderWidth, borderColor, RoundedCornerShape(VitoShapes.small)),
    ) {
        if (isFilled) {
            // Always obscured — show filled dot, never the digit
            Box(
                Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (enabled) VitoTheme.colorScheme.onSurface
                        else VitoTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
            )
        }
    }
}
