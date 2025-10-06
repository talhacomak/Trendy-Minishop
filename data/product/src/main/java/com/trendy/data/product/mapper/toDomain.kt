package com.trendy.data.product.mapper

import com.trendy.data.product.remote.dto.ProductDto
import com.trendy.domain.model.Product

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
