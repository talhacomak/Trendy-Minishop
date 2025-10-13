package com.trendy.feature.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.trendy.domain.model.Product
import com.trendy.feature.home.drag.CartDropArea
import com.trendy.feature.home.drag.rememberCartDropController
import com.trendy.feature.home.drag.rememberDragAndDropState
import com.trendy.feature.home.ui.CategoryChips
import com.trendy.feature.home.ui.ErrorState
import com.trendy.feature.home.ui.ProductGrid
import com.trendy.feature.home.ui.SelectionDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onOpenCart: () -> Unit,
    vm: HomeViewModel = hiltViewModel()
) {
    val homeUiState by vm.homeUiState

    HomeScreenContent(
        onOpenCart = onOpenCart,
        homeUiState = homeUiState,
        onSelectCategory = vm::selectCategory,
        onToggleFavorite = vm::onToggleFavorite,
        onAddToCart = vm::onAddToCart,
        onRefresh = vm::refresh
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    onOpenCart: () -> Unit,
    homeUiState: HomeUiState,
    onSelectCategory: (String) -> Unit,
    onToggleFavorite: (Int) -> Unit,
    onAddToCart: (Int) -> Unit,
    onRefresh: () -> Unit
) {
    val dragState = rememberDragAndDropState()
    val cartCtrl = rememberCartDropController()
    val haptics = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var lastDroppedId by remember { mutableStateOf<Int?>(null) }
    var showSelectionFor by remember { mutableStateOf<Product?>(null) }
    fun needsSelection(p: Product): Boolean = p.category.contains("men", ignoreCase = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MiniShop") },
                actions = {
                    IconButton(onClick = onOpenCart) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Sepet")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val paddingTop = padding.calculateTopPadding()

        Box(Modifier.fillMaxSize()) {
            when {
                homeUiState.loading -> Box(
                    Modifier.fillMaxSize().padding(top = paddingTop),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }

                homeUiState.error != null -> Box(
                    Modifier.fillMaxSize().padding(top = paddingTop)
                ) {
                    ErrorState(
                        message = homeUiState.error.message ?: "Bir şeyler ters gitti",
                        onRetry = onRefresh
                    )
                }

                else -> {
                    Column(
                        Modifier.fillMaxSize().padding(top = paddingTop)
                    ) {
                        if (homeUiState.categories.isNotEmpty()) {
                            CategoryChips(
                                categories = homeUiState.categories,
                                selected = homeUiState.selectedCategory,
                                onSelect = onSelectCategory
                            )
                        }
                        ProductGrid(
                            list = homeUiState.products,
                            favoriteIds = homeUiState.favoriteIds,
                            dragState = dragState,
                            cartCtrl = cartCtrl,
                            onDragStartHaptic = { haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                            onToggleFavorite = onToggleFavorite,
                            onAddToCart = { p ->
                                if (needsSelection(p)) {
                                    showSelectionFor = p
                                } else {
                                    onAddToCart(p.id)
                                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                    scope.launch { snackbarHostState.showSnackbar("Sepete eklendi: ${p.title}") }
                                    lastDroppedId = p.id
                                }
                            }
                        )
                    }
                }
            }

            CartDropArea(
                visible = dragState.isDragging,
                addingToCart = dragState.isAddingToCart,
                currentPointerInRoot = dragState.pointerInRoot,
                controller = cartCtrl,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }

    LaunchedEffect(lastDroppedId) {
        if (lastDroppedId != null) {
            delay(250)
            lastDroppedId = null
        }
    }

    if (showSelectionFor != null) {
        SelectionDialog(
            title = "Seçim yapın",
            options = listOf("S", "M", "L", "XL"),
            onSelect = {
                val selected = showSelectionFor!!
                onAddToCart(selected.id)
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                scope.launch { snackbarHostState.showSnackbar("Sepete eklendi: ${selected.title}") }
                lastDroppedId = selected.id
                showSelectionFor = null
            },
            onDismiss = { showSelectionFor = null }
        )
    }
}


@Preview(showBackground = true, widthDp = 400, heightDp = 800)
@Composable
fun HomeScreenContentPreview() {
    val dummyProducts = listOf(
        Product(1, "T-Shirt", 149.99, "Pamuklu", "men clothing", "", Product.Rating(4.2, 34)),
        Product(2, "Sneakers", 799.0, "Spor", "men shoes", "", Product.Rating(4.4, 18)),
        Product(3, "Elbise", 499.0, "Kadın", "women clothing", "", Product.Rating(4.1, 12)),
    )
    val dummyCategories = listOf("Hepsi", "Men", "Women", "Electronics")

    val fakeUi = HomeUiState(
        loading = false,
        error = null,
        products = dummyProducts,
        favoriteIds = emptySet(),
        categories = dummyCategories,
        selectedCategory = "Hepsi"
    )

    MaterialTheme {
        HomeScreenContent(
            onOpenCart = {},
            homeUiState = fakeUi,
            onSelectCategory = {},
            onToggleFavorite = {},
            onAddToCart = {},
            onRefresh = {}
        )
    }
}
