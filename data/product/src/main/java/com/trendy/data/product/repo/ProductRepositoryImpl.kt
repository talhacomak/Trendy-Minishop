// path: data/product/src/main/java/com/trendy/data/product/repo/ProductRepositoryImpl.kt
package com.trendy.data.product.repo

import com.trendy.data.product.mapper.toDomain
import com.trendy.data.product.remote.ProductApi
import com.trendy.domain.model.Category
import com.trendy.domain.model.Product
import com.trendy.domain.repository.ProductRepository
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi
) : ProductRepository {

    override suspend fun loadCategories(): List<Category> =
        api.getCategories().map { Category(it) }

    override suspend fun loadProducts(category: String?): List<Product> =
        if (category.isNullOrBlank()) {
            api.getProducts().map { it.toDomain() }
        } else {
            api.getByCategory(category).map { it.toDomain() }
        }

    override suspend fun loadDetail(id: Int): Product =
        api.getDetail(id).toDomain()
}
