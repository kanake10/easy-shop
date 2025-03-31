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
package com.example.data.database

import androidx.room.RoomDatabase
import com.example.data.database.dao.CartDao
import com.example.data.database.dao.ProductsDao
import com.example.data.database.entity.CartItemEntity
import com.example.data.database.entity.ProductsEntity

@androidx.room.Database(
    entities = [
        CartItemEntity::class,
        ProductsEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class Database : RoomDatabase() {

    abstract fun cartDao(): CartDao
    abstract fun productsDao(): ProductsDao
}
