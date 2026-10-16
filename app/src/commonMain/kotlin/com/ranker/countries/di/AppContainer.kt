package com.ranker.countries.di

import com.ranker.countries.data.hardcoded.SeedStats
import com.ranker.countries.domain.repository.StatPublisher
import com.ranker.countries.domain.repository.StatRepository
import com.ranker.countries.domain.usecase.GetDailyRankingsUseCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Where the app's dependencies are wired, and nowhere else. The platform hands in the data source;
 * a [statPublisher] is only given in debug builds, where it seeds the source with [SeedStats].
 */
@OptIn(ExperimentalTime::class)
class AppContainer(
    statRepository: StatRepository,
    statPublisher: StatPublisher? = null,
) {

    val getDailyRankings = GetDailyRankingsUseCase(
        repository = statRepository,
        today = { Clock.System.todayIn(TimeZone.currentSystemDefault()) },
    )

    /** Replaces what the source holds with [SeedStats] and says how many; null outside debug. */
    val publishSampleStats: (suspend () -> Int)? = statPublisher?.let { publisher ->
        {
            publisher.publish(SeedStats.all)
            SeedStats.all.size
        }
    }
}
