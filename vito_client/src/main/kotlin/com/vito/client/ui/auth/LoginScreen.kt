package com.vito.client.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.vito.design.VitoColors
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize
import com.vito.design.component.VitoButtonStyle

/**
 * Login screen - phone number entry.
 * Per PLAN.md §23 - Client authentication
 */
@Composable
fun LoginScreen(
    onPhoneSubmitted: (String) -> Unit,
    onSkipToHome: () -> Unit
) {
    var phone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(VitoSpacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Vito",
            style = MaterialTheme.typography.headlineLarge,
            color = VitoColors.contentPrimary
        )

        Spacer(modifier = Modifier.height(VitoSpacing.sm))

        Text(
            text = "Enter your phone number",
            style = MaterialTheme.typography.bodyMedium,
            color = VitoColors.contentSecondary
        )

        Spacer(modifier = Modifier.height(VitoSpacing.xxl))

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Phone number") },
            placeholder = { Text("+1 (555) 000-0000") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = VitoColors.primaryAccent,
                unfocusedBorderColor = VitoColors.borderStrong,
                focusedLabelColor = VitoColors.primaryAccent
            )
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(VitoSpacing.sm))
            Text(
                text = error!!,
                color = VitoColors.destructive,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(VitoSpacing.xl))

        VitoButton(
            text = if (isLoading) "Sending…" else "Continue",
            onClick = {
                if (phone.length < 10) {
                    error = "Please enter a valid phone number"
                    return@VitoButton
                }
                isLoading = true
                error = null
                onPhoneSubmitted(phone)
            },
            loading = isLoading,
            modifier = Modifier.fillMaxWidth(),
            size = VitoButtonSize.Large
        )

        Spacer(modifier = Modifier.height(VitoSpacing.md))

        // Demo skip for development
        TextButton(onClick = onSkipToHome) {
            Text(
                text = "Skip (demo)",
                color = VitoColors.contentSecondary
            )
        }
    }
}