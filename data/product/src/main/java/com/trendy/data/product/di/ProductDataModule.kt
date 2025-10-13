// path: data/product/src/main/java/com/trendy/data/product/di/ProductDataModule.kt
package com.trendy.data.product.di

import com.trendy.data.product.remote.ProductApi
import com.trendy.data.product.repo.ProductRepositoryImpl
import com.trendy.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDataModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    companion object {
        @Provides
        @Singleton
        fun provideProductApi(retrofit: Retrofit): ProductApi =
            retrofit.create(ProductApi::class.java)
    }
}
