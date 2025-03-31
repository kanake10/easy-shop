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
import com.example.data.database.entity.ProductsEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ProductsDaoTest : BaseDbTest() {

    private lateinit var productsDao: ProductsDao

    @Before
    fun setupDao() {
        productsDao = database.productsDao()
    }

    @Test
    fun testInsertAndCacheProducts() = runBlocking {
        val product = ProductsEntity(
            id = 1,
            image = "image_url",
            price = 100.0,
            title = "Product 1",
            category = "Category 1",
            description = "Description 1"
        )

        productsDao.insert(product)
        val products = productsDao.cacheProducts().first()

        assertEquals(1, products.size)
        assertEquals("Product 1", products[0].title)
    }

    @Test
    fun testCacheProducts_emptyList() = runBlocking {
        val products = productsDao.cacheProducts().first()
        assertTrue(products.isEmpty())
    }
}
