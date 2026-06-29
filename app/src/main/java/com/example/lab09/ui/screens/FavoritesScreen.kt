package com.example.lab09.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab09.ui.components.EmptyView
import com.example.lab09.ui.components.ProductCard
import com.example.lab09.ui.viewmodel.FavoritesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onProductClick: (Int) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favoritesStore.favorites.collectAsStateWithLifecycle()

    Scaffold(topBar = { TopAppBar(title = { Text("Favoritos") }) }) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            val list = favorites.values.toList()
            if (list.isEmpty()) {
                EmptyView("Aún no marcaste productos como favoritos.")
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 6.dp)) {
                    items(list, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            isFavorite = true,
                            onClick = { onProductClick(product.id) },
                            onToggleFavorite = { viewModel.toggle(product) },
                            onAddToCart = { viewModel.addToCart(product) }
                        )
                    }
                }
            }
        }
    }
}
