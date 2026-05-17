package com.vito.client.ui.ride

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors
import com.vito.design.VitoSpacing
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize
import com.vito.design.component.VitoButtonStyle

/**
 * Job request modal - shows fare and accepts ride.
 * Per DESIGN.md §12.3 - countdown ring, fare display
 */
@Composable
fun JobRequestModal(
    jobType: String,
    pickupAddress: String,
    destinationAddress: String?,
    estimatedFareCents: Long,
    distanceMeters: Int,
    expiresAt: Long,
    onAccept: () -> Unit,
    onDecline: () -> Unit
) {
    var remainingSeconds by remember { mutableStateOf(30) }
    
    LaunchedEffect(expiresAt) {
        while (remainingSeconds > 0) {
            kotlinx.coroutines.delay(1000)
            remainingSeconds--
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(VitoColors.backgroundPrimary.copy(alpha = 0.95f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitoSpacing.md),
            colors = CardDefaults.cardColors(
                containerColor = VitoColors.backgroundSecondary
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(VitoSpacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Job Type Chip
                AssistChip(
                    onClick = { },
                    label = { Text(jobType.uppercase()) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = VitoColors.primaryAccentSubtle,
                        labelColor = VitoColors.primaryAccent
                    )
                )

                Spacer(modifier = Modifier.height(VitoSpacing.lg))

                // Pickup
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = VitoColors.primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(VitoSpacing.sm))
                    Text(
                        text = pickupAddress,
                        style = MaterialTheme.typography.titleMedium,
                        color = VitoColors.contentPrimary
                    )
                }

                // Destination
                if (destinationAddress != null) {
                    Spacer(modifier = Modifier.height(VitoSpacing.sm))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = VitoColors.destructive,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(VitoSpacing.sm))
                        Text(
                            text = destinationAddress,
                            style = MaterialTheme.typography.titleMedium,
                            color = VitoColors.contentPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(VitoSpacing.lg))
                
                Divider(color = VitoColors.borderSubtle)

                Spacer(modifier = Modifier.height(VitoSpacing.lg))

                // Fare
                Text(
                    text = "Estimated fare",
                    style = MaterialTheme.typography.bodySmall,
                    color = VitoColors.contentSecondary
                )
                Text(
                    text = "$${estimatedFareCents / 100}.${estimatedFareCents % 100 / 10}0",
                    style = MaterialTheme.typography.displaySmall,
                    color = VitoColors.primaryAccent
                )

                Spacer(modifier = Modifier.height(VitoSpacing.sm))

                // Distance
                Text(
                    text = "${distanceMeters / 1000}.${distanceMeters % 1000 / 100} km away",
                    style = MaterialTheme.typography.bodyMedium,
                    color = VitoColors.contentSecondary
                )

                Spacer(modifier = Modifier.height(VitoSpacing.xl))

                // Countdown ring
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { remainingSeconds / 30f },
                        modifier = Modifier.size(72.dp),
                        color = if (remainingSeconds <= 10) VitoColors.destructive else VitoColors.primaryAccent,
                        trackColor = VitoColors.backgroundQuaternary,
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = remainingSeconds.toString(),
                        style = MaterialTheme.typography.titleLarge,
                        color = if (remainingSeconds <= 10) VitoColors.destructive else VitoColors.contentPrimary
                    )
                }

                Spacer(modifier = Modifier.height(VitoSpacing.xl))

                // Accept/Decline buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(VitoSpacing.md)
                ) {
                    VitoButton(
                        text = "Decline",
                        onClick = onDecline,
                        style = VitoButtonStyle.Ghost,
                        modifier = Modifier.weight(1f),
                        size = VitoButtonSize.Medium
                    )
                    VitoButton(
                        text = "Accept",
                        onClick = onAccept,
                        style = VitoButtonStyle.Primary,
                        modifier = Modifier.weight(1f),
                        size = VitoButtonSize.Medium
                    )
                }
            }
        }
    }
}