package com.ranker.countries.data.hardcoded

import com.ranker.countries.domain.model.DailyStats
import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.StatRepository
import com.ranker.countries.domain.repository.StatRepository.Companion.DAILY_STAT_COUNT
import kotlinx.datetime.LocalDate

/** Stands in for a backend: always the first [DAILY_STAT_COUNT] stats of [pool], whatever the day. */
class HardcodedStatRepository internal constructor(
    private val pool: List<Stat>,
) : StatRepository {

    constructor() : this(HardcodedStats.all)

    override suspend fun statsFor(date: LocalDate): DailyStats = DailyStats(date, pool.take(DAILY_STAT_COUNT))
}
