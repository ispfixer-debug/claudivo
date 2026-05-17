package com.vito.driver.ui.auth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
@Composable
fun DriverTokenGateScreen(onValidToken: (String, String, String?) -> Unit, onAlreadyHaveAccount: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(VitoSpacing.md), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Vito Driver", style = MaterialTheme.typography.displaySmall, color = VitoTheme.colorScheme.primary)
        Spacer(Modifier.height(VitoSpacing.lg))
        Text("Invitation Required", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(VitoSpacing.xl))
        Button(onClick = {}) { Text("Scan QR Code") }
    }
}
