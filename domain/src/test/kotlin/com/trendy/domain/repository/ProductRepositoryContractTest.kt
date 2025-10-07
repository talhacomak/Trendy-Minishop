package com.trendy.domain.repository

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ProductRepositoryContractTest {
    private val fake = InMemoryProductRepository()

    @Test
    fun `initially repository is empty`() = runTest {
        assertThat(fake.products().first()).isEmpty()
    }

    @Test
    fun `saving then reading returns same items`() = runTest {
        val item = Product(id = 1, title = "X", price = 1.0, category = "c")
        fake.save(listOf(item))
        val list = fake.products().first()
        assertThat(list).containsExactly(item)
    }
}

private data class Product(val id: Int, val title: String, val price: Double, val category: String)

private class InMemoryProductRepository : ProductRepository {
    private val store = mutableListOf<Product>()
    override suspend fun save(products: List<Product>) { store.clear(); store.addAll(products) }
    override fun products() = kotlinx.coroutines.flow.flow { emit(store.toList()) }
}

private interface ProductRepository {
    suspend fun save(products: List<Product>)
    fun products(): kotlinx.coroutines.flow.Flow<List<Product>>
}