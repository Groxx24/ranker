package com.ranker.countries.data.worldbank

import com.ranker.countries.domain.model.Continent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.IOException
import kotlin.random.Random

class WorldBankTest {

    @Test
    fun readsCountriesAndLeavesOutAggregatesAndNonIsoCodes() {
        val countries = WorldBankJson.countries(COUNTRIES)

        assertEquals(setOf("JP", "PW", "EG", "BR", "XK", "HK"), countries.keys)
        assertEquals("Japan", countries.getValue("JP").name)
        assertEquals("Hong Kong", countries.getValue("HK").name)
        assertEquals("Kosovo", countries.getValue("XK").name)
    }

    @Test
    fun splitsWorldBankRegionsIntoContinents() {
        fun continent(code: String, region: String, lat: Double? = null, lon: Double? = null) =
            WorldBankJson.continentOf(code, region, lat, lon)

        assertEquals(Continent.ASIA, continent("JP", "EAS", 35.67, 139.77))
        assertEquals(Continent.ASIA, continent("TL", "EAS", -8.57, 125.57))
        assertEquals(Continent.OCEANIA, continent("PW", "EAS", 7.34, 134.48))
        assertEquals(Continent.OCEANIA, continent("TO", "EAS", -21.14, -175.22))
        assertEquals(Continent.ASIA, continent("XX", "EAS"))
        assertEquals(Continent.AFRICA, continent("EG", "MEA"))
        assertEquals(Continent.EUROPE, continent("MT", "MEA"))
        assertEquals(Continent.ASIA, continent("IR", "MEA"))
        assertEquals(Continent.ASIA, continent("KZ", "ECS"))
        assertEquals(Continent.EUROPE, continent("RU", "ECS"))
        assertEquals(Continent.NORTH_AMERICA, continent("GL", "ECS"))
        assertEquals(Continent.SOUTH_AMERICA, continent("BR", "LCN"))
        assertEquals(Continent.NORTH_AMERICA, continent("PA", "LCN"))
        assertEquals(Continent.AFRICA, continent("NG", "SSF"))
        assertNull(continent("ZH", "NA"))
    }

    @Test
    fun turnsAnIndicatorIntoAStatOfRecentFigures() {
        val countries = manyCountries(25)
        val rows = countries.keys.mapIndexed { i, code -> row(code, 2024, (i + 1) * 1_000_000.0) } +
            row("ZZ", 2024, 5.0) +
            """{"country":{"id":"${countries.keys.first()}"},"date":"2025","value":null}"""
        val stat = WorldBankJson.stat(POPULATION, page(rows), countries)!!

        assertEquals("sp-pop-totl", stat.id)
        assertEquals(25, stat.values.size)
        assertEquals(25.0, stat.values.maxOf { it.value }, 1e-9)
        assertEquals("Population, millions · World Bank 2024", stat.source)
        assertTrue(stat.fact, stat.fact.startsWith("Across 25 countries the median is 13 M; "))
    }

    @Test
    fun dropsFiguresMuchOlderThanTheNewestAndSaysWhichYears() {
        val countries = manyCountries(25)
        val codes = countries.keys.toList()
        val rows = codes.drop(1).mapIndexed { i, code -> row(code, 2020 + i % 5, 1.0 + i) } + row(codes[0], 2010, 1e12)
        val stat = WorldBankJson.stat(POPULATION, page(rows), countries)!!

        assertEquals(24, stat.values.size)
        assertEquals("Population, millions · World Bank 2020–2024", stat.source)
    }

    @Test
    fun passesOverAnIndicatorWithTooFewCountriesOrAnError() {
        val countries = manyCountries(25)
        assertNull(WorldBankJson.stat(POPULATION, page(countries.keys.take(5).map { row(it, 2024, 1.0) }), countries))
        assertNull(WorldBankJson.stat(POPULATION, """[{"message":[{"id":"120","value":"Invalid value"}]}]""", countries))
    }

    @Test
    fun picksDifferentIndicatorsThanTheAvoidedOnesAndTopsUpForMissingData() = runTest {
        val countries = manyCountries(25)
        val pool = (1..8).map { Indicator("I.$it", "T$it", "", "M$it", "", 0) }
        val source = WorldBankStatSource(pool, Random(1)) { url ->
            when {
                "/indicator/" !in url -> countriesPage(countries.keys)
                "/indicator/I.3?" in url -> "[{\"message\":[]}]"
                else -> page(countries.keys.mapIndexed { i, code -> row(code, 2024, i.toDouble()) })
            }
        }

        val stats = source.freshStats(5, avoid = setOf("i-1", "i-2"))

        assertEquals(5, stats.size)
        assertEquals(setOf("i-4", "i-5", "i-6", "i-7", "i-8"), stats.map { it.id }.toSet())
    }

    @Test
    fun failsWhenNotEnoughIndicatorsHaveData() = runTest {
        val pool = (1..6).map { Indicator("I.$it", "T$it", "", "M$it", "", 0) }
        val source = WorldBankStatSource(pool, Random(1)) { url ->
            if ("/indicator/I.1?" in url) page(listOf(row("AD", 2024, 1.0))) else "[{\"message\":[]}]"
        }
        try {
            source.freshStats(5, avoid = emptySet())
            fail("published a short set")
        } catch (expected: IOException) {
        }
    }

    private companion object {
        val POPULATION = Indicators.all.first { it.code == "SP.POP.TOTL" }

        val COUNTRIES = """
            [{"page":1},[
              {"iso2Code":"JP","region":{"id":"EAS"},"latitude":"35.67","longitude":"139.77"},
              {"iso2Code":"PW","region":{"id":"EAS"},"latitude":"7.34","longitude":"134.48"},
              {"iso2Code":"EG","region":{"id":"MEA"},"latitude":"30.1","longitude":"31.2"},
              {"iso2Code":"BR","region":{"id":"LCN"},"latitude":"-15.8","longitude":"-47.9"},
              {"iso2Code":"XK","region":{"id":"ECS"},"latitude":"42.6","longitude":"21.2"},
              {"iso2Code":"HK","region":{"id":"EAS"},"latitude":"22.4","longitude":"114.1"},
              {"iso2Code":"JG","region":{"id":"ECS"},"latitude":"","longitude":""},
              {"iso2Code":"ZH","region":{"id":"NA"},"latitude":"","longitude":""}
            ]]
        """.trimIndent()

        /** The first [n] ISO countries, all in Africa as far as these tests care. */
        fun manyCountries(n: Int) = WorldBankJson.countries(countriesPage(java.util.Locale.getISOCountries().take(n)))

        fun countriesPage(codes: Collection<String>) =
            page(codes.map { """{"iso2Code":"$it","region":{"id":"SSF"},"latitude":"0","longitude":"0"}""" })

        fun row(code: String, year: Int, value: Double) =
            """{"indicator":{"id":"X"},"country":{"id":"$code"},"date":"$year","value":$value}"""

        fun page(rows: List<String>) = """[{"page":1},[${rows.joinToString(",")}]]"""
    }
}
