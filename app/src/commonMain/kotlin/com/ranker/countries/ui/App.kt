package com.ranker.countries.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ranker.countries.di.AppContainer
import com.ranker.countries.ui.rankings.RankingsRoute
import com.ranker.countries.ui.rankings.RankingsViewModel
import com.ranker.countries.ui.theme.RankerTheme

/**
 * [bottomBar] is pinned under the screen, for platform-only UI such as an ad banner. When given, it
 * owns the navigation bar inset.
 */
@Composable
fun App(container: AppContainer, bottomBar: (@Composable () -> Unit)? = null) {
    RankerTheme {
        RankingsRoute(
            viewModel { RankingsViewModel(container.getDailyRankings, container.publishSeedStats) },
            bottomBar = bottomBar,
        )
    }
}
