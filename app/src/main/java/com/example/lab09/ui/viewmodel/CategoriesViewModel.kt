package com.example.lab09.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lab09.data.repository.ProductRepository
import com.example.lab09.ui.state.CategoriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriesViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<CategoriesUiState>(CategoriesUiState.Loading)
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    init { load() }

    fun load() {
        _uiState.value = CategoriesUiState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = CategoriesUiState.Success(repository.getCategories())
            } catch (e: Exception) {
                _uiState.value = CategoriesUiState.Error(e.message ?: "Error al cargar categorías.")
            }
        }
    }
}
