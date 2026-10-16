package com.ranker.countries.data.hardcoded

import com.ranker.countries.data.hardcoded.Countries.afghanistan
import com.ranker.countries.data.hardcoded.Countries.argentina
import com.ranker.countries.data.hardcoded.Countries.austria
import com.ranker.countries.data.hardcoded.Countries.bangladesh
import com.ranker.countries.data.hardcoded.Countries.bhutan
import com.ranker.countries.data.hardcoded.Countries.bolivia
import com.ranker.countries.data.hardcoded.Countries.brazil
import com.ranker.countries.data.hardcoded.Countries.chile
import com.ranker.countries.data.hardcoded.Countries.china
import com.ranker.countries.data.hardcoded.Countries.colombia
import com.ranker.countries.data.hardcoded.Countries.costaRica
import com.ranker.countries.data.hardcoded.Countries.drCongo
import com.ranker.countries.data.hardcoded.Countries.ethiopia
import com.ranker.countries.data.hardcoded.Countries.france
import com.ranker.countries.data.hardcoded.Countries.germany
import com.ranker.countries.data.hardcoded.Countries.hungary
import com.ranker.countries.data.hardcoded.Countries.india
import com.ranker.countries.data.hardcoded.Countries.indonesia
import com.ranker.countries.data.hardcoded.Countries.iran
import com.ranker.countries.data.hardcoded.Countries.italy
import com.ranker.countries.data.hardcoded.Countries.japan
import com.ranker.countries.data.hardcoded.Countries.kazakhstan
import com.ranker.countries.data.hardcoded.Countries.kyrgyzstan
import com.ranker.countries.data.hardcoded.Countries.liberia
import com.ranker.countries.data.hardcoded.Countries.malaysia
import com.ranker.countries.data.hardcoded.Countries.mexico
import com.ranker.countries.data.hardcoded.Countries.nepal
import com.ranker.countries.data.hardcoded.Countries.nigeria
import com.ranker.countries.data.hardcoded.Countries.pakistan
import com.ranker.countries.data.hardcoded.Countries.panama
import com.ranker.countries.data.hardcoded.Countries.papuaNewGuinea
import com.ranker.countries.data.hardcoded.Countries.peru
import com.ranker.countries.data.hardcoded.Countries.philippines
import com.ranker.countries.data.hardcoded.Countries.poland
import com.ranker.countries.data.hardcoded.Countries.russia
import com.ranker.countries.data.hardcoded.Countries.sierraLeone
import com.ranker.countries.data.hardcoded.Countries.spain
import com.ranker.countries.data.hardcoded.Countries.tajikistan
import com.ranker.countries.data.hardcoded.Countries.tanzania
import com.ranker.countries.data.hardcoded.Countries.turkey
import com.ranker.countries.data.hardcoded.Countries.unitedKingdom
import com.ranker.countries.data.hardcoded.Countries.unitedStates
import com.ranker.countries.data.hardcoded.Countries.zambia
import com.ranker.countries.domain.model.Country
import com.ranker.countries.domain.model.CountryValue
import com.ranker.countries.domain.model.Stat

/**
 * The set the debug build's seeding button writes to Firestore, replacing whatever is there: exactly
 * five stats, one tab each, in tab order. For a new set, change these and publish again. Like
 * [HardcodedStats], figures are rounded, commonly cited ones, good enough to try the app with, not
 * to quote; values are listed out of order and some stats have more than ten countries.
 */
internal object SeedStats {

    val population = Stat(
        id = "population",
        title = "Crowded nations",
        emoji = "👥",
        unit = "M",
        decimals = 0,
        fact = "India overtook China as the most populous country in 2023; together they hold over a third of humanity.",
        source = "Population, millions · UN 2024, approx.",
        values = listOf(
            china at 1_419.0, unitedStates at 345.0, india at 1_451.0, pakistan at 251.0, indonesia at 283.0,
            brazil at 212.0, nigeria at 233.0, russia at 145.0, bangladesh at 174.0, mexico at 131.0,
            ethiopia at 132.0, japan at 124.0,
        ),
    )

    val peaks = Stat(
        id = "peaks",
        title = "Sky-high summits",
        emoji = "🏔️",
        unit = "m",
        decimals = 0,
        fact = "Everest sits right on the Nepal–China border, and eight of the world's ten tallest peaks are in Nepal.",
        source = "Highest point per country · approx.",
        values = listOf(
            pakistan at 8_611.0, nepal at 8_849.0, india at 8_586.0, china at 8_849.0, tajikistan at 7_495.0,
            bhutan at 7_570.0, kyrgyzstan at 7_439.0, afghanistan at 7_492.0, argentina at 6_961.0,
            kazakhstan at 7_010.0, chile at 6_893.0, peru at 6_768.0, bolivia at 6_542.0,
        ),
    )

    val neighbours = Stat(
        id = "neighbours",
        title = "Most neighbours",
        emoji = "🤝",
        unit = "",
        decimals = 0,
        fact = "China and Russia each share a land border with 14 countries; Russia's run from Norway to North Korea.",
        source = "Countries sharing a land border · approx.",
        values = listOf(
            russia at 14.0, brazil at 10.0, china at 14.0, germany at 9.0, drCongo at 9.0, tanzania at 8.0,
            austria at 8.0, zambia at 8.0, turkey at 8.0, poland at 7.0, hungary at 7.0,
        ),
    )

    val heritage = Stat(
        id = "heritage",
        title = "World wonders",
        emoji = "🏛️",
        unit = "",
        decimals = 0,
        fact = "Italy has more UNESCO World Heritage Sites than any other country, from Venice to the Dolomites.",
        source = "UNESCO World Heritage Sites · 2025, approx.",
        values = listOf(
            china at 60.0, italy at 61.0, france at 54.0, germany at 55.0, india at 44.0, spain at 50.0,
            unitedKingdom at 35.0, mexico at 36.0, iran at 29.0, russia at 33.0, japan at 26.0,
            unitedStates at 26.0,
        ),
    )

    val rainfall = Stat(
        id = "rainfall",
        title = "Rainiest places",
        emoji = "🌧️",
        unit = "mm",
        decimals = 0,
        fact = "Parts of Colombia's Chocó rainforest get over 8 metres of rain a year, some of the wettest land on Earth.",
        source = "Average rainfall per year · World Bank, approx.",
        values = listOf(
            papuaNewGuinea at 3_142.0, colombia at 3_240.0, costaRica at 2_926.0, panama at 2_928.0,
            indonesia at 2_702.0, malaysia at 2_875.0, sierraLeone at 2_526.0, bangladesh at 2_666.0,
            philippines at 2_348.0, liberia at 2_391.0, japan at 1_668.0,
        ),
    )

    val all = listOf(population, peaks, neighbours, heritage, rainfall)

    private infix fun Country.at(value: Double) = CountryValue(this, value)
}
