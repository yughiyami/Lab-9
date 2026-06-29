package com.example.lab09.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.lab09.ui.components.ErrorView
import com.example.lab09.ui.components.LoadingView
import com.example.lab09.ui.state.ProductDetailUiState
import com.example.lab09.ui.viewmodel.ProductDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productId: Int,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    viewModel: ProductDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val favorites by viewModel.favoritesStore.favorites.collectAsStateWithLifecycle()
    val deleted by viewModel.deleted.collectAsStateWithLifecycle()
    val snackbarHost = remember { SnackbarHostState() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(productId) { viewModel.loadProduct(productId) }
    LaunchedEffect(deleted) { if (deleted) onBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { onEdit(productId) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                is ProductDetailUiState.Loading -> LoadingView("Cargando detalle...")
                is ProductDetailUiState.Error -> ErrorView(s.message) { viewModel.loadProduct(productId) }
                is ProductDetailUiState.Success -> {
                    val p = s.product
                    val isFav = favorites.containsKey(p.id)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(260.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            SubcomposeAsyncImage(
                                model = p.imageUrl,
                                contentDescription = p.title,
                                contentScale = ContentScale.Fit,
                                loading = { CircularProgressIndicator() },
                                modifier = Modifier.fillMaxSize().padding(12.dp)
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                p.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { viewModel.toggleFavorite(p) }) {
                                Icon(
                                    if (isFav) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    contentDescription = "Favorito",
                                    tint = if (isFav) MaterialTheme.colorScheme.error
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        AssistChip(
                            onClick = {},
                            label = { Text(p.category.replaceFirstChar { it.uppercase() }) }
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "$ ${"%.2f".format(p.price)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.weight(1f))
                            p.rating?.let {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                                Spacer(Modifier.width(4.dp))
                                Text("${it.rate} (${it.count})")
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Descripción", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(p.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                viewModel.addToCart(p)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Agregar al carrito") }
                    }
                }
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Eliminar producto") },
                text = { Text("¿Seguro que querés eliminar este producto? (Llamada DELETE a la API)") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteProduct(productId)
                    }) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
                }
            )
        }
    }
}
