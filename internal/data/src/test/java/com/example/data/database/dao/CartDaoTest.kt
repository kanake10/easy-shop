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
package com.example.data.database.dao

import com.example.data.database.base.BaseDbTest
import com.example.data.database.entity.CartItemEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CartDaoTest : BaseDbTest() {

    private lateinit var cartDao: CartDao

    @Before
    fun setupDao() {
        cartDao = database.cartDao()
    }

    @Test
    fun testInsertAndGetCartItem() = runBlocking {
        val cartItem = CartItemEntity(
            id = 1,
            image = "image_url",
            price = 200.0,
            title = "Item 1",
            quantity = 2
        )
        cartDao.insert(cartItem)

        val fetchedItem = cartDao.getCartItem(1)
        assertEquals("Item 1", fetchedItem?.title)
        assertEquals(200.0, fetchedItem?.price)
    }

    @Test
    fun testDeleteCartItem() = runBlocking {
        val cartItem = CartItemEntity(
            id = 1,
            image = "image_url",
            price = 100.0,
            title = "Item 1",
            quantity = 1
        )
        cartDao.insert(cartItem)

        cartDao.deleteCartItem(1)
        val fetchedItem = cartDao.getCartItem(1)
        assertNull(fetchedItem)
    }

    @Test
    fun testDeleteAllCartItems() = runBlocking {
        val cartItem1 = CartItemEntity(
            id = 1,
            image = "image1",
            price = 100.0,
            title = "Item 1",
            quantity = 1
        )
        val cartItem2 = CartItemEntity(
            id = 2,
            image = "image2",
            price = 200.0,
            title = "Item 2",
            quantity = 2
        )
        cartDao.insert(listOf(cartItem1, cartItem2))

        cartDao.deleteAllCartItems()
        val cartItems = cartDao.getAllCartItems().first()

        assertTrue(cartItems.isEmpty())
    }
}
