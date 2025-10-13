package com.trendy.feature.home.drag

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import com.trendy.domain.model.Product

class DragAndDropState {
    var isDragging by mutableStateOf(false)
    var isAddingToCart by mutableStateOf(false)
    var draggedProduct: Product? by mutableStateOf(null)
    var pointerInRoot by mutableStateOf(Offset.Zero)
}

@Composable
fun rememberDragAndDropState() = remember { DragAndDropState() }

@Composable
fun rememberCartDropController(): CartDropController {
    return remember { CartDropController() }
}

