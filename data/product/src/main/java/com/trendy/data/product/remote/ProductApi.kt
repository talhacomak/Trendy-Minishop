// path: data/product/src/main/java/com/trendy/data/product/remote/ProductApi.kt
package com.trendy.data.product.remote

import com.trendy.data.product.remote.dto.ProductDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ProductApi {

    @GET("products")
    suspend fun getProducts(): List<ProductDto>

    @GET("products/categories")
    suspend fun getCategories(): List<String>

    @GET("products/category/{name}")
    suspend fun getByCategory(@Path("name") name: String): List<ProductDto>

    @GET("products/{id}")
    suspend fun getDetail(@Path("id") id: Int): ProductDto
}
