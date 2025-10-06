// path: feature/home/src/main/java/com/trendy/feature/home/drag/CartDropController.kt
package com.trendy.feature.home.drag

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntRect

/**
 * Alttaki sepet drop alanının sınırlarını ve "pointer üzerimde mi?" bilgisini tutar.
 * HomeScreen, drag bittiğinde controller.isOver.value'ı kontrol ederek karara varır.
 */
class CartDropController internal constructor() {
    internal val bounds: MutableState<IntRect> = mutableStateOf(IntRect(0, 0, 0, 0))
    val isOver: MutableState<Boolean> = mutableStateOf(false)

    fun onLayout(rect: IntRect) {
        bounds.value = rect
    }

    fun update(pointer: Offset) {
        val b = bounds.value
        val x = pointer.x.toInt()
        val y = pointer.y.toInt()
        isOver.value = x in b.left..b.right && y in b.top..b.bottom
    }

    fun reset() {
        isOver.value = false
    }
}

@Composable
fun rememberCartDropController(): CartDropController = remember { CartDropController() }
