// DriverManagementScreen - Admin driver management
package com.vito.admin.ui.drivers

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.vito.design.VitoSpacing
import com.vito.design.VitoTypography

@Composable
fun DriverManagementScreen() {
    val typography = VitoTypography
    Column(modifier = Modifier.fillMaxSize().padding(VitoSpacing.screenHorizontal)) {
        Text("Drivers", style = typography.headlineMedium)
    }
}

