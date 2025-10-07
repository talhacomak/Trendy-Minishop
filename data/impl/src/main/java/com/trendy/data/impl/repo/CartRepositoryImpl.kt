package com.trendy.data.impl.repo

import com.trendy.core.database.dao.CartDao
import com.trendy.core.database.entities.CartItemEntity
import com.trendy.domain.model.CartItem
import com.trendy.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override fun observeCart(): Flow<List<CartItem>> =
        cartDao.observeCart().map { list ->
            list.map { CartItem(productId = it.productId, quantity = it.quantity) }
        }

    override suspend fun setQuantity(productId: Int, quantity: Int) {
        if (quantity <= 0) {
            cartDao.remove(productId)
        } else {
            cartDao.upsert(CartItemEntity(productId = productId, quantity = quantity))
        }
    }

    override suspend fun remove(productId: Int) {
        cartDao.remove(productId)
    }

    override suspend fun clear() {
        cartDao.clearAll()
    }
}