// path: data/product/src/main/java/com/trendy/data/product/mapper/ProductMapper.kt
package com.trendy.data.product.mapper

import com.trendy.data.product.remote.dto.ProductDto
import com.trendy.domain.model.Product
import com.trendy.product.remote.dto.ProductDto

fun ProductDto.toDomain(): Product =
    Product(
        id = id,
        title = title,
        price = price,
        description = description,
        category = category,
        image = image,
        rating = Product.Rating(
            rate = rating?.rate ?: 0.0,
            count = rating?.count ?: 0
        )
    )
