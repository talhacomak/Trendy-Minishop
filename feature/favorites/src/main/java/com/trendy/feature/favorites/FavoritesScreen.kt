// path: feature/favorites/src/main/java/com/trendy/feature/favorites/FavoritesScreen.kt
package com.trendy.feature.favorites

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.trendy.domain.model.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    vm: FavoritesViewModel = hiltViewModel(),
    onRemoveFavorite: ((Int) -> Unit)? = null
) {
    val s by vm.state

    Column(Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("Favoriler") })

        when {
            s.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            s.error != null -> ErrorState(message = s.error?.message ?: "Bir hata oluştu")
            s.products.isEmpty() -> EmptyState()
            else -> FavoritesList(
                list = s.products,
                onRemove = onRemoveFavorite
            )
        }
    }
}

@Composable
private fun FavoritesList(
    list: List<Product>,
    onRemove: ((Int) -> Unit)?
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(list) { p ->
            FavoriteItem(
                product = p,
                onRemove = { onRemove?.invoke(p.id) }
            )
        }
    }
}

@Composable
private fun FavoriteItem(
    product: Product,
    onRemove: (() -> Unit)?
) {
    Card(Modifier.padding(8.dp)) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                modifier = Modifier
                    .height(72.dp)
                    .padding(end = 8.dp),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.weight(1f)) {
                Text(
                    product.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall
                )
                Text("₺" + "%.2f".format(product.price))
            }
            IconButton(onClick = { onRemove?.invoke() }) {
                Icon(Icons.Filled.Delete, contentDescription = "Favoriden çıkar")
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Favori ürününüz yok")
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message)
    }
}
