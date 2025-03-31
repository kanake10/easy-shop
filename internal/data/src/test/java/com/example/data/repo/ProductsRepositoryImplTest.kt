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
package com.example.data.repo

import com.example.data.database.dao.ProductsDao
import com.example.data.database.entity.ProductsEntity
import com.example.data.impl.ProductsRepositoryImpl
import com.example.data.impl.toDto
import com.example.data.impl.toEntity
import com.example.network.api.QuickMartApi
import com.example.network.helper.NetworkHelper
import com.example.network.helper.NetworkResult
import com.example.network.models.ProductsDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductsRepositoryImplTest {

    private lateinit var productsRepository: ProductsRepositoryImpl
    private lateinit var quickMartApi: QuickMartApi
    private lateinit var productsDao: ProductsDao
    private lateinit var networkHelper: NetworkHelper
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    @Before
    fun setUp() {
        quickMartApi = mockk()
        productsDao = mockk()
        networkHelper = mockk()
        productsRepository = ProductsRepositoryImpl(
            quickMartApi,
            productsDao,
            networkHelper,
            ioDispatcher
        )
    }

    @Test
    fun `fetchProducts returns Success when network is available and API call succeeds`() = runTest {
        val productsDto = listOf(
            ProductsDto("Shoes", "image_desc", 1, "image_url", 100.00, "AirForce")
        )
        val networkResult = NetworkResult.Success(productsDto)
        val productsEntity = productsDto.map { it.toEntity() }

        coEvery { networkHelper.isNetworkAvailable() } returns true
        coEvery { quickMartApi.fetchProductsList() } returns productsDto
        coEvery { productsDao.insert(productsEntity) } returns Unit

        val result = productsRepository.fetchProducts().first()

        assertTrue(result is NetworkResult.Success)
        assertEquals(productsDto, (result as NetworkResult.Success).data)
        coVerify { productsDao.insert(productsEntity) }
    }

    @Test
    fun `fetchProducts returns cached products when network is unavailable`() = runTest {
        val cachedProducts = listOf(
            ProductsEntity("image_url", 100.00, "Jacket", "Clothes", "jacket_cloth", 1)
        )
        val cachedDto = cachedProducts.map { it.toDto() }

        coEvery { networkHelper.isNetworkAvailable() } returns false
        coEvery { productsDao.cacheProducts() } returns flowOf(cachedProducts)

        val result = productsRepository.fetchProducts().first()

        assertTrue(result is NetworkResult.Success)
        assertEquals(cachedDto, (result as NetworkResult.Success).data)
    }

    @Test
    fun `saveProducts saves the products to the database`() = runTest {
        val productsEntity = listOf(
            ProductsEntity("image_url", 100.00, "Jacket", "Clothes", "jacket_cloth", 1)
        )
        coEvery { productsDao.insert(productsEntity) } returns Unit

        coEvery { productsDao.cacheProducts() } returns flowOf(productsEntity)

        productsRepository.saveProducts(productsEntity)

        coVerify { productsDao.insert(productsEntity) }

        val savedProducts = productsDao.cacheProducts().first()
        assert(savedProducts.containsAll(productsEntity))
    }
}
