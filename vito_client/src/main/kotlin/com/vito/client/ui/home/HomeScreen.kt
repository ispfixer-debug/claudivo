package com.vito.client.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
import com.vito.design.component.VitoCard
import com.vito.design.component.VitoStatusChip
import com.vito.design.component.VitoStatusChipVariant

/**
 * Home screen - main client landing.
 * Per PLAN.md §23.1
 */
@Composable
fun HomeScreen() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VitoColors.backgroundPrimary),
        contentPadding = PaddingValues(VitoSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(VitoSpacing.lg)
    ) {
        item {
            Spacer(modifier = Modifier.height(VitoSpacing.lg))
            
            Text(
                text = "Where to?",
                style = MaterialTheme.typography.headlineLarge,
                color = VitoColors.contentPrimary
            )
        }

        item {
            // Service cards row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(VitoSpacing.md)
            ) {
                item {
                    ServiceCard(
                        title = "Ride",
                        icon = Icons.Default.DirectionsCar,
                        onClick = { }
                    )
                }
                item {
                    ServiceCard(
                        title = "Send",
                        icon = Icons.Default.LocalShipping,
                        onClick = { }
                    )
                }
                item {
                    ServiceCard(
                        title = "Mart",
                        icon = Icons.Default.ShoppingCart,
                        onClick = { }
                    )
                }
            }
        }

        item {
            // Recent trips section
            Text(
                text = "Recent trips",
                style = MaterialTheme.typography.titleLarge,
                color = VitoColors.contentPrimary
            )
        }

        item {
            EmptyTripsState()
        }
    }
}

@Composable
private fun ServiceCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    VitoCard(
        onClick = onClick,
        modifier = Modifier.size(width = 110.dp, height = 120.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = VitoColors.primaryAccent,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(VitoSpacing.sm))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = VitoColors.contentPrimary
            )
        }
    }
}

@Composable
private fun EmptyTripsState() {
    VitoCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VitoSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsCar,
                contentDescription = null,
                tint = VitoColors.contentTertiary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(VitoSpacing.md))
            Text(
                text = "No trips yet",
                style = MaterialTheme.typography.titleMedium,
                color = VitoColors.contentSecondary
            )
            Spacer(modifier = Modifier.height(VitoSpacing.xs))
            Text(
                text = "Your first trip is waiting",
                style = MaterialTheme.typography.bodyMedium,
                color = VitoColors.contentTertiary
            )
        }
    }
}