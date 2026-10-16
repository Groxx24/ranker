package com.ranker.countries.data.hardcoded

import com.ranker.countries.data.hardcoded.Countries.algeria
import com.ranker.countries.data.hardcoded.Countries.argentina
import com.ranker.countries.data.hardcoded.Countries.australia
import com.ranker.countries.data.hardcoded.Countries.austria
import com.ranker.countries.data.hardcoded.Countries.belgium
import com.ranker.countries.data.hardcoded.Countries.bosnia
import com.ranker.countries.data.hardcoded.Countries.brazil
import com.ranker.countries.data.hardcoded.Countries.canada
import com.ranker.countries.data.hardcoded.Countries.chile
import com.ranker.countries.data.hardcoded.Countries.china
import com.ranker.countries.data.hardcoded.Countries.czechia
import com.ranker.countries.data.hardcoded.Countries.denmark
import com.ranker.countries.data.hardcoded.Countries.egypt
import com.ranker.countries.data.hardcoded.Countries.estonia
import com.ranker.countries.data.hardcoded.Countries.finland
import com.ranker.countries.data.hardcoded.Countries.france
import com.ranker.countries.data.hardcoded.Countries.germany
import com.ranker.countries.data.hardcoded.Countries.greece
import com.ranker.countries.data.hardcoded.Countries.iceland
import com.ranker.countries.data.hardcoded.Countries.india
import com.ranker.countries.data.hardcoded.Countries.indonesia
import com.ranker.countries.data.hardcoded.Countries.iran
import com.ranker.countries.data.hardcoded.Countries.ireland
import com.ranker.countries.data.hardcoded.Countries.italy
import com.ranker.countries.data.hardcoded.Countries.japan
import com.ranker.countries.data.hardcoded.Countries.kazakhstan
import com.ranker.countries.data.hardcoded.Countries.latvia
import com.ranker.countries.data.hardcoded.Countries.luxembourg
import com.ranker.countries.data.hardcoded.Countries.mexico
import com.ranker.countries.data.hardcoded.Countries.montenegro
import com.ranker.countries.data.hardcoded.Countries.morocco
import com.ranker.countries.data.hardcoded.Countries.netherlands
import com.ranker.countries.data.hardcoded.Countries.newZealand
import com.ranker.countries.data.hardcoded.Countries.norway
import com.ranker.countries.data.hardcoded.Countries.philippines
import com.ranker.countries.data.hardcoded.Countries.poland
import com.ranker.countries.data.hardcoded.Countries.russia
import com.ranker.countries.data.hardcoded.Countries.serbia
import com.ranker.countries.data.hardcoded.Countries.slovakia
import com.ranker.countries.data.hardcoded.Countries.slovenia
import com.ranker.countries.data.hardcoded.Countries.spain
import com.ranker.countries.data.hardcoded.Countries.sweden
import com.ranker.countries.data.hardcoded.Countries.switzerland
import com.ranker.countries.data.hardcoded.Countries.turkey
import com.ranker.countries.data.hardcoded.Countries.unitedKingdom
import com.ranker.countries.data.hardcoded.Countries.unitedStates
import com.ranker.countries.domain.model.Country
import com.ranker.countries.domain.model.CountryValue
import com.ranker.countries.domain.model.Stat

/**
 * The pool the day's stats are picked from. Figures are rounded, commonly cited ones, good enough
 * to try the app with, not to quote. Values are deliberately listed out of order and some stats
 * have more than ten countries: ranking is the domain's job.
 */
internal object HardcodedStats {

    val coffee = Stat(
        id = "coffee",
        title = "Coffee lovers",
        emoji = "☕",
        unit = "kg",
        decimals = 1,
        fact = "Finns drink about four cups a day, and two coffee breaks are written into many work contracts.",
        source = "Coffee per person per year · ICO, approx.",
        values = listOf(
            norway at 9.9, finland at 12.0, denmark at 8.7, iceland at 9.0, netherlands at 8.4,
            switzerland at 7.9, sweden at 8.2, canada at 6.5, belgium at 6.8, luxembourg at 6.5,
            bosnia at 6.2, austria at 6.1,
        ),
    )

    val islands = Stat(
        id = "islands",
        title = "Island nations",
        emoji = "🏝️",
        unit = "",
        decimals = 0,
        fact = "Sweden counts every rock over 25 m² above the water, which is how it ends up with over a quarter of a million islands.",
        source = "Islands per country · national surveys, approx.",
        values = listOf(
            finland at 178_947.0, sweden at 267_570.0, canada at 52_455.0, norway at 239_057.0,
            indonesia at 17_508.0, unitedStates at 18_617.0, japan at 14_125.0, philippines at 7_641.0,
            australia at 8_222.0, greece at 6_000.0, chile at 5_000.0,
        ),
    )

    val height = Stat(
        id = "height",
        title = "Tallest people",
        emoji = "📏",
        unit = "cm",
        decimals = 1,
        fact = "Dutch men grew almost 20 cm in 150 years; the Balkans are catching up fast.",
        source = "Average height of men · NCD-RisC, approx.",
        values = listOf(
            montenegro at 183.3, netherlands at 183.8, bosnia at 182.5, estonia at 182.8,
            denmark at 181.9, iceland at 182.1, latvia at 181.2, czechia at 181.2,
            slovenia at 180.9, slovakia at 181.0, serbia at 180.6,
        ),
    )

    val tea = Stat(
        id = "tea",
        title = "Tea drinkers",
        emoji = "🍵",
        unit = "kg",
        decimals = 2,
        fact = "Türkiye drinks more tea per person than anyone, mostly as small glasses of strong black çay, all day long.",
        source = "Tea per person per year · approx.",
        values = listOf(
            ireland at 2.19, turkey at 3.16, iran at 1.50, unitedKingdom at 1.94, morocco at 1.22,
            russia at 1.38, egypt at 1.01, newZealand at 1.19, japan at 0.97, poland at 1.00,
            china at 0.57,
        ),
    )

    val chocolate = Stat(
        id = "chocolate",
        title = "Chocolate cravings",
        emoji = "🍫",
        unit = "kg",
        decimals = 1,
        fact = "The Swiss eat close to 9 kg of chocolate a year each, about three bars a week.",
        source = "Chocolate per person per year · approx.",
        values = listOf(
            austria at 8.1, switzerland at 8.8, ireland at 7.9, germany at 7.9, sweden at 6.6,
            unitedKingdom at 7.6, norway at 6.3, estonia at 6.5, poland at 5.7, denmark at 5.9,
            belgium at 5.5,
        ),
    )

    val tourism = Stat(
        id = "tourism",
        title = "Tourist magnets",
        emoji = "✈️",
        unit = "M",
        decimals = 1,
        fact = "France welcomes about 100 million visitors a year, more than its own population.",
        source = "International arrivals, millions · UN Tourism 2023, approx.",
        values = listOf(
            spain at 85.2, france at 100.0, italy at 57.2, unitedStates at 66.5, mexico at 42.2,
            turkey at 55.2, germany at 34.8, unitedKingdom at 37.2, austria at 30.9, greece at 32.7,
        ),
    )

    val area = Stat(
        id = "area",
        title = "Giant lands",
        emoji = "🗺️",
        unit = "M km²",
        decimals = 2,
        fact = "Russia spans 11 time zones; it is bigger than Pluto's whole surface.",
        source = "Total area, millions of km² · approx.",
        values = listOf(
            canada at 9.98, russia at 17.10, china at 9.60, unitedStates at 9.83, australia at 7.69,
            brazil at 8.52, argentina at 2.78, india at 3.29, algeria at 2.38, kazakhstan at 2.72,
        ),
    )

    val all = listOf(coffee, islands, height, tea, chocolate, tourism, area)

    private infix fun Country.at(value: Double) = CountryValue(this, value)
}
