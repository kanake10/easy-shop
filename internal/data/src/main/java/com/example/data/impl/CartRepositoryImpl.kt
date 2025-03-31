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

import com.example.data.database.dao.CartDao
import com.example.data.database.entity.CartItemEntity
import com.example.data.interfaces.CartRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

internal class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
) : CartRepository {

    override suspend fun addToCart(item: CartItemEntity) {
        val cartItemEntity = CartItemEntity(
            id = item.id,
            title = item.title,
            price = item.price,
            image = item.image,
            quantity = item.quantity
        )
        cartDao.insert(cartItemEntity)
    }

    override suspend fun getCartItem(itemId: Int): CartItemEntity? {
        return cartDao.getCartItem(itemId)
    }

    override fun getAllCartItems(): Flow<List<CartItemEntity>> {
        return cartDao.getAllCartItems()
    }

    override suspend fun removeCartItem(itemId: Int) {
        cartDao.deleteCartItem(itemId)
    }

    override suspend fun clearCart() {
        cartDao.deleteAllCartItems()
    }
}
