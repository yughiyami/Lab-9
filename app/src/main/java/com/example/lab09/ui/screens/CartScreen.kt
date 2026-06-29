package com.example.lab09.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.example.lab09.ui.components.EmptyView
import com.example.lab09.ui.viewmodel.CartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: CartViewModel = hiltViewModel()) {
    val items by viewModel.cartStore.items.collectAsStateWithLifecycle()
    val total = items.values.sumOf { it.product.price * it.quantity }
    val count = items.values.sumOf { it.quantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carrito ($count)") },
                actions = {
                    if (items.isNotEmpty()) {
                        TextButton(onClick = { viewModel.clear() }) { Text("Vaciar") }
                    }
                }
            )
        },
        bottomBar = {
            if (items.isNotEmpty()) {
                Surface(tonalElevation = 4.dp) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text("Total", style = MaterialTheme.typography.labelMedium)
                            Text(
                                "$ ${"%.2f".format(total)}",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Button(onClick = { viewModel.clear() }) { Text("Comprar") }
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                EmptyView("Tu carrito está vacío.")
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 6.dp)) {
                    items(items.values.toList(), key = { it.product.id }) { item ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    Modifier.size(72.dp).clip(RoundedCornerShape(12.dp)).background(Color.White),
                                    contentAlignment = Alignment.Center
                                ) {
                                    SubcomposeAsyncImage(
                                        model = item.product.imageUrl,
                                        contentDescription = item.product.title,
                                        contentScale = ContentScale.Fit,
                                        loading = { CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(24.dp)) },
                                        modifier = Modifier.fillMaxSize().padding(4.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(
                                        item.product.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        "$ ${"%.2f".format(item.product.price)}",
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { viewModel.decrement(item.product.id) }) {
                                        Icon(Icons.Filled.Remove, contentDescription = "Menos")
                                    }
                                    Text("${item.quantity}", style = MaterialTheme.typography.titleMedium)
                                    IconButton(onClick = { viewModel.add(item.product.id) }) {
                                        Icon(Icons.Filled.Add, contentDescription = "Más")
                                    }
                                    IconButton(onClick = { viewModel.remove(item.product.id) }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Quitar", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
