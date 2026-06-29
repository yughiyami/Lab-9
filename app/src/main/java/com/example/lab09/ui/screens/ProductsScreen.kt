package com.example.lab09.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab09.ui.components.CategoryChips
import com.example.lab09.ui.components.EmptyView
import com.example.lab09.ui.components.ErrorView
import com.example.lab09.ui.components.LoadingView
import com.example.lab09.ui.components.ProductCard
import com.example.lab09.ui.components.ProductSearchBar
import com.example.lab09.ui.state.ProductsUiState
import com.example.lab09.ui.viewmodel.ProductsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    onProductClick: (Int) -> Unit,
    onAddProduct: () -> Unit,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites by viewModel.favoritesStore.favorites.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Productos") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar producto")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is ProductsUiState.Loading -> LoadingView()
                is ProductsUiState.Error -> ErrorView(s.message) { viewModel.loadProducts() }
                is ProductsUiState.Success -> Column(Modifier.fillMaxSize()) {
                    ProductSearchBar(
                        query = s.query,
                        onQueryChange = viewModel::onQueryChange
                    )
                    CategoryChips(
                        categories = s.categories,
                        selected = s.selectedCategory,
                        onSelect = viewModel::selectCategory
                    )
                    if (s.products.isEmpty()) {
                        EmptyView("No hay productos para mostrar.")
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 6.dp, bottom = 80.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(s.products, key = { it.id }) { product ->
                                ProductCard(
                                    product = product,
                                    isFavorite = favorites.containsKey(product.id),
                                    onClick = { onProductClick(product.id) },
                                    onToggleFavorite = { viewModel.toggleFavorite(product) },
                                    onAddToCart = { viewModel.addToCart(product) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
