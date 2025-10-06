// path: domain/src/main/java/com/trendy/domain/usecase/GetCategoriesUseCase.kt
package com.trendy.domain.usecase

import com.trendy.domain.model.Category
import com.trendy.domain.repository.ProductRepository
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repo: ProductRepository
) {
    suspend operator fun invoke(): List<Category> = repo.loadCategories()
}
