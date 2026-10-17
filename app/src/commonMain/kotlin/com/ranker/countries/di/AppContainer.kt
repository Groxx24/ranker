package com.ranker.countries.di

import com.ranker.countries.domain.repository.DebugLock
import com.ranker.countries.domain.repository.StatPublisher
import com.ranker.countries.domain.repository.StatRepository
import com.ranker.countries.domain.repository.StatSource
import com.ranker.countries.domain.usecase.GetDailyRankingsUseCase
import com.ranker.countries.domain.usecase.PublishSeedStatsUseCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Where the app's dependencies are wired, and nowhere else. The platform hands in the data source;
 * [debug] is only given in debug builds, where it replaces the published stats with new ones.
 */
@OptIn(ExperimentalTime::class)
class AppContainer(
    statRepository: StatRepository,
    debug: DebugPublishing? = null,
) {

    /** What the debug build publishes with, where the new stats come from, and the lock in front of it. */
    class DebugPublishing(val publisher: StatPublisher, val source: StatSource, val lock: DebugLock)

    val getDailyRankings = GetDailyRankingsUseCase(
        repository = statRepository,
        today = { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
    )

    /** Null outside debug builds. */
    val publishSeedStats: PublishSeedStatsUseCase? = debug?.let {
        PublishSeedStatsUseCase(lock = it.lock, publisher = it.publisher, source = it.source)
    }
}
