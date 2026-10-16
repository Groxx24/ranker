package com.ranker.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ranker.app.di.AppContainer
import com.ranker.app.ui.rankings.RankingsRoute
import com.ranker.app.ui.rankings.RankingsViewModel
import com.ranker.app.ui.theme.RankerTheme

@Composable
fun App(container: AppContainer) {
    RankerTheme {
        RankingsRoute(viewModel { RankingsViewModel(container.getDailyRankings) })
    }
}
