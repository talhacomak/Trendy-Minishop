package com.trendy.domain.usecase

import com.trendy.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartQuantityUseCase @Inject constructor(
    private val repo: CartRepository
) {
    suspend operator fun invoke(productId: Int, quantity: Int) {
        if (quantity <= 0) repo.remove(productId) else repo.setQuantity(productId, quantity)
    }
}
