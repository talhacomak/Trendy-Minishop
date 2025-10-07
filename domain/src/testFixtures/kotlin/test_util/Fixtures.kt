package test_util

import kotlin.random.Random

object Fixtures {
    fun product(
        id: Int = Random.nextInt(1, 999_999),
        title: String = "Demo Product",
        price: Double = 99.99,
        category: String = "default",
    ) = ProductTestModel(id, title, price, category)
}

data class ProductTestModel(
    val id: Int,
    val title: String,
    val price: Double,
    val category: String
)