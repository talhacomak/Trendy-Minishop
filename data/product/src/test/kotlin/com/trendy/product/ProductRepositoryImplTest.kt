package com.trendy.product

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import test_util.MainDispatcherRule

class ProductRepositoryImplTest {
    @get:Rule val main = MainDispatcherRule()

    private val remote: RemoteDataSource = mock()
    private val local: LocalDataSource = mock()
    private val repo = ProductRepositoryImpl(remote, local)

    @Test
    fun `getProducts prefers local then refreshes`() = runTest {
        whenever(local.observeProducts()).thenReturn(flowOf(listOf(Product(1, "A", 10.0, "c"))))
        whenever(remote.fetchProducts()).thenReturn(listOf(Product(2, "B", 20.0, "c")))
        whenever(local.replaceAll(any())).thenAnswer { }

        repo.products().test {
            val first = awaitItem()
            assertThat(first.map { it.id }).containsExactly(1)
            cancelAndIgnoreRemainingEvents()
        }
    }
}

data class Product(val id: Int, val title: String, val price: Double, val category: String)

interface RemoteDataSource { suspend fun fetchProducts(): List<Product> }
interface LocalDataSource {
    fun observeProducts(): kotlinx.coroutines.flow.Flow<List<Product>>
    suspend fun replaceAll(items: List<Product>)
}

class ProductRepositoryImpl(
    private val remote: RemoteDataSource,
    private val local: LocalDataSource,
) {
    fun products() = local.observeProducts()
    suspend fun refresh() { local.replaceAll(remote.fetchProducts()) }
}