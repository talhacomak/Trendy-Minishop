package com.trendy.domain.repository

import com.trendy.domain.model.Category
import com.trendy.domain.model.Product

interface ProductRepository {
    suspend fun loadCategories(): List<Category>
    suspend fun loadProducts(category: String?): List<Product>
    suspend fun loadDetail(id: Int): Product
}