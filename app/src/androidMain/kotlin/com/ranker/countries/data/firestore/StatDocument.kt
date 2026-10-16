package com.ranker.countries.data.firestore

import com.ranker.countries.domain.model.Continent
import com.ranker.countries.domain.model.Country
import com.ranker.countries.domain.model.CountryValue
import com.ranker.countries.domain.model.Stat

/**
 * How a [Stat] is laid out in Firestore. The document id is the stat's id; the rest are fields:
 *
 * ```
 * order: 0, title: "Coffee lovers", emoji: "☕", unit: "kg", decimals: 1, fact: "…", source: "…",
 * values: [{ code: "FI", name: "Finland", continent: "EUROPE", value: 12.0 }, …]
 * ```
 *
 * Countries are stored inline, so a stat's document is all it takes to rank it.
 */
internal object StatDocument {
    const val ORDER = "order"

    fun encode(stat: Stat, order: Int): Map<String, Any> = mapOf(
        ORDER to order,
        "title" to stat.title,
        "emoji" to stat.emoji,
        "unit" to stat.unit,
        "decimals" to stat.decimals,
        "fact" to stat.fact,
        "source" to stat.source,
        "values" to stat.values.map {
            mapOf(
                "code" to it.country.code,
                "name" to it.country.name,
                "continent" to it.country.continent.name,
                "value" to it.value,
            )
        },
    )

    /** Null when a field the app needs is missing or of the wrong type; a bad value is skipped. */
    fun decode(id: String, data: Map<String, Any?>): Stat? {
        val values = (data["values"] as? List<*>)?.mapNotNull { decodeValue(it as? Map<*, *>) } ?: return null
        return Stat(
            id = id,
            title = data["title"] as? String ?: return null,
            emoji = data["emoji"] as? String ?: "",
            unit = data["unit"] as? String ?: "",
            decimals = (data["decimals"] as? Number)?.toInt() ?: 0,
            fact = data["fact"] as? String ?: "",
            source = data["source"] as? String ?: "",
            values = values,
        )
    }

    private fun decodeValue(entry: Map<*, *>?): CountryValue? {
        entry ?: return null
        val continent = (entry["continent"] as? String)
            ?.let { name -> Continent.entries.firstOrNull { it.name == name } }
            ?: return null
        return CountryValue(
            country = Country(
                code = entry["code"] as? String ?: return null,
                name = entry["name"] as? String ?: return null,
                continent = continent,
            ),
            // Firestore hands whole numbers back as Long.
            value = (entry["value"] as? Number)?.toDouble() ?: return null,
        )
    }
}
