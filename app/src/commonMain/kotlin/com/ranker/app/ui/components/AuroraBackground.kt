package com.ranker.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.ranker.app.ui.theme.Accent
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Two soft glows in the [accent]'s colours drifting slowly behind the content. The accent is
 * blended while the pager moves, so the light shifts with every swipe.
 */
@Composable
fun AuroraBackground(accent: Accent, modifier: Modifier = Modifier) {
    val background = MaterialTheme.colorScheme.background
    val strength = if (background.luminance() < 0.5f) 0.42f else 0.26f
    val drift by rememberInfiniteTransition(label = "aurora").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(16_000, easing = LinearEasing), RepeatMode.Restart),
        label = "drift",
    )
    Canvas(modifier) {
        drawRect(background)
        val angle = drift * 2 * PI.toFloat()
        val first = Offset(
            x = size.width * (0.2f + 0.18f * cos(angle)),
            y = size.height * (0.12f + 0.06f * sin(angle)),
        )
        val second = Offset(
            x = size.width * (0.85f - 0.2f * sin(angle)),
            y = size.height * (0.62f + 0.1f * cos(angle)),
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(accent.start.copy(alpha = strength), Color.Transparent),
                center = first,
                radius = size.width * 0.95f,
            ),
            radius = size.width * 0.95f,
            center = first,
        )
        drawCircle(
            brush = Brush.radialGradient(
                listOf(accent.end.copy(alpha = strength * 0.75f), Color.Transparent),
                center = second,
                radius = size.width * 0.9f,
            ),
            radius = size.width * 0.9f,
            center = second,
        )
    }
}
