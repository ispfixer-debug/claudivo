// RegistrationViewModel - Client registration
package com.vito.client.ui.registration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RegistrationUiState(
    val username: String = "",
    val displayName: String = "",
    val pin: String = "",
    val usernameError: String? = null,
    val pinError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
)

sealed class RegistrationEvent { object Registered : RegistrationEvent() }

@HiltViewModel
class RegistrationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(RegistrationUiState())
    val uiState: StateFlow<RegistrationUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<RegistrationEvent>()
    val events: SharedFlow<RegistrationEvent> = _events.asSharedFlow()

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, usernameError = null)
    }

    fun updateDisplayName(name: String) {
        _uiState.value = _uiState.value.copy(displayName = name)
    }

    fun updatePin(pin: String) {
        _uiState.value = _uiState.value.copy(pin = pin, pinError = null)
    }

    fun register() {
        val state = _uiState.value
        var error = false
        if (state.username.length < 3) {
            _uiState.value = state.copy(usernameError = "Username must be 3+ characters")
            error = true
        }
        if (state.pin.length != 6) {
            _uiState.value = _uiState.value.copy(pinError = "PIN must be 6 digits")
            error = true
        }
        if (error) return

        _uiState.value = state.copy(isLoading = true)
        viewModelScope.launch {
            try {
                // Call vito-register-client
                _events.emit(RegistrationEvent.Registered)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

