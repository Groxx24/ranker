package com.ranker.app.domain.model

/** A country, identified by its ISO 3166-1 alpha-2 [code] (the UI draws its flag from it). */
data class Country(
    val code: String,
    val name: String,
    val continent: Continent,
)

enum class Continent { AFRICA, ASIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA, OCEANIA }
