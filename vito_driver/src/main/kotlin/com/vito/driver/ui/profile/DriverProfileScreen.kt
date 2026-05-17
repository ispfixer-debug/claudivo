package com.vito.driver.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
import com.vito.design.VitoTypography

@Composable
fun DriverProfileScreen(onSignOut: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(VitoSpacing.screenHorizontal)) {
        Text("Profile", style = VitoTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(VitoSpacing.xl))
        Button(onClick = { /* TODO: change PIN */ }, modifier = Modifier.fillMaxWidth()) { Text("Change PIN") }
        Spacer(modifier = Modifier.height(VitoSpacing.md))
        OutlinedButton(onClick = onSignOut, modifier = Modifier.fillMaxWidth()) { Text("Sign out") }
    }
}

