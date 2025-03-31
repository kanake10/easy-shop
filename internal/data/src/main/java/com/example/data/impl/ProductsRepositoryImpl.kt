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
package com.example.data.impl

import com.example.data.database.dao.ProductsDao
import com.example.data.database.entity.ProductsEntity
import com.example.data.interfaces.ProductsRepository
import com.example.network.api.QuickMartApi
import com.example.network.helper.NetworkHelper
import com.example.network.helper.NetworkResult
import com.example.network.helper.safeApiCall
import com.example.network.models.ProductsDto
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber

internal class ProductsRepositoryImpl @Inject constructor(
    private val quickMartApi: QuickMartApi,
    private val productsDao: ProductsDao,
    private val networkHelper: NetworkHelper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ProductsRepository {

    override fun fetchProducts(): Flow<NetworkResult<List<ProductsDto>>> = flow {
        if (networkHelper.isNetworkAvailable()) {
            when (val result = safeApiCall(ioDispatcher) { quickMartApi.fetchProductsList() }) {
                is NetworkResult.Success -> {
                    Timber.d("API call successful. Received ${result.data.size} products.")
                    val entities = result.data.map { it.toEntity() }
                    saveProducts(entities)
                    emit(result)
                }
                is NetworkResult.ServerError,
                is NetworkResult.NetworkError,
                is NetworkResult.UnexpectedError -> emit(result)
                is NetworkResult.Loading -> TODO()
            }
        } else {
            productsDao.cacheProducts().collect { offlineProducts ->
                val dtoList = offlineProducts.map { it.toDto() }
                Timber.d("Loaded ${dtoList.size} products from cache.")
                emit(NetworkResult.Success(dtoList))
            }
        }
    }.flowOn(ioDispatcher)

    override suspend fun saveProducts(products: List<ProductsEntity>) =
        productsDao.insert(products)
}

fun ProductsDto.toEntity(): ProductsEntity {
    return ProductsEntity(
        id = id,
        image = image,
        price = price,
        title = title,
        category = category,
        description = description
    )
}

fun ProductsEntity.toDto(): ProductsDto {
    return ProductsDto(
        id = id,
        image = image,
        price = price,
        title = title,
        category = category,
        description = description
    )
}
