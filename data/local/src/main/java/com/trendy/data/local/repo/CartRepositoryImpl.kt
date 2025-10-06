// path: data/local/src/main/java/com/trendy/data/local/repo/CartRepositoryImpl.kt
package com.trendy.data.local.repo

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
        cartDao.observeCart().map { list -> list.map { CartItem(productId = it.productId, quantity = it.quantity) } }

    override suspend fun setQuantity(productId: Int, quantity: Int) {
        cartDao.upsert(CartItemEntity(productId = productId, quantity = quantity))
    }

    override suspend fun remove(productId: Int) {
        cartDao.remove(productId)
    }

    override suspend fun clear() {
        // Basit yaklaşım: tüm öğeleri çekip tek tek sil
        val current = cartDao.observeCart().map { it.map { e -> e.productId } }.firstOrNull().orEmpty()
        current.forEach { id -> cartDao.remove(id) }
    }
}
