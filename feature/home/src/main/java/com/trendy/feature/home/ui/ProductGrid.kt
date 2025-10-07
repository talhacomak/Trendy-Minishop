package com.trendy.feature.home.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.trendy.domain.model.Product
import com.trendy.feature.home.drag.CartDropController
import com.trendy.feature.home.drag.DragAndDropState

@Composable
fun ProductGrid(
    list: List<Product>,
    favoriteIds: Set<Int>,
    dragState: DragAndDropState,
    cartCtrl: CartDropController,
    onDragStartHaptic: () -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onAddToCart: (Product) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = list, key = { it.id }, contentType = { "product" }) { p ->
            DraggableProductCard(
                product = p,
                dragState = dragState,
                cartCtrl = cartCtrl,
                onDragStart = onDragStartHaptic,
                minScaleAtTarget = 0.88f,
                shouldReturnToOrigin = true,
                onDropAccepted = { accepted ->
                    onAddToCart(accepted)
                }
            ) {
                ProductCard(
                    product = p,
                    isFavorite = favoriteIds.contains(p.id),
                    onFavoriteToggle = { onToggleFavorite(p.id) },
                    onAddToCart = { onAddToCart(p) },
                    onClick = null
                )
            }
        }
    }
}

@Preview
@Composable
fun ProductGridPreview() {
    val sampleProducts = listOf(
        Product(
            id = 1,
            title = "Sample Product 1",
            price = 29.99,
            description = "This is a sample product description.",
            category = "electronics",
            image = "test",
            rating = Product.Rating(rate = 4.5, count = 10)
        ),
        Product(
            id = 2,
            title = "Sample Product 2",
            price = 59.99,
            description = "This is another sample product description.",
            category = "jewelery",
            image = "test",
            rating = Product.Rating(rate = 4.0, count = 20)
        )
    )
    ProductGrid(
        list = sampleProducts,
        favoriteIds = setOf(1),
        dragState = DragAndDropState(),
        cartCtrl = CartDropController(5f, 5f),
        onDragStartHaptic = {},
        onToggleFavorite = {},
        onAddToCart = {}
    )
}
