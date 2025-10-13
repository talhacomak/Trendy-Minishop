package com.trendy.domain.repository

import com.trendy.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    fun observeCart(): Flow<List<CartItem>>
    suspend fun setQuantity(productId: Int, quantity: Int)
    suspend fun remove(productId: Int)
    suspend fun clear()
}
