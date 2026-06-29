package com.example.lab09.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab09.data.local.CartStore
import com.example.lab09.data.local.FavoritesStore
import com.example.lab09.data.repository.ProductRepository
import com.example.lab09.domain.Product
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
    private val repository: ProductRepository,
    val favoritesStore: FavoritesStore,
    val cartStore: CartStore
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProductsUiState>(ProductsUiState.Loading)
    val uiState: StateFlow<ProductsUiState> = _uiState.asStateFlow()

    private var fullProducts: List<Product> = emptyList()

    init { loadProducts() }

    fun loadProducts() {
        _uiState.value = ProductsUiState.Loading
        viewModelScope.launch {
            try {
                val products = repository.getProducts()
                val categories = repository.getCategories()
                fullProducts = products
                _uiState.value = ProductsUiState.Success(
                    products = products,
                    categories = categories
                )
            } catch (e: IOException) {
                _uiState.value = ProductsUiState.Error("Sin conexión a internet. Verifica tu red.")
            } catch (e: Exception) {
                _uiState.value = ProductsUiState.Error(e.message ?: "Error desconocido.")
            }
        }
    }

    fun selectCategory(category: String?) {
        val current = _uiState.value as? ProductsUiState.Success ?: return
        viewModelScope.launch {
            try {
                val products = if (category == null) repository.getProducts()
                else repository.getProductsByCategory(category)
                fullProducts = products
                _uiState.value = current.copy(
                    products = applyQuery(products, current.query),
                    selectedCategory = category
                )
            } catch (e: Exception) {
                _uiState.value = ProductsUiState.Error(e.message ?: "Error al filtrar.")
            }
        }
    }

    fun onQueryChange(query: String) {
        val current = _uiState.value as? ProductsUiState.Success ?: return
        _uiState.value = current.copy(
            products = applyQuery(fullProducts, query),
            query = query
        )
    }

    fun toggleFavorite(product: Product) = favoritesStore.toggle(product)
    fun addToCart(product: Product) = cartStore.add(product)

    private fun applyQuery(list: List<Product>, q: String): List<Product> =
        if (q.isBlank()) list
        else list.filter { it.title.contains(q, ignoreCase = true) }
}
