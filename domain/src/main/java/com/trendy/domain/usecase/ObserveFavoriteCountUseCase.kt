package com.trendy.domain.usecase

import com.trendy.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveFavoriteCountUseCase @Inject constructor(
    private val repo: FavoriteRepository
) {
    operator fun invoke(): Flow<Int> = repo.observeFavorites().map { it.size }
}
