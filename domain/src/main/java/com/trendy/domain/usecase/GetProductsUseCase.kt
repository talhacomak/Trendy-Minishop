package com.trendy.domain.usecase

import com.trendy.domain.model.Product
import com.trendy.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val repo: ProductRepository
) {
    suspend operator fun invoke(category: String?): List<Product> =
        repo.loadProducts(category)
}
