package com.trendy.feature.home.drag

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import com.trendy.feature.home.R

@Composable
fun CartDropArea(
    visible: Boolean,
    addingToCart: Boolean = false,              // when true, trigger a short "pulse" on the cart
    currentPointerInRoot: Offset,
    controller: CartDropController,
    modifier: Modifier = Modifier,
    targetSize: Dp = 240.dp,                    // cart footprint while visible
    minPulseScale: Float = 0.92f                // how small the cart gets during the pulse
) {
    // Tracks whether a pulse animation is currently running (prevents early hide).
    var isPulsing by remember { mutableStateOf(false) }

    // Dedicated scale for the pulse (multiplied on top of base scale).
    val pulseScaleAnim = remember { Animatable(1f) }

    // Keep the cart on screen while dragging, or while the pulse is running,
    // or right when an item is accepted (addingToCart).
    val shouldBeVisible = visible || addingToCart || isPulsing

    // Base entrance/exit animations for size, scale and alpha.
    val animatedSize by animateDpAsState(
        targetValue = if (shouldBeVisible) targetSize else 0.dp,
        animationSpec = spring(dampingRatio = 0.85f, stiffness = Spring.StiffnessMedium),
        label = "cartSize"
    )
    val baseScale by animateFloatAsState(
        targetValue = if (shouldBeVisible) 1f else 0.7f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessLow),
        label = "cartBaseScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (shouldBeVisible) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.9f, stiffness = Spring.StiffnessLow),
        label = "cartAlpha"
    )

    // Trigger a single pulse when addingToCart becomes true.
    LaunchedEffect(addingToCart) {
        if (addingToCart && !pulseScaleAnim.isRunning) {
            isPulsing = true
            try {
                pulseScaleAnim.snapTo(1f)
                // shrink a bit
                pulseScaleAnim.animateTo(
                    minPulseScale,
                    spring(dampingRatio = 0.75f, stiffness = 900f)
                )
                // grow back to normal
                pulseScaleAnim.animateTo(
                    1f,
                    spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMedium)
                )
            } finally {
                isPulsing = false
            }
        }
    }

    // Keep target rect updated for overlap/proximity checks.
    Box(
        modifier = modifier
            .size(animatedSize)
            .zIndex(10f) // keep on top of product grid while interacting
            .graphicsLayer {
                // No background/elevation — only a pure icon with transforms.
                this.alpha = alpha
                val pulseScale = pulseScaleAnim.value
                scaleX = baseScale * pulseScale
                scaleY = baseScale * pulseScale
            }
            .onGloballyPositioned { coords ->
                controller.setTargetRect(
                    topLeft = coords.positionInRoot(),
                    size = coords.size.toSize()
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.cart),
            contentDescription = "Cart drop area"
        )
    }

    // Update controller with pointer while dragging; otherwise reset if fully idle.
    LaunchedEffect(visible, addingToCart, currentPointerInRoot) {
        when {
            visible -> controller.update(currentPointerInRoot)
            // do not reset while pulsing (cart still visible)
            !addingToCart && !isPulsing -> controller.reset()
        }
    }
}

@Preview
@Composable
private fun CartDropAreaPreview() {
    var pointer by remember { mutableStateOf(Offset.Zero) }
    val ctrl = rememberCartDropController()
    Box(Modifier.fillMaxSize().padding(16.dp)) {
        CartDropArea(
            visible = true,
            addingToCart = true,
            currentPointerInRoot = pointer,
            controller = ctrl
        )
        Box(
            Modifier
                .fillMaxSize()
                .onGloballyPositioned { coords -> pointer = Offset(
                    x = coords.size.width / 2f,
                    y = coords.size.height / 2f
                ) }
        )
    }
}
