package com.ranker.countries.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ranker.countries.di.AppContainer
import com.ranker.countries.ui.rankings.RankingsRoute
import com.ranker.countries.ui.rankings.RankingsViewModel
import com.ranker.countries.ui.theme.RankerTheme

@Composable
fun App(container: AppContainer) {
    RankerTheme {
        RankingsRoute(viewModel { RankingsViewModel(container.getDailyRankings, container.publishSeedStats) })
    }
}
