// RideBookingViewModel - Book a ride
package com.vito.client.ui.ride

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RideBookingUiState(
    val pickup: String = "",
    val destination: String = "",
    val fareEstimate: String? = null,
    val distanceKm: Double = 0.0,
    val canRequest: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class RideCreatedEvent { data class RideCreated(val rideId: String) : RideCreatedEvent() }

@HiltViewModel
class RideBookingViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(RideBookingUiState())
    val uiState: StateFlow<RideBookingUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<RideCreatedEvent>()
    val events = _events.asSharedFlow()

    fun updatePickup(text: String) { _uiState.value = _uiState.value.copy(pickup = text) }
    fun updateDestination(text: String) { _uiState.value = _uiState.value.copy(destination = text) }

    fun requestRide() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                // Call vito-create-ride
                _events.emit(RideCreatedEvent.RideCreated("ride_placeholder"))
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

