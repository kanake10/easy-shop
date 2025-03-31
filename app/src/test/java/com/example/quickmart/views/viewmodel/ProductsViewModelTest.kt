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
import app.cash.turbine.test
import com.example.data.interfaces.ProductsRepository
import com.example.network.helper.NetworkResult
import com.example.network.models.ProductsDto
import com.example.quickmart.views.viewmodels.ProductsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

@ExperimentalCoroutinesApi
class ProductsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ProductsViewModel
    private lateinit var repository: ProductsRepository
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock(ProductsRepository::class.java)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `productsState should emit success with products when repository returns success`() = runTest {
        // Arrange
        val products = listOf(
            ProductsDto("Shoes", "image_desc", 1, "image_url", 100.00, "AirForce")
        )
        val successResult = NetworkResult.Success(products)
        `when`(repository.fetchProducts()).thenReturn(flowOf(successResult))

        // Act
        viewModel = ProductsViewModel(repository)

        // Assert
        viewModel.productsState.test {
            val expectedLoading = NetworkResult.Loading(NetworkResult.LoadingState.Idle)
            assertEquals(expectedLoading, awaitItem())
            assertEquals(successResult, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
        verify(repository, times(1)).fetchProducts()
    }

//    @Test
//    fun `productsState should emit error when repository returns error`() = runTest {
//        // Arrange
//        val errorMessage = "Failed to fetch products"
//        val errorResult: NetworkResult<List<ProductsDto>> = NetworkResult.Error(errorMessage)
//        `when`(repository.fetchProducts()).thenReturn(flowOf(errorResult))
//
//        // Act
//        viewModel = ProductsViewModel(repository)
//
//        // Assert
//        viewModel.productsState.test {
//            val expectedLoading = NetworkResult.Loading(NetworkResult.LoadingState.Idle)
//            assertEquals(expectedLoading, awaitItem())
//            assertEquals(errorResult, awaitItem())
//            cancelAndConsumeRemainingEvents()
//        }
//        verify(repository, times(1)).fetchProducts()
//    }

    @Test
    fun `productsState should emit loading then success`() = runTest {
        // Arrange
        val products = listOf(
            ProductsDto("Shoes", "image_desc", 1, "image_url", 100.00, "AirForce")
        )
        val successResult = NetworkResult.Success(products)
        `when`(repository.fetchProducts()).thenReturn(flowOf(successResult))

        // Act
        viewModel = ProductsViewModel(repository)

        // Assert
        viewModel.productsState.test {
            val expectedLoading = NetworkResult.Loading(NetworkResult.LoadingState.Idle)
            assertEquals(expectedLoading, awaitItem())
            assertEquals(successResult, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
        verify(repository, times(1)).fetchProducts()
    }

    @Test
    fun `productsState should handle empty product list`() = runTest {
        // Arrange
        val emptyListResult: NetworkResult<List<ProductsDto>> = NetworkResult.Success(emptyList())
        `when`(repository.fetchProducts()).thenReturn(flowOf(emptyListResult))

        // Act
        viewModel = ProductsViewModel(repository)

        // Assert
        viewModel.productsState.test {
            val expectedLoading = NetworkResult.Loading(NetworkResult.LoadingState.Idle)
            assertEquals(expectedLoading, awaitItem())
            assertEquals(emptyListResult, awaitItem())
            cancelAndConsumeRemainingEvents()
        }
        verify(repository, times(1)).fetchProducts()
    }
}
