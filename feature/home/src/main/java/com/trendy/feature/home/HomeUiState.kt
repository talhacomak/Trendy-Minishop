package com.trendy.feature.home

import com.trendy.domain.model.Product

data class HomeUiState(
    val loading: Boolean = false,
    val error: Throwable? = null,
    val categories: List<String> = emptyList(), // with "All"
    val selectedCategory: String = "All",
    val products: List<Product> = emptyList(),
    val favoriteIds: Set<Int> = emptySet()
)
