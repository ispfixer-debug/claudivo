// TokenGateViewModel - QR token validation
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

data class TokenGateUiState(
    val token: String = "",
    val isLoading: Boolean = false,
    val isScanning: Boolean = false,
    var error: String? = null,
)

sealed class TokenGateEvent {
    object Validated : TokenGateEvent()
}

@HiltViewModel
class TokenGateViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(TokenGateUiState())
    val uiState: StateFlow<TokenGateUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<TokenGateEvent>()
    val events: SharedFlow<TokenGateEvent> = _events.asSharedFlow()

    fun startScanning() {
        _uiState.value = _uiState.value.copy(isScanning = true)
    }

    fun onTokenScanned(token: String) {
        _uiState.value = _uiState.value.copy(token = token, isLoading = true, isScanning = false)
        
        viewModelScope.launch {
            try {
                // Call vito-validate-token
                _events.emit(TokenGateEvent.Validated)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}

