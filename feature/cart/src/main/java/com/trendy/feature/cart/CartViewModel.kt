package com.trendy.feature.cart

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trendy.domain.model.Product
import com.trendy.domain.usecase.GetProductsUseCase
import com.trendy.domain.usecase.ObserveCartUseCase
import com.trendy.domain.usecase.RemoveFromCartUseCase
import com.trendy.domain.usecase.UpdateCartQuantityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCart: ObserveCartUseCase,
    private val getProducts: GetProductsUseCase,
    private val updateQty: UpdateCartQuantityUseCase,
    private val removeFromCart: RemoveFromCartUseCase
) : ViewModel() {

    var cartUiState = mutableStateOf(CartUiState())
        private set

    private var job: Job? = null

    init {
        observeUi()
    }

    private fun observeUi() {
        job?.cancel()
        job = viewModelScope.launch {
            // Note: Since FakeStore is small, fetching all products at once is a practical solution.
            combine(
                observeCart(),                                   // Flow<List<CartItem>>
                flow { emit(getProducts(null)) } // List<Product>
            ) { cartItems, allProducts ->
                val items = cartItems.mapNotNull { ci ->
                    val p: Product? = allProducts.find { it.id == ci.productId }
                    p?.let { CartUiState.Item(product = it, quantity = ci.quantity) }
                }
                val total = items.sumOf { it.product.price * it.quantity }
                items to total
            }
                .onStart { cartUiState.value = cartUiState.value.copy(loading = true, error = null) }
                .catch { t -> cartUiState.value = cartUiState.value.copy(loading = false, error = t) }
                .collect { (items, total) ->
                    cartUiState.value = cartUiState.value.copy(loading = false, items = items, total = total, error = null)
                }
        }
    }

    fun inc(productId: Int) {
        val current = cartUiState.value.items.find { it.product.id == productId }?.quantity ?: 0
        viewModelScope.launch { updateQty(productId, current + 1) }
    }

    fun dec(productId: Int) {
        val current = cartUiState.value.items.find { it.product.id == productId }?.quantity ?: 0
        val next = current - 1
        viewModelScope.launch {
            if (next <= 0) removeFromCart(productId) else updateQty(productId, next)
        }
    }

    fun remove(productId: Int) {
        viewModelScope.launch { removeFromCart(productId) }
    }
}
