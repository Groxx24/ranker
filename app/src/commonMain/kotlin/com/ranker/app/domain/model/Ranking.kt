package com.ranker.app.domain.model

import kotlinx.datetime.LocalDate

/** The top [TOP_COUNT] countries of one [stat], highest value first. */
data class Ranking(
    val stat: Stat,
    val entries: List<RankedCountry>,
    val insights: RankingInsights,
) {
    val leader: RankedCountry get() = entries.first()

    companion object {
        const val TOP_COUNT = 10
    }
}

data class RankedCountry(
    /** 1 for the leader. */
    val rank: Int,
    val country: Country,
    val value: Double,
    /** [value] as a share of the leader's, 0 to 1; what a bar's length is drawn from. */
    val shareOfLeader: Double,
)

/** What stands out in a ranking, worked out from its entries. */
data class RankingInsights(
    /** How many times the last place's value the leader's is. */
    val leaderToLastRatio: Double,
    /** How far ahead of number 2 the leader is, in percent of number 2's value. */
    val leadOverRunnerUpPercent: Double,
    /** The continent with the most countries in the ranking, and how many. */
    val topContinent: Continent,
    val topContinentCount: Int,
)

/** Today's rankings, one per tab. */
data class DailyRankings(
    val date: LocalDate,
    val rankings: List<Ranking>,
)
