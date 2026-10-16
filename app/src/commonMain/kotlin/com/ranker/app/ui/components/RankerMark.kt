package com.ranker.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * The launcher icon's mark: three ranked bars, longest on top, and a sparkle after the winner.
 * Same proportions as `ic_launcher_foreground.xml`, on a 60 × 40 grid.
 */
@Composable
fun RankerMark(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = size.width / 60f
        fun bar(top: Float, length: Float, from: Color, to: Color) = drawRoundRect(
            brush = Brush.horizontalGradient(listOf(from, to), startX = 0f, endX = length * unit),
            topLeft = Offset(0f, top * unit),
            size = Size(length * unit, 10 * unit),
            cornerRadius = CornerRadius(5 * unit),
        )
        bar(0f, 46f, Color(0xFFFFC857), Color(0xFFFF7A45))
        bar(15f, 34f, Color(0xFF4BE1FF), Color(0xFF3D7BFF))
        bar(30f, 22f, Color(0xFFFF8AC4), Color(0xFFFF3D6E))
        sparkle(Offset(54 * unit, 5 * unit), 6 * unit, Color(0xFFFFE08A))
    }
}

/** A four-pointed star with concave sides. */
internal fun DrawScope.sparkle(center: Offset, radius: Float, color: Color) {
    val (x, y) = center
    val path = Path().apply {
        moveTo(x, y - radius)
        quadraticTo(x, y, x + radius, y)
        quadraticTo(x, y, x, y + radius)
        quadraticTo(x, y, x - radius, y)
        quadraticTo(x, y, x, y - radius)
        close()
    }
    drawPath(path, color)
}
