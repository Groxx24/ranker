package com.ranker.app.data.hardcoded

import com.ranker.app.domain.model.DailyStats
import com.ranker.app.domain.model.Stat
import com.ranker.app.domain.repository.StatRepository
import com.ranker.app.domain.repository.StatRepository.Companion.DAILY_STAT_COUNT
import kotlinx.datetime.LocalDate

/**
 * Stands in for a backend: each day takes the next [DAILY_STAT_COUNT] stats of [pool], wrapping
 * around, so the tabs change from one day to the next. Swapping in a real source means another
 * [StatRepository] in `AppContainer`; nothing above the data layer changes.
 */
class HardcodedStatRepository internal constructor(
    private val pool: List<Stat>,
) : StatRepository {

    constructor() : this(HardcodedStats.all)

    override suspend fun statsFor(date: LocalDate): DailyStats {
        val start = date.toEpochDays().mod(pool.size)
        val count = minOf(DAILY_STAT_COUNT, pool.size)
        return DailyStats(date, List(count) { pool[(start + it) % pool.size] })
    }
}
