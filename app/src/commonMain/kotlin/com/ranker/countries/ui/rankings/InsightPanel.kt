package com.ranker.countries.ui.rankings


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ranker.countries.domain.model.Ranking
import com.ranker.countries.resources.Res
import com.ranker.countries.resources.insight_continent
import com.ranker.countries.resources.insight_lead
import com.ranker.countries.resources.insight_ratio
import com.ranker.countries.ui.format.continentName
import com.ranker.countries.ui.format.formatNumber
import com.ranker.countries.ui.format.formatPercent
import com.ranker.countries.ui.theme.Accent
import org.jetbrains.compose.resources.stringResource

/** A glassy card under the bars: the stat's story and what stands out in its ranking. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsightPanel(ranking: Ranking, accent: Accent, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(24.dp)
    val insights = ranking.insights
    Column(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.72f), shape)
            .border(
                1.dp,
                Brush.linearGradient(listOf(accent.start.copy(alpha = 0.6f), accent.end.copy(alpha = 0.1f))),
                shape,
            )
            .padding(18.dp),
    ) {
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
