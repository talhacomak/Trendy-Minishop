package com.trendy.feature.home.drag

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

/**
 * Drag hedefi olan "sepet" alanını temsil eder.
 * - targetRect: root koordinatlarında hedef alan
 * - pointer: anlık pointer
 * - Proximity: 0 (çok uzak) .. 1 (hedef merkezine yakın/üstünde)
 * - Overlap: verilen kart rect'i ile hedefin örtüşme oranı (0..1)
 */
class CartDropController(
    private val minProximityRadiusPx: Float = 48f,
    private val maxProximityRadiusPx: Float = 420f
) {
    private var targetTopLeft by mutableStateOf(Offset.Zero)
    private var targetSize by mutableStateOf(Size.Zero)

    private var pointer by mutableStateOf(Offset.Zero)

    fun setTargetRect(topLeft: Offset, size: Size) {
        targetTopLeft = topLeft
        targetSize = size
    }

    fun update(pointerInRoot: Offset) {
        pointer = pointerInRoot
    }

    fun reset() {
        pointer = Offset.Zero
    }

    val targetRect: Rect
        get() = Rect(targetTopLeft, targetTopLeft + Offset(targetSize.width, targetSize.height))

    val targetCenter: Offset
        get() = Offset(
            x = targetTopLeft.x + targetSize.width / 2f,
            y = targetTopLeft.y + targetSize.height / 2f
        )

    /**
     * 0 (uzak) .. 1 (merkezde/üstünde) yakınlık.
     * Eşikler: minProximityRadiusPx (tam yakın) ve maxProximityRadiusPx (tam uzak).
     */
    fun proximityToCenter(point: Offset): Float {
        if (targetSize.isEmpty()) return 0f
        val d = hypot((point.x - targetCenter.x), (point.y - targetCenter.y))
        val clamped = ((maxProximityRadiusPx - d) / (maxProximityRadiusPx - minProximityRadiusPx))
        return clamped.coerceIn(0f, 1f)
    }

    /**
     * Kart rect'i ile hedef (sepet) rect'inin örtüşme oranı: overlapArea / cardArea (0..1)
     */
    fun overlapRatioOf(cardRect: Rect): Float {
        if (targetSize.isEmpty()) return 0f
        val r1 = cardRect
        val r2 = targetRect
        val left = max(r1.left, r2.left)
        val right = min(r1.right, r2.right)
        val top = max(r1.top, r2.top)
        val bottom = min(r1.bottom, r2.bottom)
        val overlapW = (right - left).coerceAtLeast(0f)
        val overlapH = (bottom - top).coerceAtLeast(0f)
        val overlapArea = overlapW * overlapH
        val cardArea = r1.width * r1.height
        return if (cardArea > 0f) (overlapArea / cardArea).coerceIn(0f, 1f) else 0f
    }

    /**
     * Eski kullanım için: pointer hedef içinde mi?
     */
    fun shouldAccept(pointInRoot: Offset): Boolean {
        return targetRect.contains(pointInRoot)
    }
}
