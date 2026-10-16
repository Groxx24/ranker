package com.ranker.countries.domain.repository

import com.ranker.countries.domain.model.DailyStats
import kotlinx.datetime.LocalDate

interface StatRepository {
    /**
     * The stats to show on [date]: the set last published, in its order, the same every day until
     * a new one replaces it; [DAILY_STAT_COUNT] at most.
     */
    suspend fun statsFor(date: LocalDate): DailyStats

    companion object {
        /** How many stats a published set has, one tab each. */
        const val DAILY_STAT_COUNT = 5
    }
}
