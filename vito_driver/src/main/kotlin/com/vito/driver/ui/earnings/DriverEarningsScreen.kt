// DriverEarningsScreen - Driver earnings dashboard
package com.vito.driver.ui.earnings

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
fun DriverEarningsScreen() {
    Column(
        modifier = Modifier.fillMaxSize().padding(VitoSpacing.screenHorizontal),
    ) {
        Text(stringResource(R.string.earnings), style = VitoTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(VitoSpacing.lg)) {
                Text(stringResource(R.string.today_earnings), style = VitoTheme.typography.bodyMedium)
                Text("$25.50", style = VitoTheme.typography.displaySmall)
            }
        }
    }
}

