package com.trendy.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.trendy.core.database.dao.CartDao
import com.trendy.core.database.dao.FavoriteDao
import com.trendy.core.database.entities.CartItemEntity
import com.trendy.core.database.entities.FavoriteEntity

@Database(
    entities = [FavoriteEntity::class, CartItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteDao(): FavoriteDao
    abstract fun cartDao(): CartDao
}
