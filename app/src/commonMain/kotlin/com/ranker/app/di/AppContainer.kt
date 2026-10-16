package com.ranker.app.di

import com.ranker.app.data.hardcoded.HardcodedStatRepository
import com.ranker.app.domain.repository.StatRepository
import com.ranker.app.domain.usecase.GetDailyRankingsUseCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Where the app's dependencies are wired, and nowhere else. */
@OptIn(ExperimentalTime::class)
class AppContainer {

    private val statRepository: StatRepository = HardcodedStatRepository()

    val getDailyRankings = GetDailyRankingsUseCase(
        repository = statRepository,
        today = { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
    )
}
