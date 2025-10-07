package com.trendy.data.local.mapper

import com.trendy.core.database.entities.CartItemEntity
import com.trendy.domain.model.CartItem

fun CartItemEntity.toDomain(): CartItem =
    CartItem(productId = productId, quantity = quantity)

fun CartItem.toEntity(): CartItemEntity =
    CartItemEntity(productId = productId, quantity = quantity)
