package com.ranker.countries.data

import com.ranker.countries.domain.model.DailyStats
import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.StatRepository.Companion.DAILY_STAT_COUNT
import kotlinx.datetime.LocalDate

/**
 * The stats of [date]: the next [DAILY_STAT_COUNT] of [pool], wrapping around, so the tabs change
 * from one day to the next. Every source picks the same way, so switching sources keeps the day.
 */
internal fun dailyRotation(pool: List<Stat>, date: LocalDate): DailyStats {
    if (pool.isEmpty()) return DailyStats(date, emptyList())
    val start = date.toEpochDays().mod(pool.size)
    val count = minOf(DAILY_STAT_COUNT, pool.size)
    return DailyStats(date, List(count) { pool[(start + it) % pool.size] })
}
