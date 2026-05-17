package com.vito.design.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors
import com.vito.design.VitoShapes
import com.vito.design.VitoSpacing

/**
 * Vito Button styles per DESIGN.md §12.1
 */
enum class VitoButtonStyle {
    Primary,   // primaryAccent fill, contentOnAccent text
    Secondary, // backgroundTertiary fill, contentPrimary text
    Ghost      // transparent fill, primaryAccent text
}

/**
 * Button sizes
 */
enum class VitoButtonSize {
    Large,   // height 52dp, labelLarge
    Medium,  // height 44dp, labelMedium
    Small    // height 36dp, labelSmall
}

/**
 * Vito Button - primary CTA component.
 * Per DESIGN.md §12.1
 */
@Composable
fun VitoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: VitoButtonStyle = VitoButtonStyle.Primary,
    size: VitoButtonSize = VitoButtonSize.Large,
    loading: Boolean = false,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Background color based on style and state
    val backgroundColor = when {
        !enabled -> VitoColors.backgroundQuaternary.copy(alpha = 0.38f)
        style == VitoButtonStyle.Primary -> if (isPressed) VitoColors.primaryAccentDim else VitoColors.primaryAccent
        style == VitoButtonStyle.Secondary -> VitoColors.backgroundTertiary
        else -> Color.Transparent
    }

    // Text color
    val textColor = when {
        !enabled -> VitoColors.contentTertiary
        style == VitoButtonStyle.Primary -> VitoColors.onPrimary
        style == VitoButtonStyle.Secondary -> VitoColors.contentPrimary
        else -> VitoColors.primaryAccent
    }

    // Border for secondary
    val border = when (style) {
        VitoButtonStyle.Secondary -> VitoColors.borderStrong
        else -> Color.Transparent
    }

    // Size mappings
    val height = when (size) {
        VitoButtonSize.Large -> 52.dp
        VitoButtonSize.Medium -> 44.dp
        VitoButtonSize.Small -> 36.dp
    }

    val textStyle = when (size) {
        VitoButtonSize.Large -> MaterialTheme.typography.labelLarge
        VitoButtonSize.Medium -> MaterialTheme.typography.labelMedium
        VitoButtonSize.Small -> MaterialTheme.typography.labelSmall
    }

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(VitoShapes.extraLarge))
            .background(backgroundColor, RoundedCornerShape(VitoShapes.extraLarge))
            .then(
                if (border != Color.Transparent) Modifier.background(
                    border.copy(alpha = 1f),
                    RoundedCornerShape(VitoShapes.extraLarge)
                ).padding(1.dp) else Modifier
            )
            .clip(RoundedCornerShape(VitoShapes.extraLarge))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !loading,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = VitoSpacing.lg),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = VitoSpacing.xs),
                        tint = textColor
                    )
                }
                Text(
                    text = text,
                    style = textStyle,
                    color = textColor
                )
            }
        }
    }
}