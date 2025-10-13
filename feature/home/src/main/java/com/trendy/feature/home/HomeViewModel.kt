package com.trendy.feature.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trendy.domain.usecase.AddToCartUseCase
import com.trendy.domain.usecase.GetCategoriesUseCase
import com.trendy.domain.usecase.GetProductsUseCase
import com.trendy.domain.usecase.ObserveFavoritesUseCase
import com.trendy.domain.usecase.ToggleFavoriteUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategories: GetCategoriesUseCase,
    private val getProducts: GetProductsUseCase,
    private val observeFavorites: ObserveFavoritesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val addToCart: AddToCartUseCase
) : ViewModel() {

    var homeUiState = mutableStateOf(HomeUiState())
        private set

    private var loadJob: Job? = null
    private var favsJob: Job? = null

    init {
        observeFavs()
        refresh()
    }

    fun refresh() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            homeUiState.value = homeUiState.value.copy(loading = true, error = null)
            try {
                val categories = buildList {
                    add("All")
                    addAll(getCategories().map { it.name })
                }.distinct()
                val cat = homeUiState.value.selectedCategory.takeIf { it in categories } ?: "All"
                val products = getProducts(cat.takeIf { it != "All" })
                homeUiState.value = homeUiState.value.copy(
                    loading = false,
                    categories = categories,
                    selectedCategory = cat,
                    products = products
                )
            } catch (t: Throwable) {
                homeUiState.value = homeUiState.value.copy(loading = false, error = t)
            }
        }
    }

    fun selectCategory(category: String) {
        if (category == homeUiState.value.selectedCategory) return
        homeUiState.value = homeUiState.value.copy(selectedCategory = category)
        refresh()
    }

    fun onToggleFavorite(productId: Int) {
        viewModelScope.launch { toggleFavorite(productId) }
    }

    fun onAddToCart(productId: Int) {
        // Basic usage: current quantity unknown → delta=1, currentQty=0
        viewModelScope.launch { addToCart(productId, delta = 1, currentQty = 0) }
    }

    private fun observeFavs() {
        favsJob?.cancel()
        favsJob = viewModelScope.launch {
            observeFavorites()
                .onEach { ids -> homeUiState.value = homeUiState.value.copy(favoriteIds = ids) }
                .catch { /* t -> homeUiState.value = homeUiState.value.copy(error = t) */ }
                .collect { /* no-op */ }
        }
    }
}
