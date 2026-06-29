package com.example.lab09.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lab09.data.remote.NewProductBody
import com.example.lab09.ui.state.FormUiState
import com.example.lab09.ui.viewmodel.ProductFormViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    productId: Int? = null,
    onBack: () -> Unit,
    viewModel: ProductFormViewModel = hiltViewModel()
) {
    val isEdit = productId != null
    val formState by viewModel.formState.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()

    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }
    val snackbarHost = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadCategories()
        if (isEdit) {
            viewModel.loadProductIntoForm(productId!!) { body ->
                title = body.title
                price = body.price.toString()
                description = body.description
                imageUrl = body.image
                category = body.category
            }
        }
    }

    LaunchedEffect(formState) {
        when (val s = formState) {
            is FormUiState.Success -> {
                snackbarHost.showSnackbar(s.message)
                viewModel.reset()
                onBack()
            }
            is FormUiState.Error -> {
                snackbarHost.showSnackbar(s.message)
                viewModel.reset()
            }
            else -> Unit
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Editar producto" else "Nuevo producto") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = title, onValueChange = { title = it },
                label = { Text("Título") }, modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price, onValueChange = { price = it },
                label = { Text("Precio") }, modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = description, onValueChange = { description = it },
                label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = imageUrl, onValueChange = { imageUrl = it },
                label = { Text("URL de imagen") }, modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    readOnly = false,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            val loading = formState is FormUiState.Loading
            Button(
                onClick = {
                    val body = NewProductBody(
                        title = title.trim(),
                        price = price.toDoubleOrNull() ?: 0.0,
                        description = description.trim(),
                        image = imageUrl.trim(),
                        category = category.trim()
                    )
                    if (isEdit) viewModel.update(productId!!, body)
                    else viewModel.create(body)
                },
                enabled = !loading && title.isNotBlank() && price.isNotBlank() && category.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (isEdit) "Actualizar (PUT)" else "Crear (POST)")
            }

            Text(
                "Nota: FakeStoreAPI simula POST/PUT/DELETE; devuelve el objeto pero no lo persiste.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
