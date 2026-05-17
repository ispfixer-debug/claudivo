package com.vito.client.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vito.client.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTypography
import com.vito.design.VitoTheme
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize

/**
 * TokenGateScreen - QR code scanner for invitation-only registration.
 * Per RULE #4 - TokenGateScreen is only entry. No "Create Account" without QR token.
 */
@Composable
fun TokenGateScreen(
    viewModel: TokenGateViewModel = hiltViewModel(),
    onTokenValidated: () -> Unit,
) {
    val typography = VitoTypography
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.events.collect { if (it is TokenGateEvent.Validated) onTokenValidated() }
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
            text = stringResource(R.string.qr_gate_headline),
            style = typography.bodyMedium,
            color = VitoTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(VitoSpacing.xxl))

        // QR Scanner placeholder - real impl uses CameraX + ML Kit
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(VitoTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (s.isScanning) {
                CircularProgressIndicator()
            } else {
                Text(
                    text = stringResource(R.string.qr_scanner_placeholder),
                    style = typography.bodyMedium,
                    color = VitoTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        s.error?.let { err ->
            Spacer(modifier = Modifier.height(VitoSpacing.sm))
            Text(
                text = err,
                style = typography.bodySmall,
                color = VitoTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(VitoSpacing.xl))

        VitoButton(
            text = stringResource(R.string.qr_scan_prompt),
            onClick = { viewModel.startScanning() },
            modifier = Modifier.fillMaxWidth(),
            size = VitoButtonSize.Large,
        )

        Spacer(modifier = Modifier.height(VitoSpacing.md))

        // Manual entry fallback - per PLAN allows typing token
        OutlinedButton(
            onClick = { /* TODO: manual token entry dialog */ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(stringResource(R.string.qr_manual_entry))
        }
    }
}
