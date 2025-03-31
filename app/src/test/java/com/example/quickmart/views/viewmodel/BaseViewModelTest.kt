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
package com.example.quickmart.views.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.data.database.entity.ProductsEntity
import com.example.data.interfaces.ProductsRepository
import com.example.network.helper.NetworkResult
import com.example.network.models.ProductsDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

abstract class BaseViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutor: InstantTaskExecutorRule = InstantTaskExecutorRule()
}

class MainCoroutineRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

class TestProductsRepository : ProductsRepository {

    private val productsFlow = MutableStateFlow<NetworkResult<List<ProductsDto>>>(
        NetworkResult.Success(testProducts())
    )

    override fun fetchProducts(): Flow<NetworkResult<List<ProductsDto>>> {
        return productsFlow
    }

    override suspend fun saveProducts(products: List<ProductsEntity>) {
        // Mock saving behavior here if needed, e.g., updating a list or state
    }

    fun setProducts(result: NetworkResult<List<ProductsDto>>) {
        productsFlow.update { result }
    }

    // Mock data for testing purposes
    fun testProducts(): List<ProductsDto> {
        return listOf(
            ProductsDto("Shoes", "image_desc", 1, "image_url", 100.00, "AirForce"),
            ProductsDto("Clothes", "image_desc", 2, "image_url", 100.00, "AirForce")
        )
    }
}
