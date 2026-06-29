package com.example.lab09.data.remote

import com.example.lab09.domain.Product
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {

    @GET("products")
    suspend fun getProducts(): List<Product>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Product

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<Product>
}
