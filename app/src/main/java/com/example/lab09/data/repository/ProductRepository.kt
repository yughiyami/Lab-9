package com.example.lab09.data.repository

import com.example.lab09.data.remote.NewProductBody
import com.example.lab09.data.remote.ProductApi
import com.example.lab09.domain.Product
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val api: ProductApi
) {
    suspend fun getProducts(): List<Product> = api.getProducts()
    suspend fun getProduct(id: Int): Product = api.getProduct(id)
    suspend fun getCategories(): List<String> = api.getCategories()
    suspend fun getProductsByCategory(category: String): List<Product> =
        api.getProductsByCategory(category)

    suspend fun createProduct(body: NewProductBody): Product = api.createProduct(body)
    suspend fun updateProduct(id: Int, body: NewProductBody): Product = api.updateProduct(id, body)
    suspend fun deleteProduct(id: Int): Product = api.deleteProduct(id)
}
