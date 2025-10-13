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
 * Represents the drop target area ("cart").
 * - targetRect: target area in root coordinates
 * - pointer: current pointer position
 * - Proximity: 0 (far) .. 1 (close to/over target center)
 * - Overlap: overlap ratio between the given card rect and the target rect (0..1)
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
     * Proximity to the target center: 0 (far) .. 1 (at/over center).
     * Thresholds: minProximityRadiusPx = fully close, maxProximityRadiusPx = fully far.
     */
    fun proximityToCenter(point: Offset): Float {
        if (targetSize.isEmpty()) return 0f
        val d = hypot((point.x - targetCenter.x), (point.y - targetCenter.y))
        val clamped = ((maxProximityRadiusPx - d) / (maxProximityRadiusPx - minProximityRadiusPx))
        return clamped.coerceIn(0f, 1f)
    }

    /**
     * Calculates the overlap ratio between the card rect and the target (cart) rect:
     * overlapArea / cardArea (0..1)
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
     * Checks if the given point is inside the target area.
     */
    fun shouldAccept(pointInRoot: Offset): Boolean {
        return targetRect.contains(pointInRoot)
    }
}
