package com.ranker.countries.ui.rankings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ranker.countries.domain.model.DailyRankings
import com.ranker.countries.domain.model.Ranking
import com.ranker.countries.resources.Res
import com.ranker.countries.resources.app_name
import com.ranker.countries.resources.empty_rankings
import com.ranker.countries.resources.header_tagline
import com.ranker.countries.resources.load_failed
import com.ranker.countries.resources.loading
import com.ranker.countries.resources.retry
import com.ranker.countries.ui.components.AuroraBackground
import com.ranker.countries.ui.components.RankerMark
import com.ranker.countries.ui.format.shortDate
import com.ranker.countries.ui.theme.Accent
import com.ranker.countries.ui.theme.accentFor
import com.ranker.countries.ui.theme.lerp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.floor

@Composable
fun RankingsRoute(viewModel: RankingsViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    Box(Modifier.fillMaxSize()) {
        RankingsScreen(state, onRetry = viewModel::onRetry)
        if (viewModel.canPublishSampleStats) {
            val publish by viewModel.publish.collectAsStateWithLifecycle()
            PublishSampleStats(
                state = publish,
                onPublish = viewModel::onPublishSampleStats,
                onResultShown = viewModel::onPublishResultShown,
            )
        }
    }
}

@Composable
fun RankingsScreen(state: RankingsUiState, onRetry: () -> Unit) {
    when (state) {
        RankingsUiState.Loading -> Centered {
            CircularProgressIndicator()
            Spacer(Modifier.height(16.dp))
            Text(stringResource(Res.string.loading), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        RankingsUiState.Failed -> Centered {
            Text(stringResource(Res.string.load_failed), color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
        }
        RankingsUiState.Empty -> Centered {
            Text(stringResource(Res.string.empty_rankings), color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(16.dp))
            Button(onClick = onRetry) { Text(stringResource(Res.string.retry)) }
        }
        is RankingsUiState.Ready -> RankingsContent(state.day)
    }
}

@Composable
private fun RankingsContent(day: DailyRankings) {
    val pager = rememberPagerState { day.rankings.size }
    val scope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize()) {
        AuroraBackground(blendedAccent(pager), Modifier.fillMaxSize())
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Header(day)
            Spacer(Modifier.height(16.dp))
            StatTabs(
                rankings = day.rankings,
                selected = pager.targetPage,
                onSelect = { scope.launch { pager.animateScrollToPage(it) } },
            )
            HorizontalPager(pager, Modifier.weight(1f), beyondViewportPageCount = 1) { page ->
                RankingPage(
                    ranking = day.rankings[page],
                    accent = accentFor(page),
                    isActive = pager.targetPage == page,
                )
            }
        }
    }
}

/** The accent between the two pages the pager is showing, weighted by how far it has moved. */
private fun blendedAccent(pager: PagerState): Accent {
    val position = pager.currentPage + pager.currentPageOffsetFraction
    val from = floor(position).toInt().coerceAtLeast(0)
    return lerp(accentFor(from), accentFor(from + 1), position - from)
}

@Composable
private fun Header(day: DailyRankings) {
    val accent = accentFor(0)
    Row(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RankerMark(Modifier.size(width = 30.dp, height = 20.dp))
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(Res.string.app_name).uppercase(),
                style = MaterialTheme.typography.headlineSmall.merge(
                    TextStyle(
                        brush = Brush.linearGradient(listOf(MaterialTheme.colorScheme.onBackground, accent.end)),
                        letterSpacing = 4.sp,
                    ),
                ),
            )
            Text(
                stringResource(Res.string.header_tagline),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = shortDate(day.date),
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun RankingPage(ranking: Ranking, accent: Accent, isActive: Boolean) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(ranking.stat.emoji, fontSize = 34.sp)
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    ranking.stat.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Text(
                    ranking.stat.source,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        RankingBars(
            ranking = ranking,
            accent = accent,
            play = isActive,
        )
        Spacer(Modifier.height(24.dp))
        InsightPanel(ranking = ranking, accent = accent)
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            content()
        }
    }
}
