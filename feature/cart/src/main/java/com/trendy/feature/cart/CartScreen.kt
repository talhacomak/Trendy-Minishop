package com.trendy.feature.cart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.trendy.domain.model.Product


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    vm: CartViewModel = hiltViewModel()
) {
    val uiState by vm.cartUiState
    CartScreenContent(
        cartUiState = uiState,
        onIncreaseQuantity = { productId -> vm.inc(productId) },
        onDecreaseQuantity = { productId -> vm.dec(productId) },
        onRemoveItem = { productId -> vm.remove(productId) }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartScreenContent(
    cartUiState: CartUiState,
    onIncreaseQuantity: (Int) -> Unit,
    onDecreaseQuantity: (Int) -> Unit,
    onRemoveItem: (Int) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Sepet") })

        when {
            cartUiState.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            cartUiState.error != null -> ErrorState(message = cartUiState.error.message ?: "Bir hata oluştu")
            cartUiState.items.isEmpty() -> EmptyState()
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(cartUiState.items) { item ->
                        CartRow(
                            title = item.product.title,
                            price = item.product.price,
                            image = item.product.image,
                            qty = item.quantity,
                            onInc = { onIncreaseQuantity(item.product.id) },
                            onDec = { onDecreaseQuantity(item.product.id) },
                            onRemove = { onRemoveItem(item.product.id) }
                        )
                    }
                }
                SummaryBar(total = cartUiState.total)
            }
        }
    }
}

@Composable
private fun CartRow(
    title: String,
    price: Double,
    image: String,
    qty: Int,
    onInc: () -> Unit,
    onDec: () -> Unit,
    onRemove: () -> Unit
) {
    Card(Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = image,
                contentDescription = title,
                modifier = Modifier
                    .size(72.dp)
                    .clip(shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "₺" + "%.2f".format(price),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            QtyStepper(
                qty = qty,
                onDec = onDec,
                onInc = onInc,
                onRemove = onRemove
            )
        }
    }
}

@Composable
private fun QtyStepper(
    qty: Int,
    onDec: () -> Unit,
    onInc: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        IconButton(onClick = onDec) {
            Icon(Icons.Filled.Remove, contentDescription = "Azalt")
        }
        Text(
            text = qty.toString(),
            modifier = Modifier
                .width(28.dp)
                .padding(horizontal = 2.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )
        IconButton(onClick = onInc) {
            Icon(Icons.Filled.Add, contentDescription = "Arttır")
        }
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Delete, contentDescription = "Kaldır")
        }
    }
}


@Composable
private fun SummaryBar(total: Double) {
    Surface(tonalElevation = 2.dp) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Toplam")
                Text("₺" + "%.2f".format(total))
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { /* TODO: checkout */ },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Devam Et") }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Sepet boş")
    }
}

@Composable
private fun ErrorState(message: String) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(message)
        Spacer(Modifier.height(8.dp))
        Button(onClick = { }) {
            Text("Yenile")
        }
    }
}


@Preview(showBackground = true, name = "Filled cart")
@Composable
private fun CartScreenContentPreview_Filled() {
    val dummyProducts = listOf(
        Product(1, "T-Shirt", 149.99, "Pamuklu", "men clothing", "", Product.Rating(4.2, 34)),
        Product(2, "Sneakers", 799.0, "Spor", "men shoes", "", Product.Rating(4.4, 18)),
        Product(3, "Elbise", 499.0, "Kadın", "women clothing", "", Product.Rating(4.1, 12)),
    )
    val demoItems = dummyProducts.map {
        CartUiState.Item(
            product = it,
            quantity = (1..3).random()
        )
    }

    val demoState = CartUiState(
        loading = false,
        error = null,
        items = demoItems,
        total = demoItems.sumOf { it.product.price * it.quantity }
    )

    MaterialTheme {
        CartScreenContent(
            cartUiState = demoState,
            onIncreaseQuantity = {},
            onDecreaseQuantity = {},
            onRemoveItem = {}
        )
    }
}