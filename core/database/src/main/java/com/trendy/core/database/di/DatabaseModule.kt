package com.trendy.core.database.di

import android.content.Context
import androidx.room.Room
import com.trendy.core.database.AppDatabase
import com.trendy.core.database.dao.CartDao
import com.trendy.core.database.dao.FavoriteDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "trendy.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideFavoriteDao(db: AppDatabase): FavoriteDao = db.favoriteDao()
    @Provides fun provideCartDao(db: AppDatabase): CartDao = db.cartDao()
}
