package com.example.lab09.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab09.data.repository.ProductRepository
import com.example.lab09.ui.state.ProductsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductsViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        _uiState.value = ProductsUiState.Loading
        viewModelScope.launch {
            try {
                val products = repository.getProducts()
                val categories = repository.getCategories()
                _uiState.value = ProductsUiState.Success(
                    products = products,
                    categories = categories
                )
            } catch (e: IOException) {
                _uiState.value = ProductsUiState.Error(
                    "Sin conexión a internet. Verifica tu red."
                )
            } catch (e: Exception) {
                _uiState.value = ProductsUiState.Error(
                    e.message ?: "Error desconocido al cargar productos."
                )
            }
        }
    }

    fun selectCategory(category: String?) {
        val current = _uiState.value as? ProductsUiState.Success ?: return
        _uiState.value = ProductsUiState.Loading
        viewModelScope.launch {
            try {
                val products = if (category == null) {
                    repository.getProducts()
                } else {
                    repository.getProductsByCategory(category)
                }
                _uiState.value = current.copy(
                    products = products,
                    selectedCategory = category
                )
            } catch (e: Exception) {
                _uiState.value = ProductsUiState.Error(
                    e.message ?: "Error al filtrar la categoría."
                )
            }
        }
    }
}
