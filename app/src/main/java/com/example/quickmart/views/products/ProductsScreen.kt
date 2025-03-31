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
package com.example.quickmart.views.products

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.network.helper.NetworkResult
import com.example.network.models.ProductsDto
import com.example.quickmart.composables.EasyShopErrorScreen
import com.example.quickmart.composables.EasyShopLoadingScreen
import com.example.quickmart.composables.EasyShopTopBar
import com.example.quickmart.composables.ProductCard
import com.example.quickmart.views.viewmodels.CartViewModel
import com.example.quickmart.views.viewmodels.ProductsViewModel

@Composable
fun ProductsScreen(
    navController: NavController,
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val cartViewModel: CartViewModel = hiltViewModel()
    val productsState by viewModel.productsState.collectAsState()
    val context = LocalContext.current

    BackHandler {
        (context as? Activity)?.finish()
    }

    Scaffold(
        topBar = {
            EasyShopTopBar(
                title = "EasyShop"
            )
        },
        floatingActionButton = {
            EasyShopFloatingActionButton(
                onClick = { navController.navigate("cartScreen") },
                text = "View Cart"
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (productsState) {
                is NetworkResult.Loading -> EasyShopLoadingScreen()
                is NetworkResult.Success -> {
                    val products = (productsState as NetworkResult.Success).data
                    ProductsList(
                        products = products,
                        onAddToCart = { product ->
                            cartViewModel.addToCart(
                                id = product.id,
                                title = product.title,
                                price = product.price,
                                image = product.image
                            )
                        }
                    )
                }
                is NetworkResult.ServerError -> {
                    val code = (productsState as NetworkResult.ServerError).code
                    EasyShopErrorScreen("Server Error: $code")
                }
                is NetworkResult.NetworkError -> {
                    EasyShopErrorScreen("Network error. Please check your internet connection.")
                }
                is NetworkResult.UnexpectedError -> {
                    val errorMessage = (productsState as NetworkResult.UnexpectedError).errorMessage
                        ?: "Unexpected error occurred"
                    EasyShopErrorScreen(errorMessage)
                }
                else -> {
                    Text("Unknown state")
                }
            }
        }
    }
}

@Composable
fun EasyShopFloatingActionButton(
    onClick: () -> Unit,
    text: String
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = "View Cart"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text)
        }
    }
}

@Composable
fun ProductsList(
    products: List<ProductsDto>,
    onAddToCart: (ProductsDto) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(products) { product ->
            ProductItem(product, onAddToCart)
        }
    }
}

@Composable
fun ProductItem(
    product: ProductsDto,
    onAddToCart: (ProductsDto) -> Unit,
    cartViewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val isInCart = cartItems.any { it.id == product.id }

    ProductCard(
        product = product,
        isInCart = isInCart,
        onAddToCart = {
            if (isInCart) {
                cartViewModel.removeCartItem(product.id)
            } else {
                onAddToCart(product)
            }
        },
        modifier = Modifier.padding(16.dp),
        imageSize = 120.dp
    )
}
