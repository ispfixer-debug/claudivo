// RegistrationScreen - Client registration with invitation token
package com.vito.client.ui.registration

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = hiltViewModel(),
    onRegistered: () -> Unit,
) {
    val typography = VitoTypography
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.events.collect { if (it is RegistrationEvent.Registered) onRegistered() }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = VitoSpacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(VitoSpacing.xxl))
        Text(
            text = stringResource(R.string.create_account),
            style = typography.headlineMedium,
        )
        Spacer(modifier = Modifier.height(VitoSpacing.xs))
        Text(
            text = stringResource(R.string.create_account_subtitle),
            style = typography.bodyMedium,
            color = VitoTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(VitoSpacing.xl))
        VitoTextField(
            value = s.username,
            onValueChange = { viewModel.updateUsername(it) },
            label = stringResource(R.string.username_label),
            hint = stringResource(R.string.username_placeholder),
            enabled = !s.isLoading,
            modifier = Modifier.fillMaxWidth(),
            isError = s.usernameError != null,
        )
        s.usernameError?.let { error ->
            Text(error, style = typography.bodySmall, color = VitoTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(VitoSpacing.md))
        VitoTextField(
            value = s.displayName,
            onValueChange = { viewModel.updateDisplayName(it) },
            label = stringResource(R.string.display_name_label),
            hint = stringResource(R.string.display_name_placeholder),
            enabled = !s.isLoading,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(VitoSpacing.md))
        VitoPinField(
            value = s.pin,
            onValueChange = { viewModel.updatePin(it) },
            length = 6,
            enabled = !s.isLoading,
            modifier = Modifier.fillMaxWidth(),
            isError = s.pinError != null,
        )
        s.pinError?.let { error ->
            Text(error, style = typography.bodySmall, color = VitoTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(VitoSpacing.xl))
        VitoButton(
            text = if (s.isLoading) stringResource(R.string.creating) else stringResource(R.string.create_account),
            onClick = { viewModel.register() },
            loading = s.isLoading,
            modifier = Modifier.fillMaxWidth(),
            size = VitoButtonSize.Large,
        )
        s.error?.let { error ->
            Spacer(modifier = Modifier.height(VitoSpacing.md))
            Text(error, style = typography.bodyMedium, color = VitoTheme.colorScheme.error)
        }
    }
}

