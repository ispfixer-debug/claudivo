package com.vito.client.ui.ride

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class RideCompletedEvent
data object RideCompleted : RideCompletedEvent()

data class ActiveRideUiState(
    val status: String = "", // "accepted", "arriving", "in_progress", "completed"
    val driverName: String? = null,
    val plateNumber: String? = null,
    val etaMinutes: Int = 0,
    val fare: String = ""
)

@HiltViewModel
class ActiveRideViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(ActiveRideUiState())
    val uiState: StateFlow<ActiveRideUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<RideCompletedEvent?>(null)
    val events: StateFlow<RideCompletedEvent?> = _events.asStateFlow()

    fun loadRide(rideId: String) {
        // TODO: Load ride from backend
    }

    fun cancelRide() {
        _events.value = RideCompleted
    }

    val canCancel: Boolean get() = _uiState.value.status != "completed"
}