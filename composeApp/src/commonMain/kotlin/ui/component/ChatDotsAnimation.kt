package ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ChatWaitingDots() {
    val dotSize = 6.dp
    val dotSpacing = 4.dp

    // Infinite transition to animate the dots
    val infiniteTransition = rememberInfiniteTransition()

    // Animate the alpha value for the first dot
    val alphaFirstDot by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animate the alpha value for the second dot with a delay
    val alphaSecondDot by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing, delayMillis = 250),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Animate the alpha value for the third dot with a delay
    val alphaThirdDot by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing, delayMillis = 500),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(dotSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(alpha = alphaFirstDot, size = dotSize)
        Dot(alpha = alphaSecondDot, size = dotSize)
        Dot(alpha = alphaThirdDot, size = dotSize)
    }
}

@Composable
fun Dot(alpha: Float, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Color.LightGray.copy(alpha = alpha)
            )
    )
}