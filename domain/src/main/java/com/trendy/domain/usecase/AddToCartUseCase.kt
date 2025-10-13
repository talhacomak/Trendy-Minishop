package com.trendy.domain.usecase

import com.trendy.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repo: CartRepository
) {
    /**
     * delta: to add quantity (+/-)
     * currentQty: known quantity at UI side (put 0 if you don't know)
     */
    suspend operator fun invoke(productId: Int, delta: Int = 1, currentQty: Int = 0) {
        val next = currentQty + delta
        if (next <= 0) repo.remove(productId) else repo.setQuantity(productId, next)
    }
}
