package com.ranker.countries.di

import com.ranker.countries.data.hardcoded.HardcodedStatRepository
import com.ranker.countries.domain.repository.StatRepository
import com.ranker.countries.domain.usecase.GetDailyRankingsUseCase
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
