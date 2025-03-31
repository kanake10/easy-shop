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
package com.example.quickmart.views

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.example.quickmart.composables.EasyShopLoadingScreen
import com.example.quickmart.composables.EasyShopTopBar
import com.example.quickmart.views.products.EasyShopFloatingActionButton
import org.junit.Rule
import org.junit.Test

class ProductsScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testTopBar_isDisplayed() {
        composeTestRule.setContent {
            EasyShopTopBar(title = "EasyShop")
        }
        composeTestRule.onNodeWithText("EasyShop").assertExists()
    }

    @Test
    fun testFloatingActionButton_isClickable() {
        var clicked = false
        composeTestRule.setContent {
            EasyShopFloatingActionButton(
                onClick = { clicked = true },
                text = "View Cart"
            )
        }

        composeTestRule.onNodeWithText("View Cart").assertExists()
        composeTestRule.onNodeWithText("View Cart").performClick()

        assert(clicked)
    }

    @Test
    fun testLoadingState_isDisplayed() {
        composeTestRule.setContent {
            EasyShopLoadingScreen()
        }

        composeTestRule.onNodeWithTag("loading_screen").assertExists()
    }
}
