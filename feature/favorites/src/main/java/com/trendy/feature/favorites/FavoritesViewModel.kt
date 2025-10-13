package com.trendy.feature.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trendy.domain.usecase.GetProductsUseCase
import com.trendy.domain.usecase.ObserveFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val getProducts: GetProductsUseCase,
    private val observeFavorites: ObserveFavoritesUseCase
) : ViewModel() {

    var state = androidx.compose.runtime.mutableStateOf(FavoritesUiState())
        private set

    private var loadJob: Job? = null

    init {
        observeFavProducts()
    }

    private fun observeFavProducts() {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            combine(
                // due to FakeStoreApi is small, we can fetch all products once
                kotlinx.coroutines.flow.flow { emit(getProducts(null)) },
                observeFavorites()
            ) { allProducts, favIds ->
                allProducts.filter { favIds.contains(it.id) }
            }
                .onStart { state.value = state.value.copy(loading = true, error = null) }
                .catch { t -> state.value = state.value.copy(loading = false, error = t) }
                .collect { favList ->
                    state.value = state.value.copy(loading = false, products = favList, error = null)
                }
        }
    }
}
