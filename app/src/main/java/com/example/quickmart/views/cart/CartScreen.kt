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
package com.example.quickmart.views.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.data.database.entity.CartItemEntity
import com.example.quickmart.R
import com.example.quickmart.composables.EasyShopTopBar
import com.example.quickmart.views.viewmodels.CartViewModel

@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = hiltViewModel()
) {
    val cartItems by viewModel.cartItems.collectAsState(initial = emptyList())
    val totalAmount = cartItems.sumOf { it.price }
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            EasyShopTopBar(
                title = "Your Cart",
                onBackPressed = {
                    navController.navigate("products")
                }
            )
        }
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            EmptyCartScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(cartItems) { cartItem ->
                        CartItemRow(
                            cartItemEntity = cartItem,
                            onDelete = { viewModel.removeCartItem(cartItem.id) },
                            modifier = Modifier.padding(8.dp),
                            imageSize = 90.dp,
                            titleStyle = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
                Divider()
                TotalPriceDisplay(totalAmount)
                PlaceOrderButton(onClick = { showDialog = true }, isLoading = false)
            }
        }

        if (showDialog) {
            OrderSuccessDialog(
                onDismiss = { showDialog = false },
                onConfirm = {
                    viewModel.clearCart()
                    showDialog = false
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun EmptyCartScreen() {
    InfoScreen(
        icon = Icons.Default.ShoppingCart,
        message = "Cart is empty",
        messageColor = Color.Gray
    )
}

@Composable
fun CartItemRow(
    cartItemEntity: CartItemEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = 80.dp,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    showDeleteIcon: Boolean = true
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = cartItemEntity.image,
                contentDescription = "Product Image",
                modifier = Modifier.size(imageSize),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = cartItemEntity.title, style = titleStyle)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$${cartItemEntity.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showDeleteIcon) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun TotalPriceDisplay(totalAmount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Total:", style = MaterialTheme.typography.titleMedium)
        Text(
            text = "$$totalAmount",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlaceOrderButton(onClick: () -> Unit, isLoading: Boolean) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(20.dp))
        } else {
            Text("Place Order")
        }
    }
}

@Composable
fun OrderSuccessDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.payment_success),
                    contentDescription = "Success Image",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Your order was successful!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.Blue
                )
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onConfirm
                ) {
                    Text("OK")
                }
            }
        }
    )
}

@Composable
fun InfoScreen(
    icon: ImageVector,
    iconSize: Dp = 120.dp,
    message: String,
    messageColor: Color = Color.Gray,
    messageFontSize: TextUnit = 20.sp
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icon",
                modifier = Modifier.size(iconSize),
                tint = Color.Green
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                fontSize = messageFontSize,
                fontWeight = FontWeight.Bold,
                color = messageColor
            )
        }
    }
}
