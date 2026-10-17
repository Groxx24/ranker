package com.ranker.countries.data.worldbank

import com.ranker.countries.domain.model.Continent
import com.ranker.countries.domain.model.Country
import com.ranker.countries.domain.model.CountryValue
import com.ranker.countries.domain.model.Stat
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * Reads the World Bank API's JSON. Every response is `[paging, rows]`; an error is a lone
 * `[{"message": …}]`, read here as no rows.
 */
internal object WorldBankJson {

    /** A country's latest figure is dropped when it is this many years older than the newest one. */
    const val MAX_AGE_YEARS = 5

    /** Fewer countries than this with a figure, and the indicator isn't worth a tab. */
    const val MIN_COUNTRIES = 20

    /**
     * Countries by ISO code, from `/v2/country`. Aggregates (regions, income groups) have region
     * "NA" and are left out, as are codes that aren't ISO countries, like the Channel Islands' JG.
     */
    fun countries(json: String): Map<String, Country> = buildMap {
        for (row in rows(json)) {
            val code = row.optString("iso2Code")
            val name = nameOf(code) ?: continue
            val continent = continentOf(
                code = code,
                region = row.optJSONObject("region")?.optString("id").orEmpty(),
                latitude = row.optString("latitude").toDoubleOrNull(),
                longitude = row.optString("longitude").toDoubleOrNull(),
            ) ?: continue
            put(code, Country(code, name, continent))
        }
    }

    /**
     * [indicator] as a stat, from `/v2/country/all/indicator/{code}?mrnev=1`, which gives each
     * country's most recent figure. Null when too few of [countries] have a recent one.
     */
    fun stat(indicator: Indicator, json: String, countries: Map<String, Country>): Stat? {
        val figures = rows(json).mapNotNull { row ->
            if (row.isNull("value")) return@mapNotNull null
            val country = countries[row.optJSONObject("country")?.optString("id")] ?: return@mapNotNull null
            val year = row.optString("date").toIntOrNull() ?: return@mapNotNull null
            Figure(country, year, row.getDouble("value") * indicator.scale)
        }
        val newest = figures.maxOfOrNull { it.year } ?: return null
        val recent = figures.filter { it.year >= newest - MAX_AGE_YEARS }
        if (recent.size < MIN_COUNTRIES) return null
        val oldest = recent.minOf { it.year }
        val values = recent.map { CountryValue(it.country, it.value) }
        return Stat(
            id = indicator.id,
            title = indicator.title,
            emoji = indicator.emoji,
            unit = indicator.unit,
            decimals = indicator.decimals,
            fact = factOf(values, indicator),
            source = "${indicator.measure} · World Bank " + if (oldest == newest) "$newest" else "$oldest–$newest",
            values = values,
        )
    }

    /** "Across 187 countries the median is 8.6 %; Japan's is 3.5× that." */
    internal fun factOf(values: List<CountryValue>, indicator: Indicator): String {
        val sorted = values.sortedBy { it.value }
        val median = sorted[sorted.size / 2].value
        val leader = sorted.last()
        val across = "Across ${values.size} countries the median is ${format(median, indicator)}"
        val ratio = leader.value / median
        return if (median > 0 && ratio >= 1.1) {
            "$across; ${leader.country.name}'s is ${String.format(Locale.ENGLISH, "%.1f", ratio)}× that."
        } else {
            "$across."
        }
    }

    private fun format(value: Double, indicator: Indicator): String {
        val number = String.format(Locale.ENGLISH, "%,.${indicator.decimals}f", value)
        return if (indicator.unit.isEmpty()) number else "$number ${indicator.unit}"
    }

    /**
     * The World Bank's regions split into continents, the way the UN's geoscheme does: Central Asia,
     * the Caucasus, Turkey and Cyprus are Asia, North Africa is Africa, Malta and Russia are Europe.
     * East Asia & Pacific has no list of its islands, so the Pacific ones are told apart by where
     * their capital is: south of 25°N and east of 130°E or across the date line.
     */
    internal fun continentOf(code: String, region: String, latitude: Double?, longitude: Double?): Continent? =
        when (region) {
            "SSF" -> Continent.AFRICA
            "SAS" -> Continent.ASIA
            "NAC" -> Continent.NORTH_AMERICA
            "LCN" -> if (code in SOUTH_AMERICA) Continent.SOUTH_AMERICA else Continent.NORTH_AMERICA
            "MEA" -> when (code) {
                in NORTH_AFRICA -> Continent.AFRICA
                "MT" -> Continent.EUROPE
                else -> Continent.ASIA
            }
            "EAS" -> if (latitude != null && longitude != null && latitude < 25 && (longitude > 130 || longitude < -100)) {
                Continent.OCEANIA
            } else {
                Continent.ASIA
            }
            "ECS" -> when (code) {
                "GL" -> Continent.NORTH_AMERICA
                in ASIA_IN_EUROPE_REGION -> Continent.ASIA
                else -> Continent.EUROPE
            }
            else -> null
        }

    /** A short English name, or null when [code] isn't a country (Kosovo counts, unlike in ISO). */
    internal fun nameOf(code: String): String? {
        if (code !in ISO_COUNTRIES && code != "XK") return null
        return NAMES[code] ?: Locale.Builder().setRegion(code).build().getDisplayCountry(Locale.ENGLISH)
    }

    private fun rows(json: String): List<JSONObject> {
        val rows = JSONArray(json).optJSONArray(1) ?: return emptyList()
        return List(rows.length()) { rows.getJSONObject(it) }
    }

    private class Figure(val country: Country, val year: Int, val value: Double)

    private val ISO_COUNTRIES = Locale.getISOCountries().toSet()
    private val SOUTH_AMERICA = setOf("AR", "BO", "BR", "CL", "CO", "EC", "GY", "PE", "PY", "SR", "UY", "VE")
    private val NORTH_AFRICA = setOf("DZ", "DJ", "EG", "LY", "MA", "TN")
    private val ASIA_IN_EUROPE_REGION = setOf("AM", "AZ", "CY", "GE", "KG", "KZ", "TJ", "TM", "TR", "UZ")

    /** Where the system's name is long or awkward for a bar label. */
    private val NAMES = mapOf(
        "AE" to "UAE",
        "BA" to "Bosnia & Herz.",
        "CD" to "DR Congo",
        "CF" to "Central African Rep.",
        "CG" to "Congo",
        "HK" to "Hong Kong",
        "MM" to "Myanmar",
        "MO" to "Macao",
        "MP" to "N. Mariana Is.",
        "PS" to "Palestine",
        "TC" to "Turks & Caicos",
        "VC" to "St. Vincent",
        "VG" to "British Virgin Is.",
        "VI" to "US Virgin Is.",
    )
}
