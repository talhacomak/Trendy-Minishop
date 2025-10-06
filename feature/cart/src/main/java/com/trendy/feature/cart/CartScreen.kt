// path: feature/cart/src/main/java/com/trendy/feature/cart/CartScreen.kt
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun CartScreen(
    vm: CartViewModel = hiltViewModel()
) {
    val s by vm.state

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Sepet") })

        when {
            s.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.CircularProgressIndicator()
            }
            s.error != null -> ErrorState(message = s.error?.message ?: "Bir hata oluştu")
            s.items.isEmpty() -> EmptyState()
            else -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    items(s.items) { item ->
                        CartRow(
                            title = item.product.title,
                            price = item.product.price,
                            image = item.product.image,
                            qty = item.quantity,
                            onInc = { vm.inc(item.product.id) },
                            onDec = { vm.dec(item.product.id) },
                            onRemove = { vm.remove(item.product.id) }
                        )
                    }
                }
                SummaryBar(total = s.total)
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
    Card(Modifier.padding(8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = image,
                contentDescription = title,
                modifier = Modifier
                    .height(64.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
                Text("₺" + "%.2f".format(price), style = MaterialTheme.typography.bodyMedium)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDec) { Icon(Icons.Filled.Remove, contentDescription = "Azalt") }
                Text(qty.toString(), modifier = Modifier.padding(horizontal = 8.dp), textAlign = TextAlign.Center)
                IconButton(onClick = onInc) { Icon(Icons.Filled.Add, contentDescription = "Arttır") }
                IconButton(onClick = onRemove) { Icon(Icons.Filled.Delete, contentDescription = "Kaldır") }
            }
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
        Button(onClick = { /* No-op: ViewModel’de ayrı retry ihtiyacı yok */ }) {
            Text("Yenile")
        }
    }
}
