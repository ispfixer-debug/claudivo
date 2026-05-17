// RideBookingScreen - Book a ride
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
import com.vito.design.component.VitoButton
import com.vito.design.component.VitoButtonSize
import com.vito.design.component.input.VitoTextField

@Composable
fun RideBookingScreen(
    viewModel: RideBookingViewModel = hiltViewModel(),
    onRideCreated: (String) -> Unit,
) {
    val typography = VitoTypography
    val s by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is RideCreatedEvent.RideCreated) {
                onRideCreated(event.rideId)
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = VitoSpacing.screenHorizontal),
    ) {
        Text(stringResource(R.string.where_to), style = typography.headlineMedium)
        Spacer(modifier = Modifier.height(VitoSpacing.lg))
        VitoTextField(
            value = s.pickup,
            onValueChange = { viewModel.updatePickup(it) },
            label = stringResource(R.string.pickup_location),
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(VitoSpacing.md))
        VitoTextField(
            value = s.destination,
            onValueChange = { viewModel.updateDestination(it) },
            label = stringResource(R.string.destination),
            modifier = Modifier.fillMaxWidth(),
        )
        if (s.fareEstimate != null) {
            Spacer(modifier = Modifier.height(VitoSpacing.lg))
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(VitoSpacing.md)) {
                    Text(stringResource(R.string.estimated_fare))
                    Text(s.fareEstimate!!, style = typography.titleLarge)
                    Text(stringResource(R.string.distance_format, s.distanceKm), style = typography.bodySmall)
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        VitoButton(
            text = stringResource(R.string.request_ride),
            onClick = { viewModel.requestRide() },
            modifier = Modifier.fillMaxWidth(),
            size = VitoButtonSize.Large,
            enabled = s.canRequest,
        )
    }
}

