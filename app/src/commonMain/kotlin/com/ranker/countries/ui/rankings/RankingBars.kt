package com.ranker.countries.ui.rankings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ranker.countries.domain.model.RankedCountry
import com.ranker.countries.domain.model.Ranking
import com.ranker.countries.ui.format.flagEmoji
import com.ranker.countries.ui.format.formatValue
import com.ranker.countries.ui.theme.Accent
import com.ranker.countries.ui.theme.Bronze
import com.ranker.countries.ui.theme.Gold
import com.ranker.countries.ui.theme.OnAccent
import com.ranker.countries.ui.theme.Silver
import kotlinx.coroutines.delay

private const val STAGGER_MILLIS = 70L

/**
 * The ten bars of a ranking. Each time [play] turns on they grow in one after the other with a
 * springy overshoot while their numbers count up; when it turns off they slide back out, so a page
 * swiped back to performs again.
 */
@Composable
fun RankingBars(
    ranking: Ranking,
    accent: Accent,
    play: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        ranking.entries.forEachIndexed { index, entry ->
            BarRow(
                entry = entry,
                index = index,
                unit = ranking.stat.unit,
                decimals = ranking.stat.decimals,
                accent = accent,
                play = play,
            )
        }
    }
}

@Composable
private fun BarRow(
    entry: RankedCountry,
    index: Int,
    unit: String,
    decimals: Int,
    accent: Accent,
    play: Boolean,
) {
    val growth = remember { Animatable(0f) }
    LaunchedEffect(play) {
        if (play) {
            growth.snapTo(0f)
            delay(index * STAGGER_MILLIS)
            growth.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = 90f))
        } else {
            growth.animateTo(0f, tween(220, easing = FastOutSlowInEasing))
        }
    }
    val appear = growth.value.coerceIn(0f, 1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = appear
                translationX = (1f - appear) * 48.dp.toPx()
            }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RankBadge(entry.rank)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(flagEmoji(entry.country.code), fontSize = 18.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = entry.country.name,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = formatValue(entry.value * appear, decimals, unit),
                    style = MaterialTheme.typography.labelLarge.copy(fontFeatureSettings = "tnum"),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(6.dp))
            Bar(
                share = entry.shareOfLeader.toFloat() * growth.value,
                accent = accent,
                shimmer = entry.rank == 1,
            )
        }
    }
}

@Composable
private fun Bar(share: Float, accent: Accent, shimmer: Boolean) {
    val track = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
    val sweep = if (shimmer) {
        rememberInfiniteTransition(label = "shimmer").animateFloat(
            initialValue = -0.3f,
            targetValue = 1.3f,
            animationSpec = infiniteRepeatable(
                tween(1_400, easing = LinearEasing),
                RepeatMode.Restart,
                initialStartOffset = StartOffset(900),
            ),
            label = "sweep",
        ).value
    } else {
        null
    }

    Canvas(Modifier.fillMaxWidth().height(12.dp)) {
        val corner = CornerRadius(size.height / 2)
        drawRoundRect(track, cornerRadius = corner)
        // Never thinner than the bar is tall, so even the shortest one keeps its round ends, and
        // never past the track, though the spring overshoots.
        val width = (size.width * share).coerceIn(if (share > 0f) size.height else 0f, size.width)
        if (width <= 0f) return@Canvas
        val fill = Brush.horizontalGradient(listOf(accent.start, accent.end), startX = 0f, endX = width)
        drawRoundRect(fill, size = Size(width, size.height), cornerRadius = corner)
        if (sweep != null) {
            val bar = Path().apply { addRoundRect(RoundRect(0f, 0f, width, size.height, corner)) }
            clipPath(bar) {
                val center = width * sweep
                val band = size.height * 3
                drawRect(
                    Brush.linearGradient(
                        listOf(Color.Transparent, Color.White.copy(alpha = 0.55f), Color.Transparent),
                        start = Offset(center - band, 0f),
                        end = Offset(center + band, size.height),
                    ),
                )
            }
        }
    }
}

/** The rank in a circle: gold, silver and bronze for the podium, with a bobbing crown on the winner. */
@Composable
private fun RankBadge(rank: Int) {
    val medal = when (rank) {
        1 -> Gold
        2 -> Silver
        3 -> Bronze
        else -> null
    }
    Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .then(
                    if (medal != null) {
                        Modifier.background(medal, CircleShape)
                    } else {
                        Modifier.border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = rank.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = if (medal != null) OnAccent else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (rank == 1) Crown(Modifier.align(Alignment.TopEnd))
    }
}

@Composable
private fun Crown(modifier: Modifier) {
    val bob by rememberInfiniteTransition(label = "crown").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "bob",
    )
    Text(
        text = "👑",
        fontSize = 13.sp,
        modifier = modifier
            .offset(x = 8.dp, y = (-10).dp)
            .graphicsLayer { translationY = -bob * 3.dp.toPx() }
            .rotate(20f),
    )
}
