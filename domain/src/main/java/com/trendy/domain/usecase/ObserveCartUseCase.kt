package com.trendy.domain.usecase

import com.trendy.domain.model.CartItem
import com.trendy.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCartUseCase @Inject constructor(
    private val repo: CartRepository
) {
    operator fun invoke(): Flow<List<CartItem>> = repo.observeCart()
}