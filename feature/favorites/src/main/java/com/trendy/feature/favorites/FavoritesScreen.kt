package com.trendy.feature.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import com.trendy.domain.model.Product
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    vm: FavoritesViewModel = hiltViewModel(),
    onRemoveFavorite: ((Int) -> Unit)? = null
) {
    val s by vm.state

    Scaffold(
        topBar = { TopAppBar(title = { Text("Favoriler") }) }
    ) { padding ->
        when {
            s.loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }

            s.error != null -> ErrorState(
                message = s.error?.message ?: "Bir hata oluştu",
                modifier = Modifier.padding(padding)
            )

            s.products.isEmpty() -> EmptyState(Modifier.padding(padding))

            else -> FavoritesList(
                list = s.products,
                onRemove = onRemoveFavorite,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun FavoritesList(
    list: List<Product>,
    onRemove: ((Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Use stable keys for better list performance
        items(
            items = list,
            key = { it.id }
        ) { p ->
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
    val priceText by remember(product.price) {
        mutableStateOf(
            NumberFormat.getCurrencyInstance(Locale.getDefault()).format(product.price)
        )
    }

    Card(
        // Slight elevation and spacious padding improves visual hierarchy
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product image with fixed size and rounded corners
            AsyncImage(
                model = product.image,
                contentDescription = product.title,
                modifier = Modifier
                    .size(76.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // Text area
            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                // Title: 2 lines max, readable size
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(6.dp))

                // Price: larger, high-contrast, primary color
                Text(
                    text = priceText,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Remove button with clear affordance
            IconButton(
                onClick = { onRemove?.invoke() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Favoriden çıkar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Favori ürününüz yok",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                message,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
