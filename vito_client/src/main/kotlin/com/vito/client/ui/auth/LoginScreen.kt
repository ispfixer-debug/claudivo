package com.vito.client.ui.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vito.client.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTypography
import com.vito.design.VitoTheme
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize
import com.vito.design.component.input.VitoPinField
import com.vito.design.component.input.VitoTextField

/**
 * Login screen - username + PIN entry.
 * Per PLAN.md §23 - Client authentication: username + 6-digit PIN ONLY. No phone, no OTP, no email.
 */
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
) {
    val typography = VitoTypography
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.events.collect { if (it is LoginEvent.Success) onLoginSuccess() }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = VitoSpacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "vito",
            style = typography.displaySmall,
            color = VitoTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(VitoSpacing.xs))

        Text(
            text = stringResource(R.string.login_headline),
            style = typography.bodyMedium,
            color = VitoTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(VitoSpacing.xxl))

        VitoTextField(
            value = s.username,
            onValueChange = { viewModel.updateUsername(it) },
            label = stringResource(R.string.username_label),
            hint = stringResource(R.string.username_placeholder),
            enabled = !s.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(VitoSpacing.md))

        VitoPinField(
            value = s.pin,
            onValueChange = { viewModel.updatePin(it) },
            isError = s.error != null,
            enabled = !s.isLoading,
            length = 6,
            modifier = Modifier.fillMaxWidth(),
        )

        AnimatedVisibility(visible = s.error != null) {
            Column {
                Spacer(modifier = Modifier.height(VitoSpacing.sm))
                Text(
                    text = s.error ?: "",
                    style = typography.bodySmall,
                    color = VitoTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(VitoSpacing.xl))

        VitoButton(
            text = if (s.isLoading) stringResource(R.string.login_loading) else stringResource(R.string.login_submit),
            onClick = { viewModel.login() },
            loading = s.isLoading,
            modifier = Modifier.fillMaxWidth(),
            size = VitoButtonSize.Large,
        )

        s.forgotPin?.let { showForgot ->
            Spacer(modifier = Modifier.height(VitoSpacing.md))
            TextButton(onClick = { viewModel.showForgotPinFlow() }) {
                Text(
                    text = stringResource(R.string.forgot_pin),
                    color = VitoTheme.colorScheme.primary
                )
            }
        }
    }
}
