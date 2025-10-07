package com.trendy.feature.cart

import com.trendy.domain.model.Product

data class CartUiState(
    val loading: Boolean = false,
    val error: Throwable? = null,
    val items: List<Item> = emptyList(),
    val total: Double = 0.0
) {
    data class Item(
        val product: Product,
        val quantity: Int
    )
}
