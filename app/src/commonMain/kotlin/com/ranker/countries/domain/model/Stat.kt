package com.ranker.countries.domain.model

import kotlinx.datetime.LocalDate

/**
 * One fact measured across countries, as the data source has it: any number of [values], in no
 * particular order. Ranking them is the domain's job, see [Ranking].
 */
data class Stat(
    val id: String,
    val title: String,
    val emoji: String,
    /** Shown after each value, e.g. "kg"; empty for plain counts. */
    val unit: String,
    /** How many decimals a value is shown with. */
    val decimals: Int,
    /** A human line about the stat, shown under the chart's title. */
    val fact: String,
    val source: String,
    val values: List<CountryValue>,
)

data class CountryValue(val country: Country, val value: Double)

/** The stats picked for one day, before ranking. */
data class DailyStats(
    val date: LocalDate,
    val stats: List<Stat>,
)
