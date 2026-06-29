package com.example.lab09.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lab09.data.local.CartStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    val cartStore: CartStore
) : ViewModel() {
    fun add(productId: Int) {
        cartStore.items.value[productId]?.product?.let { cartStore.add(it) }
    }
    fun decrement(productId: Int) = cartStore.decrement(productId)
    fun remove(productId: Int) = cartStore.remove(productId)
    fun clear() = cartStore.clear()
}
