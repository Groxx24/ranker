package com.ranker.countries.data.hardcoded

import com.ranker.countries.domain.repository.StatRepository.Companion.DAILY_STAT_COUNT
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import kotlinx.datetime.DatePeriod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HardcodedStatRepositoryTest {

    private val repository = HardcodedStatRepository()
    private val day = LocalDate(2026, 10, 16)

    @Test
    fun givesFiveDifferentStatsADay() = runTest {
        val ids = repository.statsFor(day).stats.map { it.id }

        assertEquals(DAILY_STAT_COUNT, ids.size)
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun changesFromOneDayToTheNext() = runTest {
        val today = repository.statsFor(day).stats.map { it.id }
        val tomorrow = repository.statsFor(day.plus(DatePeriod(days = 1))).stats.map { it.id }

        assertNotEquals(today, tomorrow)
    }

    @Test
    fun everyStatHasAtLeastTenCountries() {
        HardcodedStats.all.forEach { stat ->
            assertTrue(stat.id, stat.values.size >= 10)
            assertEquals(stat.id, stat.values.size, stat.values.map { it.country }.toSet().size)
        }
    }
}
