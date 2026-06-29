package com.example.lab09.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab09.domain.Product
import com.example.lab09.ui.components.EmptyView
import com.example.lab09.ui.components.ErrorView
import com.example.lab09.ui.components.LoadingView
import com.example.lab09.ui.components.ProductCard
import com.example.lab09.ui.viewmodel.ProductsViewModel
import com.example.lab09.data.repository.ProductRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab09.data.local.CartStore
import com.example.lab09.data.local.FavoritesStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CategoryProductsState {
    data object Loading : CategoryProductsState
    data class Success(val products: List<Product>) : CategoryProductsState
    data class Error(val message: String) : CategoryProductsState
}

@HiltViewModel
class CategoryProductsViewModel @Inject constructor(
    private val repository: ProductRepository,
    val favoritesStore: FavoritesStore,
    val cartStore: CartStore
) : ViewModel() {
    private val _state = MutableStateFlow<CategoryProductsState>(CategoryProductsState.Loading)
    val state: StateFlow<CategoryProductsState> = _state.asStateFlow()

    fun load(category: String) {
        _state.value = CategoryProductsState.Loading
        viewModelScope.launch {
            try {
                _state.value = CategoryProductsState.Success(repository.getProductsByCategory(category))
            } catch (e: Exception) {
                _state.value = CategoryProductsState.Error(e.message ?: "Error")
            }
        }
    }

    fun toggleFavorite(product: Product) = favoritesStore.toggle(product)
    fun addToCart(product: Product) = cartStore.add(product)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryProductsScreen(
    category: String,
    onProductClick: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: CategoryProductsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val favorites by viewModel.favoritesStore.favorites.collectAsStateWithLifecycle()

    LaunchedEffect(category) { viewModel.load(category) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.replaceFirstChar { it.uppercase() }) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is CategoryProductsState.Loading -> LoadingView()
                is CategoryProductsState.Error -> ErrorView(s.message) { viewModel.load(category) }
                is CategoryProductsState.Success -> {
                    if (s.products.isEmpty()) EmptyView("Sin productos en esta categoría.")
                    else LazyColumn(contentPadding = PaddingValues(vertical = 6.dp)) {
                        items(s.products, key = { it.id }) { p ->
                            ProductCard(
                                product = p,
                                isFavorite = favorites.containsKey(p.id),
                                onClick = { onProductClick(p.id) },
                                onToggleFavorite = { viewModel.toggleFavorite(p) },
                                onAddToCart = { viewModel.addToCart(p) }
                            )
                        }
                    }
                }
            }
        }
    }
}
