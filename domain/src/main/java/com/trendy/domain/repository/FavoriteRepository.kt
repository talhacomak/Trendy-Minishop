package com.trendy.domain.repository

import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun observeFavorites(): Flow<Set<Int>>
    suspend fun add(productId: Int)
    suspend fun remove(productId: Int)
    fun isFavorite(productId: Int): Flow<Boolean>
}
