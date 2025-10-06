package com.trendy.feature.home.drag

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.toOffset
import com.trendy.feature.home.R

@Composable
fun CartDropArea(
    visible: Boolean,
    currentPointerInRoot: Offset,
    controller: CartDropController,
    modifier: Modifier = Modifier,
    targetSize: Dp = 240.dp
) {
    val animatedSize by animateDpAsState(
        targetValue = if (visible) targetSize else 0.dp,
        animationSpec = spring(
            dampingRatio = 0.85f,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cartSize"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.7f,
        animationSpec = spring(
            dampingRatio = 0.9f,
            stiffness = Spring.StiffnessLow
        ),
        label = "cartScale"
    )
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = spring(
            dampingRatio = 0.9f,
            stiffness = Spring.StiffnessLow
        ),
        label = "cartAlpha"
    )

    Box(
        modifier = modifier
            .size(animatedSize)
            .zIndex(10f)
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
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
            contentDescription = "Sepet alanı"
        )
    }

    LaunchedEffect(visible, currentPointerInRoot) {
        if (visible) controller.update(currentPointerInRoot) else controller.reset()
    }
}


@Preview
@Composable
fun CartDropAreaPreview() {
    var pointer by remember { mutableStateOf(Offset.Zero) }
    val ctrl = rememberCartDropController()
    Box(Modifier.fillMaxSize().padding(16.dp)) {
        CartDropArea(
            visible = true,
            currentPointerInRoot = pointer,
            controller = ctrl
        )
        Box(Modifier.fillMaxSize().onGloballyPositioned { coords ->
            pointer = coords.size.center.toOffset()
        })
    }
}
