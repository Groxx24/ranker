package com.ranker.countries.di

import com.ranker.countries.data.hardcoded.SeedStats
import com.ranker.countries.domain.repository.DebugLock
import com.ranker.countries.domain.repository.StatPublisher
import com.ranker.countries.domain.repository.StatRepository
import com.ranker.countries.domain.usecase.GetDailyRankingsUseCase
import com.ranker.countries.domain.usecase.PublishSeedStatsUseCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Where the app's dependencies are wired, and nowhere else. The platform hands in the data source;
 * [debug] is only given in debug builds, where it seeds the source with [SeedStats].
 */
@OptIn(ExperimentalTime::class)
class AppContainer(
    statRepository: StatRepository,
    debug: DebugPublishing? = null,
) {

    /** What the debug build publishes with, and the lock in front of it. */
    class DebugPublishing(val publisher: StatPublisher, val lock: DebugLock)

    val getDailyRankings = GetDailyRankingsUseCase(
        repository = statRepository,
        today = { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
    )

    /** Null outside debug builds. */
    val publishSeedStats: PublishSeedStatsUseCase? = debug?.let {
        PublishSeedStatsUseCase(lock = it.lock, publisher = it.publisher, stats = SeedStats.all)
    }
}
