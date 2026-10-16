package com.ranker.countries.data.hardcoded

import com.ranker.countries.data.dailyRotation
import com.ranker.countries.domain.model.DailyStats
import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.StatRepository
import kotlinx.datetime.LocalDate

/**
 * Stands in for a backend, picking each day's stats from [pool] with [dailyRotation]. Also the
 * sample data the debug build seeds Firestore with.
 */
class HardcodedStatRepository internal constructor(
    private val pool: List<Stat>,
) : StatRepository {

    constructor() : this(HardcodedStats.all)

    override suspend fun statsFor(date: LocalDate): DailyStats = dailyRotation(pool, date)
}
