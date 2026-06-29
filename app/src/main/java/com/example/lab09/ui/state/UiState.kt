package com.example.lab09.ui.state

import com.example.lab09.domain.Product

sealed interface ProductsUiState {
    data object Loading : ProductsUiState
    data class Success(
        val products: List<Product>,
        val categories: List<String>,
        val selectedCategory: String? = null,
        val query: String = ""
    ) : ProductsUiState
    data class Error(val message: String) : ProductsUiState
}

sealed interface ProductDetailUiState {
    data object Loading : ProductDetailUiState
    data class Success(val product: Product) : ProductDetailUiState
    data class Error(val message: String) : ProductDetailUiState
}

sealed interface CategoriesUiState {
    data object Loading : CategoriesUiState
    data class Success(val categories: List<String>) : CategoriesUiState
    data class Error(val message: String) : CategoriesUiState
}

sealed interface FormUiState {
    data object Idle : FormUiState
    data object Loading : FormUiState
    data class Success(val message: String) : FormUiState
    data class Error(val message: String) : FormUiState
}
