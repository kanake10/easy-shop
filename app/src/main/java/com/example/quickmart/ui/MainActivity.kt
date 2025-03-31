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
package com.example.quickmart.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quickmart.theme.QuickMartTheme
import com.example.quickmart.views.cart.CartScreen
import com.example.quickmart.views.products.ProductsScreen
import com.example.quickmart.views.signin.SignInScreen
import com.example.quickmart.views.signup.SignUpScreen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickMartTheme {
                QuickMartAppHost()
            }
        }
    }
}

@Composable
fun QuickMartAppHost() {
    val navController = rememberNavController()
    val isUserLoggedIn = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }

    NavHost(
        navController = navController,
        startDestination = if (isUserLoggedIn.value) "products" else "signUp"
    ) {
        composable("signUp") { SignUpScreen(navController) }
        composable("signIn") { SignInScreen(navController) }
        composable("products") { ProductsScreen(navController) }
        composable("cartScreen") { CartScreen(navController) }
    }
}
