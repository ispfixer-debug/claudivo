// AdminDashboardScreen - Admin dashboard
package com.vito.admin.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import com.vito.admin.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTypography

@Composable
fun AdminDashboardScreen() {
    val typography = VitoTypography
    Column(
        modifier = Modifier.fillMaxSize().padding(VitoSpacing.screenHorizontal),
    ) {
        Text("Dashboard", style = typography.headlineMedium)
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitoSpacing.md),
        ) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(VitoSpacing.md)) {
                    Text("Online Drivers", style = typography.bodySmall)
                    Text("12", style = typography.displaySmall)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(VitoSpacing.md)) {
                    Text("Active Rides", style = typography.bodySmall)
                    Text("5", style = typography.displaySmall)
                }
            }
        }
        Spacer(modifier = Modifier.height(VitoSpacing.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(VitoSpacing.md),
        ) {
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(VitoSpacing.md)) {
                    Text("Mart Orders", style = typography.bodySmall)
                    Text("3", style = typography.displaySmall)
                }
            }
            Card(modifier = Modifier.weight(1f)) {
                Column(modifier = Modifier.padding(VitoSpacing.md)) {
                    Text("Revenue", style = typography.bodySmall)
                    Text("$89.50", style = typography.displaySmall)
                }
            }
        }
    }
}

