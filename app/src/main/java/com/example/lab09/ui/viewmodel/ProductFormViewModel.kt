package com.example.lab09.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab09.data.remote.NewProductBody
import com.example.lab09.data.repository.ProductRepository
import com.example.lab09.ui.state.FormUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _formState = MutableStateFlow<FormUiState>(FormUiState.Idle)
    val formState: StateFlow<FormUiState> = _formState.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    fun loadCategories() {
        viewModelScope.launch {
            runCatching { repository.getCategories() }
                .onSuccess { _categories.value = it }
        }
    }

    fun loadProductIntoForm(id: Int, onLoaded: (NewProductBody) -> Unit) {
        viewModelScope.launch {
            try {
                val p = repository.getProduct(id)
                onLoaded(
                    NewProductBody(
                        title = p.title,
                        price = p.price,
                        description = p.description,
                        image = p.imageUrl,
                        category = p.category
                    )
                )
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "Error al cargar producto.")
            }
        }
    }

    fun create(body: NewProductBody) {
        _formState.value = FormUiState.Loading
        viewModelScope.launch {
            try {
                val created = repository.createProduct(body)
                _formState.value = FormUiState.Success("Producto creado con ID ${created.id}")
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "Error al crear producto.")
            }
        }
    }

    fun update(id: Int, body: NewProductBody) {
        _formState.value = FormUiState.Loading
        viewModelScope.launch {
            try {
                val updated = repository.updateProduct(id, body)
                _formState.value = FormUiState.Success("Producto ${updated.id} actualizado.")
            } catch (e: Exception) {
                _formState.value = FormUiState.Error(e.message ?: "Error al actualizar.")
            }
        }
    }

    fun reset() { _formState.value = FormUiState.Idle }
}
