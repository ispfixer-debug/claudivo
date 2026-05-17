// LoginViewModel - Client login
package com.vito.client.ui.auth

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

data class LoginUiState(
    val username: String = "",
    val pin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val forgotPin: Boolean? = null,
)

sealed class LoginEvent {
    object Success : LoginEvent()
}

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, error = null)
    }

    fun updatePin(pin: String) {
        _uiState.value = _uiState.value.copy(pin = pin, error = null)
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.pin.length != 6) {
            _uiState.value = state.copy(error = "Please enter username and 6-digit PIN")
            return
        }
        _uiState.value = state.copy(isLoading = true, error = null)
        
        viewModelScope.launch {
            try {
                // Call vito-login edge function
                // For now, emit success
                _events.emit(LoginEvent.Success)
                _uiState.value = _uiState.value.copy(isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Login failed")
            }
        }
    }

    fun showForgotPinFlow() {
        _uiState.value = _uiState.value.copy(forgotPin = true)
    }
}

