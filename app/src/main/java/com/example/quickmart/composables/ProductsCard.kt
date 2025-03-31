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
package com.example.quickmart.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.network.models.ProductsDto

@Composable
fun ProductCard(
    product: ProductsDto,
    isInCart: Boolean,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier,
    imageSize: Dp = 100.dp,
    titleStyle: TextStyle = MaterialTheme.typography.titleMedium,
    priceStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = "Product Image",
                modifier = Modifier.size(imageSize),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.title, style = titleStyle)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "$${product.price}",
                    style = priceStyle,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onAddToCart
            ) {
                Icon(
                    imageVector = if (isInCart) Icons.Default.ShoppingCart else Icons.Default.AddShoppingCart,
                    contentDescription = if (isInCart) "In Cart" else "Add to Cart",
                    tint = if (isInCart) Color.Green else MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
