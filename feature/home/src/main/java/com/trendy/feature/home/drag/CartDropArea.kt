// path: feature/home/src/main/java/com/trendy/feature/home/drag/CartDropArea.kt
package com.trendy.feature.home.drag

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.dp

/**
 * Alttan kayan sepet drop alanı. Layout sınırlarını CartDropController'a bildirir ve
 * pointer hareketinde controller.update(...) ile "isOver" bilgisini canlı tutar.
 */
@Composable
fun CartDropArea(
    visible: Boolean,
    currentPointer: Offset,
    controller: CartDropController,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Pointer değiştikçe "üzerinde mi" güncelle
    LaunchedEffect(currentPointer, visible) {
        if (visible) controller.update(currentPointer) else controller.reset()
    }

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut()
    ) {
        Surface(
            tonalElevation = if (controller.isOver.value) 8.dp else 2.dp,
            modifier = modifier
                .fillMaxWidth()
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInWindow()
                    val size = coords.size
                    controller.onLayout(
                        IntRect(
                            pos.x.toInt(),
                            pos.y.toInt(),
                            pos.x.toInt() + size.width,
                            pos.y.toInt() + size.height
                        )
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                Spacer(Modifier.padding(4.dp))
                Text(
                    if (controller.isOver.value) "Bırak ekleyelim!" else "Sepete sürükleyin",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}
