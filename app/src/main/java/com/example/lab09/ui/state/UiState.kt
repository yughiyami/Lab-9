package com.example.lab09.ui.state

import com.example.lab09.domain.Product

sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(
        val products: List<Product>,
        val categories: List<String>,
        val selectedCategory: String? = null
    ) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    data class Success(val product: Product) : ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}
