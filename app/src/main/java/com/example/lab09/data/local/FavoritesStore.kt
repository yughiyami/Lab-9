package com.example.lab09.data.local

import com.example.lab09.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesStore @Inject constructor() {

    private val _favorites = MutableStateFlow<Map<Int, Product>>(emptyMap())
    val favorites: StateFlow<Map<Int, Product>> = _favorites.asStateFlow()

    fun toggle(product: Product) {
        val current = _favorites.value.toMutableMap()
        if (current.containsKey(product.id)) current.remove(product.id)
        else current[product.id] = product
        _favorites.value = current
    }

    fun isFavorite(id: Int): Boolean = _favorites.value.containsKey(id)
}
