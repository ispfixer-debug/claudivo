// ActiveRideScreen - Track active ride
package com.vito.client.ui.ride

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
import com.vito.design.VitoTheme
import com.vito.design.VitoTypography

@Composable
fun ActiveRideScreen(
    rideId: String,
    viewModel: ActiveRideViewModel = hiltViewModel(),
    onRideComplete: () -> Unit,
) {
    val typography = VitoTypography
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    val canCancel = viewModel.canCancel
    LaunchedEffect(rideId) { viewModel.loadRide(rideId) }
    LaunchedEffect(Unit) {
        viewModel.events.collect { if (it is RideCompletedEvent) onRideComplete() }
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(VitoSpacing.screenHorizontal),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(VitoSpacing.xxl))
        when (s.status) {
            "accepted" -> Text(stringResource(R.string.driver_enroute), style = typography.headlineMedium)
            "arriving" -> Text(stringResource(R.string.driver_arriving), style = typography.headlineMedium)
            "in_progress" -> Text(stringResource(R.string.ride_in_progress), style = typography.headlineMedium)
            "completed" -> Text(stringResource(R.string.ride_completed), style = typography.headlineMedium)
        }
        Spacer(modifier = Modifier.height(VitoSpacing.xl))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(VitoSpacing.md)) {
                Text(s.driverName ?: "", style = typography.titleMedium)
                Text(s.plateNumber ?: "", style = typography.bodyMedium)
            }
        }
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        if (canCancel && s.status != "completed") {
            OutlinedButton(
                onClick = { viewModel.cancelRide() },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.cancel_ride))
            }
        }
    }
}

