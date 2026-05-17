// DriverTokenGateScreen - Driver invitation gate
package com.vito.driver.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.vito.driver.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
import com.vito.design.VitoTypography
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize

@Composable
fun DriverTokenGateScreen(
    onTokenValidated: () -> Unit,
) {
    var token by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = VitoSpacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("vito", style = VitoTheme.typography.displaySmall, color = VitoTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(VitoSpacing.xs))
        Text(stringResource(R.string.driver_invitation), style = VitoTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(VitoSpacing.xxl))
        OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text(stringResource(R.string.invitation_code)) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(VitoSpacing.xl))
        VitoButton(text = stringResource(R.string.continue_btn), onClick = { /* TODO: validate */ }, modifier = Modifier.fillMaxWidth(), size = VitoButtonSize.Large)
    }
}

