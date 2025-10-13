package com.trendy.domain.usecase

import com.trendy.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoritesUseCase @Inject constructor(
    private val repo: FavoriteRepository
) {
    operator fun invoke(): Flow<Set<Int>> = repo.observeFavorites()
}
