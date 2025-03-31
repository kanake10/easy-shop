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
package com.example.network.api

import com.example.network.models.ProductsDto
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuickMartApiTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: QuickMartApi

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuickMartApi::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `fetchProductsList returns expected data`() = runBlocking {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
                [
                    {
                        "id": 1,
                        "title": "Sample Product",
                        "price": 19.99,
                        "category": "Electronics",
                        "description": "A great product",
                        "image": "https://example.com/image.jpg"
                    }
                ]
                """
            )
        mockWebServer.enqueue(mockResponse)

        val response = api.fetchProductsList()

        val expected = listOf(
            ProductsDto(
                id = 1,
                title = "Sample Product",
                price = 19.99,
                category = "Electronics",
                description = "A great product",
                image = "https://example.com/image.jpg"
            )
        )

        assertEquals(expected, response)
    }
}
