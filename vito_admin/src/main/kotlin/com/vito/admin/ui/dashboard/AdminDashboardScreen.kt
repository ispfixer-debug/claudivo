package com.vito.admin.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors
import com.vito.design.VitoSpacing
import com.vito.design.component.VitoCard

/**
 * Admin Dashboard - KPI overview.
 * Per PLAN.md §25.1
 */
@Composable
fun AdminDashboardScreen(
    modifier: Modifier = Modifier
) {
    // KPIs - in production these subscribe to Realtime
    val liveDrivers = remember { 42 }
    val activeJobs = remember { 8 }
    val todayGmv = remember { 125000L } // $1250
    val platformFee = remember { 18750L } // $187.50

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(VitoColors.backgroundPrimary)
            .padding(VitoSpacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(VitoSpacing.md)
    ) {
        item {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                color = VitoColors.contentPrimary
            )
            Spacer(modifier = Modifier.height(VitoSpacing.md))
        }

        // KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitoSpacing.md)
            ) {
                KpiCard(
                    title = "Live drivers",
                    value = liveDrivers.toString(),
                    icon = Icons.Default.DirectionsCar,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Active jobs",
                    value = activeJobs.toString(),
                    icon = Icons.Default.LocalActivity,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitoSpacing.md)
            ) {
                KpiCard(
                    title = "Today's GMV",
                    value = "$${todayGmv / 100}",
                    icon = Icons.Default.AttachMoney,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Platform fee",
                    value = "$${platformFee / 100}",
                    icon = Icons.Default.AccountBalance,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Actions
        item {
            Spacer(modifier = Modifier.height(VitoSpacing.lg))
            Text(
                text = "Quick actions",
                style = MaterialTheme.typography.titleLarge,
                color = VitoColors.contentPrimary
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(VitoSpacing.sm)
            ) {
                QuickActionCard(
                    title = "Review KYC",
                    icon = Icons.Default.Badge,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Payouts",
                    icon = Icons.Default.Payment,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
                QuickActionCard(
                    title = "Live map",
                    icon = Icons.Default.Map,
                    onClick = { },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    VitoCard(modifier = modifier) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = VitoColors.primaryAccent,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(VitoSpacing.sm))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = VitoColors.contentPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = VitoColors.contentSecondary
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    VitoCard(onClick = onClick, modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = VitoColors.primaryAccent,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(VitoSpacing.xxs))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = VitoColors.contentPrimary
            )
        }
    }
}