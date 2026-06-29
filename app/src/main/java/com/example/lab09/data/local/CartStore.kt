package com.example.lab09.data.local

import com.example.lab09.domain.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class CartItem(val product: Product, val quantity: Int)

@Singleton
class CartStore @Inject constructor() {

    private val _items = MutableStateFlow<Map<Int, CartItem>>(emptyMap())
    val items: StateFlow<Map<Int, CartItem>> = _items.asStateFlow()

    fun add(product: Product) {
        val current = _items.value.toMutableMap()
        val existing = current[product.id]
        current[product.id] = if (existing == null) CartItem(product, 1)
        else existing.copy(quantity = existing.quantity + 1)
        _items.value = current
    }

    fun decrement(productId: Int) {
        val current = _items.value.toMutableMap()
        val existing = current[productId] ?: return
        if (existing.quantity <= 1) current.remove(productId)
        else current[productId] = existing.copy(quantity = existing.quantity - 1)
        _items.value = current
    }

    fun remove(productId: Int) {
        _items.value = _items.value.toMutableMap().also { it.remove(productId) }
    }

    fun clear() {
        _items.value = emptyMap()
    }

    fun total(): Double = _items.value.values.sumOf { it.product.price * it.quantity }
    fun count(): Int = _items.value.values.sumOf { it.quantity }
}
