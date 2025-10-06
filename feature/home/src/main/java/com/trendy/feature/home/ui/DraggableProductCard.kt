// path: feature/home/src/main/java/com/trendy/feature/home/ui/DraggableProductCard.kt
package com.trendy.feature.home.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import com.trendy.domain.model.Product
import com.trendy.feature.home.drag.DragAndDropState
import kotlin.math.roundToInt

/**
 * Uzun-bas → sürükle. Kart sürüklenirken scale artar, drop anında kısa bir shrink uygulanabilir.
 */
@Composable
fun DraggableProductCard(
    product: Product,
    isFavorite: Boolean,
    dragState: DragAndDropState,
    onFavoriteToggle: () -> Unit,
    onAddToCartRequested: (Product) -> Unit,
    onDragStartCallback: (() -> Unit)? = null,
    /** Dışarıdan verilecek: bu kart son 200ms içinde başarılı şekilde drop edildi mi? */
    isDropping: Boolean = false,
    content: @Composable () -> Unit
) {
    val isThisDragging = dragState.isDragging && dragState.draggedProduct?.id == product.id

    // Sürükleme sırasında büyütme
    val dragScale by animateFloatAsState(
        targetValue = if (isThisDragging) 1.06f else 1f,
        label = "card-drag-scale"
    )

    // Drop sonrası kısa shrink (ör. 0.92x)
    val dropScale by animateFloatAsState(
        targetValue = if (isDropping) 0.92f else 1f,
        label = "card-drop-scale"
    )

    // Kombine ölçek
    val finalScale = dragScale * dropScale

    Box(
        modifier = Modifier
            .then(
                if (isThisDragging) {
                    Modifier
                        .offset {
                            IntOffset(
                                (dragState.dragOffset.x - dragState.startOffset.x).roundToInt(),
                                (dragState.dragOffset.y - dragState.startOffset.y).roundToInt()
                            )
                        }
                        .scale(finalScale)
                } else {
                    Modifier.scale(finalScale)
                }
            )
            .pointerInput(product.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        onDragStartCallback?.invoke()
                        dragState.startDrag(product, offset)
                    },
                    onDrag = { change, _ ->
                        dragState.updateDrag(change.position)
                    },
                    onDragEnd = {
                        // Drag bitişi üst katmanda kontrol edilecek
                    },
                    onDragCancel = {
                        dragState.endDrag()
                    }
                )
            }
    ) {
        content()
    }
}
