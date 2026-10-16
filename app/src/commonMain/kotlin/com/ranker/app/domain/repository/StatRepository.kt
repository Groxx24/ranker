package com.ranker.app.domain.repository

import com.ranker.app.domain.model.DailyStats
import kotlinx.datetime.LocalDate

interface StatRepository {
    /** The stats of [date]; [DAILY_STAT_COUNT] of them when the source has that many. */
    suspend fun statsFor(date: LocalDate): DailyStats

    companion object {
        const val DAILY_STAT_COUNT = 5
    }
}
