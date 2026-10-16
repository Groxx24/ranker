package com.ranker.app.domain.usecase

import com.ranker.app.domain.model.CountryValue
import com.ranker.app.domain.model.DailyRankings
import com.ranker.app.domain.model.RankedCountry
import com.ranker.app.domain.model.Ranking
import com.ranker.app.domain.model.RankingInsights
import com.ranker.app.domain.model.Stat
import com.ranker.app.domain.repository.StatRepository
import kotlinx.datetime.LocalDate

/**
 * Today's stats, each turned into a top 10: highest value first, ties by country name so the order
 * never jumps between runs. A stat with fewer than two countries can't be ranked and is left out.
 */
class GetDailyRankingsUseCase(
    private val repository: StatRepository,
    private val today: () -> LocalDate,
) {
    suspend operator fun invoke(): DailyRankings {
        val daily = repository.statsFor(today())
        return DailyRankings(
            date = daily.date,
            rankings = daily.stats.mapNotNull(::rank),
        )
    }

    private fun rank(stat: Stat): Ranking? {
        val top = stat.values
            .sortedWith(compareByDescending<CountryValue> { it.value }.thenBy { it.country.name })
            .take(Ranking.TOP_COUNT)
        if (top.size < 2) return null
        val leaderValue = top.first().value
        val entries = top.mapIndexed { index, it ->
            RankedCountry(
                rank = index + 1,
                country = it.country,
                value = it.value,
                shareOfLeader = if (leaderValue > 0) it.value / leaderValue else 0.0,
            )
        }
        return Ranking(stat, entries, insightsOf(entries))
    }

    private fun insightsOf(entries: List<RankedCountry>): RankingInsights {
        val leader = entries.first().value
        val runnerUp = entries[1].value
        val last = entries.last().value
        val (continent, count) = entries
            .groupingBy { it.country.continent }
            .eachCount()
            .maxBy { it.value }
            .toPair()
        return RankingInsights(
            leaderToLastRatio = if (last > 0) leader / last else 0.0,
            leadOverRunnerUpPercent = if (runnerUp > 0) (leader - runnerUp) / runnerUp * 100 else 0.0,
            topContinent = continent,
            topContinentCount = count,
        )
    }
}
