// path: feature/home/src/main/java/com/trendy/feature/home/HomeScreen.kt
package com.trendy.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.trendy.domain.model.Product
import com.trendy.feature.home.drag.CartDropArea
import com.trendy.feature.home.drag.rememberCartDropController
import com.trendy.feature.home.drag.rememberDragAndDropState
import com.trendy.feature.home.ui.DraggableProductCard
import com.trendy.feature.home.ui.ProductCard
import com.trendy.feature.home.ui.SelectionDialog
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onOpenCart: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val s by vm.state
    val dragState = rememberDragAndDropState()
    val dropCtrl = rememberCartDropController()
    val haptics = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showSelectionFor by remember { mutableStateOf<Product?>(null) }
    // 🔸 Drop animasyon tetikleyici: son başarılı drop edilen ürünün id'si
    var lastDroppedId by remember { mutableStateOf<Int?>(null) }

    fun needsSelection(p: Product): Boolean = p.category.contains("men", ignoreCase = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MiniShop") },
                actions = {
                    IconButton(onClick = onOpenCart) { Icon(Icons.Filled.ShoppingCart, contentDescription = "Sepet") }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            CartDropArea(
                visible = dragState.isDragging,
                currentPointer = dragState.dragOffset,
                controller = dropCtrl
            )
        }
    ) { padding ->
        when {
            s.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            s.error != null -> ErrorState(message = s.error?.message ?: "Bir şeyler ters gitti", onRetry = vm::refresh)
            else -> {
                Column(Modifier.fillMaxSize().padding(padding)) {
                    if (s.categories.isNotEmpty()) {
                        CategoryChips(
                            categories = s.categories,
                            selected = s.selectedCategory,
                            onSelect = vm::selectCategory
                        )
                    }
                    ProductGrid(
                        list = s.products,
                        favoriteIds = s.favoriteIds,
                        dragState = dragState,
                        onDragStartHaptic = { haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                        onToggleFavorite = vm::onToggleFavorite,
                        onAddToCart = { p ->
                            if (needsSelection(p)) showSelectionFor = p else {
                                vm.onAddToCart(p.id)
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                LaunchedEffect(p.id) {
                                    snackbarHostState.showSnackbar("Sepete eklendi: ${p.title}")
                                }
                                // 🔸 Kartta kısa shrink animasyonu için işaretle
                                lastDroppedId = p.id
                            }
                        },
                        // 🔸 Hangi kartın shrink olacağını söyle
                        isDropping = { id -> id == lastDroppedId }
                    )
                }
            }
        }
    }

    // Drag bitişinde karar: drop alanı üzerindeyse sepete ekle ve shrink tetikle
    LaunchedEffect(dragState.isDragging) {
        if (!dragState.isDragging) {
            val p = dragState.draggedProduct
            if (p != null && dropCtrl.isOver.value) {
                if (needsSelection(p)) {
                    showSelectionFor = p
                } else {
                    vm.onAddToCart(p.id)
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    snackbarHostState.showSnackbar("Sepete eklendi: ${p.title}")
                    // 🔸 shrink tetikle
                    lastDroppedId = p.id
                }
            }
            dropCtrl.reset()
            dragState.endDrag()
        }
    }

    // 🔸 Shrink efekti kısa sürsün (ör. 180–220ms), sonra flags temizlensin
    LaunchedEffect(lastDroppedId) {
        if (lastDroppedId != null) {
            delay(200)
            lastDroppedId = null
        }
    }

    // Seçim diyalogu (ör: beden)
    if (showSelectionFor != null) {
        SelectionDialog(
            title = "Seçim yapın",
            options = listOf("S", "M", "L", "XL"),
            onSelect = { _ ->
                vm.onAddToCart(showSelectionFor!!.id)
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                LaunchedEffect(showSelectionFor!!.id) {
                    snackbarHostState.showSnackbar("Sepete eklendi: ${showSelectionFor!!.title}")
                }
                // 🔸 shrink tetikle
                lastDroppedId = showSelectionFor!!.id
                showSelectionFor = null
            },
            onDismiss = { showSelectionFor = null }
        )
    }
}

@Composable
private fun ProductGrid(
    list: List<Product>,
    favoriteIds: Set<Int>,
    dragState: com.trendy.feature.home.drag.DragAndDropState,
    onDragStartHaptic: () -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onAddToCart: (Product) -> Unit,
    // 🔸 Dışarıdan "bu id şu an drop animasyonunda mı?" sorgusu
    isDropping: (Int) -> Boolean
) {
    LazyVerticalGrid(columns = GridCells.Adaptive(160.dp), contentPadding = PaddingValues(8.dp)) {
        items(list) { p ->
            DraggableProductCard(
                product = p,
                isFavorite = favoriteIds.contains(p.id),
                dragState = dragState,
                onFavoriteToggle = { onToggleFavorite(p.id) },
                onAddToCartRequested = onAddToCart,
                onDragStartCallback = onDragStartHaptic,
                // 🔸 Kart özelinde shrink bilgisi
                isDropping = isDropping(p.id)
            ) {
                ProductCard(
                    product = p,
                    isFavorite = favoriteIds.contains(p.id),
                    onFavoriteToggle = { onToggleFavorite(p.id) },
                    onAddToCart = { onAddToCart(p) },
                    onClick = null,
                    showRating = true
                )
            }
        }
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(message)
        Spacer(Modifier.height(8.dp))
        Button(onClick = onRetry) { Text("Tekrar Dene") }
    }
}
