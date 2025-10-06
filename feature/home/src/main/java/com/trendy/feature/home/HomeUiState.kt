// path: feature/home/src/main/java/com/trendy/feature/home/HomeUiState.kt
package com.trendy.feature.home

import com.trendy.domain.model.Product

data class HomeUiState(
    val loading: Boolean = false,
    val error: Throwable? = null,
    val categories: List<String> = emptyList(), // "All" dahil
    val selectedCategory: String = "All",
    val products: List<Product> = emptyList(),
    val favoriteIds: Set<Int> = emptySet()
)
