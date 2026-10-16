package com.ranker.app.domain.usecase

import com.ranker.app.domain.model.Continent
import com.ranker.app.domain.model.Country
import com.ranker.app.domain.model.CountryValue
import com.ranker.app.domain.model.DailyStats
import com.ranker.app.domain.model.Stat
import com.ranker.app.domain.repository.StatRepository
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class GetDailyRankingsUseCaseTest {

    private val day = LocalDate(2026, 10, 16)

    private fun country(code: String, continent: Continent = Continent.EUROPE) = Country(code, code, continent)

    private fun stat(vararg values: Pair<Country, Double>) = Stat(
        id = "s", title = "S", emoji = "", unit = "", decimals = 0, fact = "", source = "",
        values = values.map { (country, value) -> CountryValue(country, value) },
    )

    private fun useCase(vararg stats: Stat) = GetDailyRankingsUseCase(
        repository = object : StatRepository {
            override suspend fun statsFor(date: LocalDate) = DailyStats(date, stats.toList())
        },
        today = { day },
    )

    @Test
    fun ranksHighestFirstAndKeepsTheTopTen() = runTest {
        val values = (1..12).map { country("C$it") to it.toDouble() }.shuffled()

        val ranking = useCase(stat(*values.toTypedArray()))().rankings.single()

        assertEquals(10, ranking.entries.size)
        assertEquals((12 downTo 3).map { "C$it" }, ranking.entries.map { it.country.code })
        assertEquals((1..10).toList(), ranking.entries.map { it.rank })
    }

    @Test
    fun breaksTiesByName() = runTest {
        val ranking = useCase(stat(country("B") to 5.0, country("A") to 5.0))().rankings.single()

        assertEquals(listOf("A", "B"), ranking.entries.map { it.country.code })
    }

    @Test
    fun worksOutTheInsights() = runTest {
        val ranking = useCase(
            stat(
                country("A") to 12.0,
                country("B") to 10.0,
                country("C", Continent.ASIA) to 4.0,
            ),
        )().rankings.single()

        assertEquals(3.0, ranking.insights.leaderToLastRatio, 1e-9)
        assertEquals(20.0, ranking.insights.leadOverRunnerUpPercent, 1e-9)
        assertEquals(Continent.EUROPE, ranking.insights.topContinent)
        assertEquals(2, ranking.insights.topContinentCount)
        assertEquals(listOf(1.0, 10.0 / 12, 4.0 / 12), ranking.entries.map { it.shareOfLeader })
    }

    @Test
    fun leavesOutAStatThatCannotBeRanked() = runTest {
        val rankings = useCase(stat(country("A") to 1.0), stat(country("A") to 1.0, country("B") to 2.0))().rankings

        assertEquals(1, rankings.size)
    }
}
