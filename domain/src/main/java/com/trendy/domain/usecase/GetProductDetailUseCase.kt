package com.trendy.domain.usecase

import com.trendy.domain.model.Product
import com.trendy.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductDetailUseCase @Inject constructor(
    private val repo: ProductRepository
) {
    suspend operator fun invoke(id: Int): Product = repo.loadDetail(id)
}
