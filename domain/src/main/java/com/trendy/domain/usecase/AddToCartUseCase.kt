// path: domain/src/main/java/com/trendy/domain/usecase/AddToCartUseCase.kt
package com.trendy.domain.usecase

import com.trendy.domain.repository.CartRepository
import javax.inject.Inject

class AddToCartUseCase @Inject constructor(
    private val repo: CartRepository
) {
    /**
     * delta: eklenecek miktar (+/-)
     * currentQty: UI tarafında bilinen mevcut miktar (bilmiyorsan 0 ver)
     */
    suspend operator fun invoke(productId: Int, delta: Int = 1, currentQty: Int = 0) {
        val next = currentQty + delta
        if (next <= 0) repo.remove(productId) else repo.setQuantity(productId, next)
    }
}
