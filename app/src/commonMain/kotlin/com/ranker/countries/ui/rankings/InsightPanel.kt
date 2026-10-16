package com.ranker.countries.ui.rankings

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ranker.countries.domain.model.RankedCountry
import com.ranker.countries.domain.model.Ranking
import com.ranker.countries.resources.Res
import com.ranker.countries.resources.insight_continent
import com.ranker.countries.resources.insight_lead
import com.ranker.countries.resources.insight_ratio
import com.ranker.countries.resources.selected_leader
import com.ranker.countries.resources.selected_rank
import com.ranker.countries.resources.selected_share
import com.ranker.countries.ui.format.continentName
import com.ranker.countries.ui.format.flagEmoji
import com.ranker.countries.ui.format.formatNumber
import com.ranker.countries.ui.format.formatPercent
import com.ranker.countries.ui.format.formatValue
import com.ranker.countries.ui.theme.Accent
import org.jetbrains.compose.resources.stringResource

/**
 * A glassy card under the bars: the stat's story and what stands out in its ranking, or, once a
 * bar is tapped, that country's place against the leader. Morphs between the two.
 */
@Composable
fun InsightPanel(ranking: Ranking, selected: RankedCountry?, accent: Accent, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(24.dp)
    AnimatedContent(
        targetState = selected,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f), shape)
            .border(
                1.dp,
                Brush.linearGradient(listOf(accent.start.copy(alpha = 0.6f), accent.end.copy(alpha = 0.1f))),
                shape,
            )
            .padding(18.dp),
        transitionSpec = {
            (fadeIn(tween(260, delayMillis = 60)) + slideInVertically { it / 4 } + scaleIn(initialScale = 0.96f))
                .togetherWith(fadeOut(tween(120)))
                .using(SizeTransform(clip = false))
        },
        contentKey = { it?.rank },
        label = "insight",
    ) { entry ->
        if (entry == null) Overview(ranking) else Comparison(ranking, entry, accent)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Overview(ranking: Ranking) {
    val insights = ranking.insights
    Column {
        Text(
            text = ranking.stat.fact,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(14.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Chip("🏆", stringResource(Res.string.insight_lead, ranking.leader.country.name, formatPercent(insights.leadOverRunnerUpPercent)))
            Chip("📊", stringResource(Res.string.insight_ratio, formatNumber(insights.leaderToLastRatio, 1)))
            Chip("🌍", stringResource(Res.string.insight_continent, insights.topContinentCount, continentName(insights.topContinent)))
        }
    }
}

@Composable
private fun Chip(emoji: String, text: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(emoji, fontSize = 13.sp)
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun Comparison(ranking: Ranking, entry: RankedCountry, accent: Accent) {
    val stat = ranking.stat
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(flagEmoji(entry.country.code), fontSize = 40.sp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(entry.country.name, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    stringResource(Res.string.selected_rank, entry.rank),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = formatValue(entry.value, stat.decimals, stat.unit),
                style = TextStyle(
                    brush = Brush.horizontalGradient(listOf(accent.start, accent.end)),
                    fontSize = 26.sp,
                    fontWeight = MaterialTheme.typography.headlineMedium.fontWeight,
                ),
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = if (entry.rank == 1) {
                stringResource(Res.string.selected_leader, formatPercent(ranking.insights.leadOverRunnerUpPercent))
            } else {
                stringResource(Res.string.selected_share, formatNumber(entry.shareOfLeader * 100, 0))
            },
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        ShareMeter(entry.shareOfLeader.toFloat(), accent)
    }
}

/** How close to the leader, filling up as it appears. */
@Composable
private fun ShareMeter(share: Float, accent: Accent) {
    var target by remember { mutableStateOf(0f) }
    LaunchedEffect(share) { target = share }
    val fill by animateFloatAsState(target, spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessLow), label = "share")
    val track = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
    Canvas(Modifier.fillMaxWidth().height(6.dp)) {
        val corner = CornerRadius(size.height / 2)
        drawRoundRect(track, cornerRadius = corner)
        drawRoundRect(
            Brush.horizontalGradient(listOf(accent.start, accent.end)),
            size = Size(size.width * fill, size.height),
            cornerRadius = corner,
        )
    }
}
