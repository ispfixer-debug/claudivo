package com.vito.client.ui.auth
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
@Composable
fun TokenGateScreen(onValidToken: (String, String, String?) -> Unit, onAlreadyHaveAccount: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(VitoSpacing.md), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Vito", style = MaterialTheme.typography.displaySmall, color = VitoTheme.colorScheme.primary)
        Spacer(Modifier.height(VitoSpacing.lg))
        Text("Invitation Required", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(VitoSpacing.sm))
        Text("Scan a Vito invitation QR code", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(VitoSpacing.xl))
        Button(onClick = {}) { Text("Scan QR Code") }
        Spacer(Modifier.height(VitoSpacing.md))
        TextButton(onClick = onAlreadyHaveAccount) { Text("Have account? Log in") }
    }
}
