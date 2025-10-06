// path: core/database/src/main/java/com/trendy/core/database/entities/CartItemEntity.kt
package com.trendy.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartItemEntity(
    @PrimaryKey val productId: Int,
    val quantity: Int
)
