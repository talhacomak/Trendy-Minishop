package com.trendy.domain.usecase

import com.trendy.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val repo: CartRepository
) {
    suspend operator fun invoke(productId: Int) = repo.remove(productId)
}
