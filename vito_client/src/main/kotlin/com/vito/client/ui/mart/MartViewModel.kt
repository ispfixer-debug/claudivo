package com.vito.client.ui.mart

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class MartUiState(
    val isLoading: Boolean = false,
    val items: List<MartItem> = emptyList(),
    val cart: List<MartItem> = emptyList(),
    val error: String? = null
)

data class MartItem(
    val id: String,
    val name: String,
    val price: Double,
    val imageUrl: String? = null
)

@HiltViewModel
class MartViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(MartUiState())
    val uiState: StateFlow<MartUiState> = _uiState.asStateFlow()

    fun addToCart(item: MartItem) {
        _uiState.value = _uiState.value.copy(
            cart = _uiState.value.cart + item
        )
    }

    fun removeFromCart(itemId: String) {
        _uiState.value = _uiState.value.copy(
            cart = _uiState.value.cart.filter { it.id != itemId }
        )
    }
}