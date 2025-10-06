// path: data/local/src/main/java/com/trendy/data/local/di/LocalDataModule.kt
package com.trendy.data.local.di

import com.trendy.data.local.repo.CartRepositoryImpl
import com.trendy.data.local.repo.FavoriteRepositoryImpl
import com.trendy.domain.repository.CartRepository
import com.trendy.domain.repository.FavoriteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocalDataModule {

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl
    ): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository
}
