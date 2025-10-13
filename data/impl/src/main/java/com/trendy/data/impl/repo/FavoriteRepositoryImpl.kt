package com.trendy.data.impl.repo

import com.trendy.core.database.dao.FavoriteDao
import com.trendy.core.database.entities.FavoriteEntity
import com.trendy.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun observeFavorites(): Flow<Set<Int>> =
        favoriteDao.observeAll().map { list -> list.map { it.productId }.toSet() }

    override suspend fun add(productId: Int) {
        favoriteDao.insert(FavoriteEntity(productId))
    }

    override suspend fun remove(productId: Int) {
        favoriteDao.delete(productId)
    }

    override fun isFavorite(productId: Int): Flow<Boolean> =
        favoriteDao.isFavorite(productId)
}