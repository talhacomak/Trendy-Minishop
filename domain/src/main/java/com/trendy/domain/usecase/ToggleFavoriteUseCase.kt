package com.trendy.domain.usecase

import com.trendy.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repo: FavoriteRepository
) {
    suspend operator fun invoke(productId: Int) {
        val isFav = repo.isFavorite(productId).first()
        if (isFav) repo.remove(productId) else repo.add(productId)
    }
}
