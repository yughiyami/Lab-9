package com.example.lab09.data.remote

import com.example.lab09.domain.Product
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApi {

    @GET("products")
    suspend fun getProducts(@Query("limit") limit: Int? = null): List<Product>

    @GET("products/{id}")
    suspend fun getProduct(@Path("id") id: Int): Product

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{category}")
    suspend fun getProductsByCategory(@Path("category") category: String): List<Product>

    @POST("products")
    suspend fun createProduct(@Body product: NewProductBody): Product

    @PUT("products/{id}")
    suspend fun updateProduct(@Path("id") id: Int, @Body product: NewProductBody): Product

    @DELETE("products/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Product
}

data class NewProductBody(
    val title: String,
    val price: Double,
    val description: String,
    val image: String,
    val category: String
)
