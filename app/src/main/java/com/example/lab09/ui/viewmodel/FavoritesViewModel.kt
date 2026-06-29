package com.example.lab09.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.lab09.data.local.CartStore
import com.example.lab09.data.local.FavoritesStore
import com.example.lab09.domain.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    val favoritesStore: FavoritesStore,
    val cartStore: CartStore
) : ViewModel() {
    fun toggle(product: Product) = favoritesStore.toggle(product)
    fun addToCart(product: Product) = cartStore.add(product)
}
