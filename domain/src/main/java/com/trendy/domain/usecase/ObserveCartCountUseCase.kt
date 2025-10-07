package com.trendy.domain.usecase

import com.trendy.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveCartCountUseCase @Inject constructor(
    private val repo: CartRepository
) {
    operator fun invoke(): Flow<Int> = repo.observeCart().map { list ->
        list.sumOf { it.quantity }
    }
}
