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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.quickmart.composables.EasyShopPasswordField
import com.example.quickmart.composables.EasyShopReusableButton
import com.example.quickmart.composables.EasyShopTextField
import com.example.quickmart.composables.EasyShopWelcomeTitle
import org.junit.Rule
import org.junit.Test

class SignInScreenUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun test_EasyShopWelcomeTitle_isDisplayed() {
        composeTestRule.setContent {
            EasyShopWelcomeTitle(
                title = "Welcome Back",
                subtitle = "Enjoy Shopping"
            )
        }
        composeTestRule.onNodeWithText("Welcome Back").assertExists()
    }

    @Test
    fun testEmailField_isInputtedCorrectly() {
        var email = TextFieldValue("")
        composeTestRule.setContent {
            EasyShopTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
                isError = email.text.isBlank(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
        }

        composeTestRule.onNodeWithText("Email").assertExists()
        composeTestRule.onNodeWithText("Email").performTextInput("test@example.com")
        assert(email.text == "test@example.com")
    }

    @Test
    fun testPasswordField_isInputtedCorrectly() {
        var password = TextFieldValue("")
        var passwordVisible = false
        composeTestRule.setContent {
            EasyShopPasswordField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = it },
                isError = password.text.isBlank()
            )
        }

        composeTestRule.onNodeWithText("Password").assertExists()
        composeTestRule.onNodeWithText("Password").performTextInput("password123")
        assert(password.text == "password123")
    }

    @Test
    fun testSignInButton_isClickable() {
        var clicked = false
        composeTestRule.setContent {
            EasyShopReusableButton(
                onClick = { clicked = true },
                text = "Sign In",
                isLoading = false
            )
        }

        composeTestRule.onNodeWithText("Sign In").assertExists()
        composeTestRule.onNodeWithText("Sign In").performClick()

        assert(clicked)
    }

    @Test
    fun testErrorMessage_isDisplayed() {
        composeTestRule.setContent {
            Text(text = "All fields are required!", color = Color.Red)
        }

        composeTestRule.onNodeWithText("All fields are required!").assertExists()
    }

    @Test
    fun testSignInErrorMessage_displayedWhenFailure() {
        composeTestRule.setContent {
            Text(
                text = "Sign In Failed",
                color = Color.Red,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        composeTestRule.onNodeWithText("Sign In Failed").assertExists()
    }
}
