// DriverHomeScreen - Driver main screen
package com.vito.driver.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vito.driver.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
import com.vito.design.VitoTypography

@Composable
fun DriverHomeScreen(
    onOnlineToggle: (Boolean) -> Unit,
    onJobRequest: () -> Unit,
) {
    var isOnline by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize().padding(VitoSpacing.screenHorizontal),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = VitoSpacing.lg),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(R.string.driver_home), style = VitoTheme.typography.headlineMedium)
            Switch(checked = isOnline, onCheckedChange = { isOnline = it; onOnlineToggle(it) })
        }
        Spacer(modifier = Modifier.weight(1f))
        if (isOnline) {
            Text(stringResource(R.string.waiting_for_jobs), style = VitoTheme.typography.bodyMedium)
        } else {
            Text(stringResource(R.string.go_online_hint), style = VitoTheme.typography.bodyMedium)
        }
    }
}

