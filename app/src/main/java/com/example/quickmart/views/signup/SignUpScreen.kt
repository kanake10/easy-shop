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
package com.example.quickmart.views.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.quickmart.R
import com.example.quickmart.composables.EasyShopPasswordField
import com.example.quickmart.composables.EasyShopReusableButton
import com.example.quickmart.composables.EasyShopTextField
import com.example.quickmart.composables.EasyShopWelcomeTitle
import com.example.quickmart.views.viewmodels.AuthViewModel
import com.example.quickmart.views.viewmodels.Response

@Composable
fun SignUpScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var name by remember { mutableStateOf(TextFieldValue("")) }
    var confirmPassword by remember { mutableStateOf(TextFieldValue("")) }
    var passwordVisible by remember { mutableStateOf(false) }

    val errorMessage = when (authState) {
        is Response.Failure -> (authState as Response.Failure).e?.message ?: stringResource(
            id = R.string.sign_up_failed
        )
        else -> null
    }

    LaunchedEffect(authState) {
        if (authState is Response.Success) {
            navController.navigate("products") {
                popUpTo("signUp") { inclusive = true }
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        EasyShopWelcomeTitle(
            title = stringResource(id = R.string.welcome_to_easyshop),
            subtitle = stringResource(id = R.string.sign_up_subtitle),
            iconSize = 70.dp,
            paddingBottom = 16.dp
        )

        Spacer(modifier = Modifier.height(60.dp))

        EasyShopTextField(
            value = name,
            onValueChange = { name = it },
            label = stringResource(id = R.string.name_label),
            isError = name.text.isBlank()
        )

        EasyShopTextField(
            value = email,
            onValueChange = { viewModel.onEmailChange(it) },
            label = stringResource(id = R.string.email_label),
            isError = email.text.isBlank(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        EasyShopPasswordField(
            value = password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = stringResource(id = R.string.password_label),
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = it },
            isError = password.text.isBlank()
        )

        EasyShopPasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = stringResource(id = R.string.confirm_password_label),
            passwordVisible = passwordVisible,
            onPasswordVisibilityChange = { passwordVisible = it },
            isError = confirmPassword.text.isBlank() || confirmPassword.text != password.text
        )

        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(bottom = 12.dp))
        }

        EasyShopReusableButton(
            onClick = {
                if (name.text.isBlank() || email.text.isBlank() || password.text.isBlank() || confirmPassword.text.isBlank()) {
                    viewModel._authState.value = Response.Failure(
                        Exception("All fields are required!")
                    )
                } else if (password.text != confirmPassword.text) {
                    viewModel._authState.value = Response.Failure(
                        Exception("Passwords do not match!")
                    )
                } else {
                    viewModel.signUp(email.text, password.text)
                }
            },
            text = "Sign Up",
            isLoading = isLoading
        )

        TextButton(
            onClick = { navController.navigate("signIn") },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(
                stringResource(id = R.string.already_have_account)
            )
        }
    }
}
