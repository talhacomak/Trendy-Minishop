package com.trendy.feature.favorites

import com.trendy.domain.model.Product

data class FavoritesUiState(
    val loading: Boolean = false,
    val error: Throwable? = null,
    val products: List<Product> = emptyList()
)
