package com.vito.client.ui.send

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

sealed class SendCreatedEvent {
    data class Created(val packageId: String) : SendCreatedEvent()
}

data class SendUiState(
    val isLoading: Boolean = false,
    val sender: String = "",
    val recipient: String = "",
    val pickup: String = "",
    val dropoff: String = "",
    val feeEstimate: String = "",
    val error: String? = null
)

@HiltViewModel
class SendViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SendUiState())
    val uiState: StateFlow<SendUiState> = _uiState.asStateFlow()

    private val _events = MutableStateFlow<SendCreatedEvent?>(null)
    val events: StateFlow<SendCreatedEvent?> = _events.asStateFlow()

    fun updateSender(value: String) { _uiState.value = _uiState.value.copy(sender = value) }
    fun updateRecipient(value: String) { _uiState.value = _uiState.value.copy(recipient = value) }
    fun updatePickup(value: String) { _uiState.value = _uiState.value.copy(pickup = value) }
    fun updateDropoff(value: String) { _uiState.value = _uiState.value.copy(dropoff = value) }

    fun requestSend() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        try {
            _events.value = SendCreatedEvent.Created("send_placeholder")
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
        }
    }

    val canRequest: Boolean get() = _uiState.value.dropoff.isNotBlank()
}