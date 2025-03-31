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

import com.example.data.database.dao.CartDao
import com.example.data.database.entity.CartItemEntity
import com.example.data.impl.CartRepositoryImpl
import com.example.data.interfaces.CartRepository
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class CartRepositoryImplTest {

    private lateinit var cartRepository: CartRepository
    private lateinit var cartDao: CartDao

    @Before
    fun setUp() {
        cartDao = mockk(relaxed = true)
        cartRepository = CartRepositoryImpl(cartDao)
    }

    @Test
    fun `test addToCart inserts cart item into the database`() = runTest {
        val cartItem = CartItemEntity(
            id = 1,
            title = "Test Item",
            price = 100.0,
            image = "test_image.jpg",
            quantity = 1
        )
        cartRepository.addToCart(cartItem)
        coVerify { cartDao.insert(cartItem) }
    }

    @Test
    fun `test getCartItem returns the correct item`() = runTest {
        val cartItem = CartItemEntity(
            id = 1,
            title = "Test Item",
            price = 100.0,
            image = "test_image.jpg",
            quantity = 1
        )
        coEvery { cartDao.getCartItem(1) } returns cartItem
        val result = cartRepository.getCartItem(1)

        assertNotNull(result)
        assertEquals(cartItem, result)
    }

    @Test
    fun `test getCartItem returns null if item not found`() = runTest {
        coEvery { cartDao.getCartItem(1) } returns null

        val result = cartRepository.getCartItem(1)

        assertNull(result)
    }

    @Test
    fun `test getAllCartItems returns list of items`() = runTest {
        val cartItems = listOf(
            CartItemEntity("image+url", 200.00, "Jacket", 1, 1),
            CartItemEntity("image+url", 200.00, "Jacket", 1, 2)
        )
        every { cartDao.getAllCartItems() } returns flowOf(cartItems)

        val result = cartRepository.getAllCartItems().first()

        assertEquals(cartItems, result)
    }

    @Test
    fun `test removeCartItem deletes the item from the cart`() = runTest {
        val itemId = 1

        cartRepository.removeCartItem(itemId)

        coVerify { cartDao.deleteCartItem(itemId) }
    }

    @Test
    fun `test clearCart deletes all items from the cart`() = runTest {
        cartRepository.clearCart()

        coVerify { cartDao.deleteAllCartItems() }
    }
}
