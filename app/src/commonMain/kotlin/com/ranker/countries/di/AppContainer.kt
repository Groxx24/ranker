package com.ranker.countries.di

import com.ranker.countries.data.hardcoded.HardcodedStats
import com.ranker.countries.domain.repository.StatPublisher
import com.ranker.countries.domain.repository.StatRepository
import com.ranker.countries.domain.usecase.GetDailyRankingsUseCase
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Where the app's dependencies are wired, and nowhere else. The platform hands in the data source;
 * a [statPublisher] is only given in debug builds, where it seeds the source with sample stats.
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

    /** Writes the hardcoded sample stats to the source and says how many; null outside debug. */
    val publishSampleStats: (suspend () -> Int)? = statPublisher?.let { publisher ->
        {
            publisher.publish(HardcodedStats.all)
            HardcodedStats.all.size
        }
    }
}
