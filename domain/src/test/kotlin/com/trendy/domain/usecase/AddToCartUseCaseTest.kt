package com.trendy.domain.usecase

import com.trendy.domain.repository.CartRepository
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions

class AddToCartUseCaseTest {

    private val cartRepository: CartRepository = mock()
    private val addToCartUseCase = AddToCartUseCase(cartRepository)

    @Test
    fun `when currentQty is 0 and delta is +1 then setQuantity with 1`() = runTest {
        // Given
        val productId = 42
        val currentQty = 0
        val delta = +1

        // When
        addToCartUseCase(productId = productId, delta = delta, currentQty = currentQty)

        // Then
        verify(cartRepository).setQuantity(productId, 1)
        verify(cartRepository, never()).remove(productId)
        verifyNoMoreInteractions(cartRepository)
    }

    @Test
    fun `when currentQty is 2 and delta is +3 then setQuantity with 5`() = runTest {
        // Given
        val productId = 7
        val currentQty = 2
        val delta = +3

        // When
        addToCartUseCase(productId = productId, delta = delta, currentQty = currentQty)

        // Then
        verify(cartRepository).setQuantity(productId, 5)
        verify(cartRepository, never()).remove(productId)
        verifyNoMoreInteractions(cartRepository)
    }

    @Test
    fun `when currentQty is 1 and delta is -1 then remove item`() = runTest {
        // Given
        val productId = 99
        val currentQty = 1
        val delta = -1

        // When
        addToCartUseCase(productId = productId, delta = delta, currentQty = currentQty)

        // Then
        verify(cartRepository).remove(productId)
        verify(cartRepository, never()).setQuantity(productId, 0)
        verifyNoMoreInteractions(cartRepository)
    }

    @Test
    fun `when currentQty is 0 and delta is -1 then remove item (non-positive next)`() = runTest {
        // Given
        val productId = 5
        val currentQty = 0
        val delta = -1

        // When
        addToCartUseCase(productId = productId, delta = delta, currentQty = currentQty)

        // Then
        verify(cartRepository).remove(productId)
        verify(cartRepository, never()).setQuantity(productId, 0)
        verifyNoMoreInteractions(cartRepository)
    }

    @Test
    fun `when delta is 0 then behavior depends on currentQty (gt 0 means set, le 0 means remove)`() = runTest {
        // Case A: currentQty = 3, delta = 0 -> setQuantity(3)
        val productIdA = 11
        addToCartUseCase(productId = productIdA, delta = 0, currentQty = 3)
        verify(cartRepository).setQuantity(productIdA, 3)
        verify(cartRepository, never()).remove(productIdA)

        // Case B: currentQty = 0, delta = 0 -> remove
        val productIdB = 12
        addToCartUseCase(productId = productIdB, delta = 0, currentQty = 0)
        verify(cartRepository).remove(productIdB)
        verify(cartRepository, never()).setQuantity(productIdB, 0)
    }
}
