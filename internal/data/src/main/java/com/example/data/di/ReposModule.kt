/*
 * Copyright 2025 Ezra Kanake.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.data.di

import android.content.Context
import com.example.data.database.dao.CartDao
import com.example.data.database.dao.ProductsDao
import com.example.data.impl.CartRepositoryImpl
import com.example.data.impl.ProductsRepositoryImpl
import com.example.data.interfaces.CartRepository
import com.example.data.interfaces.ProductsRepository
import com.example.network.api.QuickMartApi
import com.example.network.helper.NetworkHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideCartRepository(
        cartDao: CartDao
    ): CartRepository {
        return CartRepositoryImpl(cartDao)
    }

    @Provides
    @Singleton
    fun provideProductsRepository(
        quickMartApi: QuickMartApi,
        productsDao: ProductsDao,
        networkHelper: NetworkHelper,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): ProductsRepository {
        return ProductsRepositoryImpl(quickMartApi, productsDao, networkHelper, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideNetworkChecker(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelper(context)
    }
}
