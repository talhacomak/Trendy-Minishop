// path: feature/home/src/main/java/com/trendy/feature/home/drag/DragAndDropState.kt
package com.trendy.feature.home.drag

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import com.trendy.domain.model.Product

@Stable
class DragAndDropState {
    var isDragging by mutableStateOf(false)
        internal set
    var dragOffset by mutableStateOf(Offset.Zero)
        internal set
    var startOffset by mutableStateOf(Offset.Zero)
        internal set
    var draggedProduct by mutableStateOf<Product?>(null)
        internal set

    fun startDrag(product: Product, pointer: Offset) {
        draggedProduct = product
        startOffset = pointer
        dragOffset = pointer
        isDragging = true
    }

    fun updateDrag(pointer: Offset) {
        dragOffset = pointer
    }

    fun endDrag() {
        isDragging = false
        dragOffset = Offset.Zero
        startOffset = Offset.Zero
        draggedProduct = null
    }
}

@Composable
fun rememberDragAndDropState(): DragAndDropState = remember { DragAndDropState() }
