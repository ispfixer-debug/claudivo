// SendScreen - Send package booking
package com.vito.client.ui.send

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vito.client.R
import com.vito.design.VitoSpacing
import com.vito.design.VitoTheme
import com.vito.design.VitoTypography
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize
import com.vito.design.component.input.VitoTextField

@Composable
fun SendScreen(
    viewModel: SendViewModel = hiltViewModel(),
    onSendCreated: (String) -> Unit,
) {
    val typography = VitoTypography
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is SendCreatedEvent.Created) {
                onSendCreated(event.packageId)
            }
        }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = VitoSpacing.screenHorizontal),
    ) {
        Text(stringResource(R.string.send_package), style = typography.headlineMedium)
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        VitoTextField(value = s.pickup, onValueChange = { viewModel.updatePickup(it) }, label = stringResource(R.string.pickup_location), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(VitoSpacing.md))
        VitoTextField(value = s.recipient, onValueChange = { viewModel.updateRecipient(it) }, label = stringResource(R.string.recipient_name), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        if (s.feeEstimate != null) {
            Text(stringResource(R.string.delivery_fee) + ": " + s.feeEstimate, style = typography.titleMedium)
        }
        Spacer(modifier = Modifier.weight(1f))
        VitoButton(text = stringResource(R.string.request_send), onClick = { viewModel.requestSend() }, modifier = Modifier.fillMaxWidth(), size = VitoButtonSize.Large, enabled = viewModel.canRequest)
    }
}

