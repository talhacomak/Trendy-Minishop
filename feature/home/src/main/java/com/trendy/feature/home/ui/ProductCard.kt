package com.trendy.feature.home.ui


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.trendy.domain.model.Product

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onAddToCart: () -> Unit,
    onClick: (() -> Unit)? = null,
    showRating: Boolean = true
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .semantics {
                contentDescription = "Ürün kartı: ${product.title}"
            }
    ) {
        Column(Modifier.padding(8.dp)) {
            // Görsel
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(6.dp))

            // Başlık
            Text(
                text = product.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall
            )

            // Fiyat + (opsiyonel) Puan
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                PricePill(price = product.price)
                if (showRating && product.rating.count > 0) {
                    AssistChip(
                        onClick = { /* no-op */ },
                        label = { Text("★ ${"%.1f".format(product.rating.rate)} (${product.rating.count})") }
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Aksiyonlar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconToggleButton(
                    checked = isFavorite,
                    onCheckedChange = { onFavoriteToggle() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) "Favoriden çıkar" else "Favoriye ekle"
                    )
                }
                FilledTonalButton(onClick = onAddToCart) {
                    Text("Sepete Ekle")
                }
            }
        }
    }
}

@Composable
private fun PricePill(price: Double) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = "₺" + "%.2f".format(price),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Preview(name = "ProductCard – Favorisiz, Puanlı", showBackground = true, widthDp = 200)
@Composable
private fun PreviewProductCard_Normal() {
    val sample = Product(
        id = 1,
        title = "Kablosuz Bluetooth Kulaklık Max Ultra Bass",
        price = 3299.90,
        description = "",
        category = "electronics",
        image = "https://picsum.photos/seed/headphones/400/400",
        rating = Product.Rating(rate = 4.6, count = 128)
    )
    ProductCard(
        product = sample,
        isFavorite = false,
        onFavoriteToggle = {},
        onAddToCart = {},
        onClick = {},
        showRating = true
    )
}