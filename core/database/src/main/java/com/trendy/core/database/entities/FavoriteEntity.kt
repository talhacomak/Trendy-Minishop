// path: core/database/src/main/java/com/trendy/core/database/entities/FavoriteEntity.kt
package com.trendy.core.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val productId: Int
)
