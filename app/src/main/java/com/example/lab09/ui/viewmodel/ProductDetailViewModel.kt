package com.example.lab09.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab09.data.repository.ProductRepository
import com.example.lab09.ui.state.ProductDetailUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductDetailUiState>(ProductDetailUiState.Loading)
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    fun loadProduct(id: Int) {
        _uiState.value = ProductDetailUiState.Loading
        viewModelScope.launch {
            try {
                val product = repository.getProduct(id)
                _uiState.value = ProductDetailUiState.Success(product)
            } catch (e: Exception) {
                _uiState.value = ProductDetailUiState.Error(
                    e.message ?: "Error al cargar el producto."
                )
            }
        }
    }
}
