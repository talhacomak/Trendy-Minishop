package com.trendy.feature.home.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.trendy.domain.model.Product
import com.trendy.feature.home.drag.CartDropController
import com.trendy.feature.home.drag.DragAndDropState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.math.abs

@Suppress("UNUSED_PARAMETER")
@Composable
fun DraggableProductCard(
    product: Product,
    dragState: DragAndDropState,
    cartCtrl: CartDropController,
    onDragStart: (() -> Unit)? = null,
    isDropping: Boolean = false,
    minScaleAtTarget: Float = 0.88f,
    shouldReturnToOrigin: Boolean = true,
    // Animasyon BITTİKTEN sonra, kart sepet merkezine emilip küçüldüğünde çağrılır
    onDropAccepted: (Product) -> Unit = {},
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    var dragDx by remember { mutableFloatStateOf(0f) }
    var dragDy by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val animX = remember { Animatable(0f) }
    val animY = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(1f) }
    val selectedScale = 1.5f

    var isAnimating by remember { mutableStateOf(false) }
    LaunchedEffect(animX, animY, scaleAnim) {
        snapshotFlow { animX.isRunning || animY.isRunning || scaleAnim.isRunning }
            .distinctUntilChanged()
            .collectLatest { running -> isAnimating = running }
    }

    // Kart ölçüsü ve root içindeki topLeft
    var cardPosInRoot by remember { mutableStateOf(Offset.Zero) }
    var cardSize by remember { mutableStateOf(Size.Zero) }

    fun currentTx() = if (isDragging) dragDx else animX.value
    fun currentTy() = if (isDragging) dragDy else animY.value

    fun cardRectInRoot(tx: Float, ty: Float): Rect {
        val topLeft = cardPosInRoot + Offset(tx, ty)
        return Rect(topLeft, topLeft + Offset(cardSize.width, cardSize.height))
    }

    fun updatePointerInRoot(localPointer: Offset) {
        val tx = currentTx()
        val ty = currentTy()
        dragState.pointerInRoot = cardPosInRoot + Offset(tx, ty) + localPointer
    }

    Box(
        modifier = Modifier
            .onGloballyPositioned { coords ->
                cardPosInRoot = coords.positionInRoot()
                cardSize = coords.size.toSize()
            }
            .zIndex(if (isDragging || isAnimating) 1f else 0f)
            .graphicsLayer {
                translationX = currentTx()
                translationY = currentTy()
                scaleX = scaleAnim.value
                scaleY = scaleAnim.value
            }
            .pointerInput(product.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { startOffset ->
                        onDragStart?.invoke()
                        isDragging = true
                        dragState.isDragging = true
                        dragState.draggedProduct = product

                        // animasyonları kes ve konumu temizle
                        scope.launch { animX.stop(); animX.snapTo(0f) }
                        scope.launch { animY.stop(); animY.snapTo(0f) }
                        dragDx = 0f
                        dragDy = 0f

                        // başlangıç skalası (biraz büyüterek)
                        scope.launch {
                            scaleAnim.animateTo(
                                targetValue = selectedScale,
                                animationSpec = spring(
                                    dampingRatio = 0.9f,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                        updatePointerInRoot(startOffset)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragDx += dragAmount.x
                        dragDy += dragAmount.y

                        // Yakınlık: 0 (uzak) .. 1 (merkezde/üstte)
                        val proximity = cartCtrl.proximityToCenter(
                            point = cardRectInRoot(dragDx, dragDy).center
                        )

                        // Yaklaştıkça küçülsün: selectedScale -> minScaleAtTarget
                        val targetScale = lerp(
                            start = selectedScale,
                            end = minScaleAtTarget,
                            t = proximity
                        )

                        // Skalayla his ver (yumuşakça yaklaşsın)
                        scope.launch {
                            // ani sıçramaları filtrelemek için küçük bir farkta atla
                            if (abs(scaleAnim.targetValue - targetScale) > 0.01f) {
                                scaleAnim.animateTo(
                                    targetValue = targetScale,
                                    animationSpec = spring(
                                        dampingRatio = 0.9f,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }

                        updatePointerInRoot(change.position)
                        // (Opsiyonel) dragState.currentLocal vs. hesapları burada da güncellenebilir
                    },
                    onDragEnd = {
                        isDragging = false
                        dragState.isDragging = false

                        val tx = dragDx
                        val ty = dragDy
                        val rect = cardRectInRoot(tx, ty)
                        val overlap = cartCtrl.overlapRatioOf(rect)

                        // Yarıdan fazlası sepet alanında mı?
                        val halfOrMoreInside = overlap >= 0.5f

                        if (halfOrMoreInside) {
                            // Sepetin merkezine doğru "emilme" animasyonu
                            val cardCenter = rect.center
                            val targetCenter = cartCtrl.targetCenter
                            val delta = targetCenter - cardCenter

                            val moveSpec = spring<Float>(
                                dampingRatio = 0.85f,
                                stiffness = 700f
                            )
                            val scaleSpec = spring<Float>(
                                dampingRatio = 0.9f,
                                stiffness = Spring.StiffnessLow
                            )

                            val jobs = listOf(
                                scope.launch {
                                    animX.snapTo(tx)
                                    animX.animateTo(tx + delta.x, moveSpec)
                                },
                                scope.launch {
                                    animY.snapTo(ty)
                                    animY.animateTo(ty + delta.y, moveSpec)
                                },
                                scope.launch {
                                    // merkeze emilirken biraz daha küçülsün (görsel tat)
                                    scaleAnim.animateTo(minScaleAtTarget * 0.8f, scaleSpec)
                                }
                            )

                            scope.launch {
                                jobs.joinAll() // tüm animasyonlar bitsin
                                // ✅ Animasyon bittikten sonra ViewModel'e ekleme bilgisini ver
                                onDropAccepted(product)
                                // Kart görünümü tekrar normal hâldeyken orijine dönsün (UI toparlama)
                                if (shouldReturnToOrigin) {
                                    val backSpec = spring<Float>(
                                        dampingRatio = 0.85f,
                                        stiffness = 600f
                                    )
                                    animX.animateTo(0f, backSpec)
                                    animY.animateTo(0f, backSpec)
                                    scaleAnim.animateTo(1f, scaleSpec)
                                }
                            }
                        } else {
                            // Eski davranış: orijine dön
                            val backSpec = spring<Float>(dampingRatio = 0.85f, stiffness = 600f)
                            scope.launch { animX.snapTo(tx); animX.animateTo(0f, backSpec) }
                            scope.launch { animY.snapTo(ty); animY.animateTo(0f, backSpec) }
                            scope.launch {
                                scaleAnim.animateTo(
                                    targetValue = 1f,
                                    animationSpec = spring(
                                        dampingRatio = 0.9f,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                            }
                        }
                    },
                    onDragCancel = {
                        isDragging = false
                        dragState.isDragging = false

                        val backSpec = spring<Float>(dampingRatio = 0.85f, stiffness = 600f)
                        scope.launch { animX.snapTo(dragDx); animX.animateTo(0f, backSpec) }
                        scope.launch { animY.snapTo(dragDy); animY.animateTo(0f, backSpec) }
                        scope.launch {
                            scaleAnim.animateTo(
                                targetValue = 1f,
                                animationSpec = spring(
                                    dampingRatio = 0.9f,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        }
                    }
                )
            }
    ) {
        content()
    }
}

/** Linear interpolation helper */
private fun lerp(start: Float, end: Float, t: Float): Float = start + (end - start) * t
