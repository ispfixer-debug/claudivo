package com.vito.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors
import com.vito.design.VitoShapes
import com.vito.design.VitoSpacing

/**
 * Vito Card - base surface container.
 * Per DESIGN.md §12.5
 */
@Composable
fun VitoCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val backgroundColor = VitoColors.backgroundTertiary
    val borderColor = VitoColors.borderSubtle

    Box(
        modifier = modifier
            .clip(VitoShapes.mediumShape)
            .background(backgroundColor, VitoShapes.mediumShape)
            .then(
                if (borderColor != Color.Transparent) Modifier
                    .background(borderColor.copy(alpha = 1f), VitoShapes.mediumShape)
                    .padding(1.dp)
                else Modifier
            )
            .clip(VitoShapes.mediumShape)
            .then(
                if (onClick != null) Modifier.clickable(
                    role = Role.Button,
                    onClick = onClick
                ) else Modifier
            )
    ) {
        Column(
            modifier = Modifier.padding(VitoSpacing.md)
        ) {
            content()
        }
    }
}

/**
 * Status chip variants
 */
@Composable
fun VitoStatusChip(
    text: String,
    modifier: Modifier = Modifier,
    variant: VitoStatusChipVariant = VitoStatusChipVariant.Neutral
) {
    val (backgroundColor, textColor) = when (variant) {
        VitoStatusChipVariant.Primary -> VitoColors.primaryAccentSubtle to VitoColors.primaryAccent
        VitoStatusChipVariant.Success -> VitoColors.destructiveSubtle.copy(alpha = 0.3f) to VitoColors.success
        VitoStatusChipVariant.Warning -> VitoColors.warningSubtle to VitoColors.warning
        VitoStatusChipVariant.Destructive -> VitoColors.destructiveSubtle to VitoColors.destructive
        VitoStatusChipVariant.Neutral -> VitoColors.backgroundQuaternary to VitoColors.contentSecondary
    }

    Box(
        modifier = modifier
            .clip(VitoShapes.extraSmallShape)
            .background(backgroundColor, VitoShapes.extraSmallShape)
            .padding(horizontal = VitoSpacing.sm, vertical = VitoSpacing.xxs)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}

enum class VitoStatusChipVariant {
    Primary, Success, Warning, Destructive, Neutral
}