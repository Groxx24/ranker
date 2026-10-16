package com.ranker.countries.data.hardcoded

import com.ranker.countries.domain.model.Continent.AFRICA
import com.ranker.countries.domain.model.Continent.ASIA
import com.ranker.countries.domain.model.Continent.EUROPE
import com.ranker.countries.domain.model.Continent.NORTH_AMERICA
import com.ranker.countries.domain.model.Continent.OCEANIA
import com.ranker.countries.domain.model.Continent.SOUTH_AMERICA
import com.ranker.countries.domain.model.Country

/** Every country the hardcoded stats mention. */
internal object Countries {
    val algeria = Country("DZ", "Algeria", AFRICA)
    val argentina = Country("AR", "Argentina", SOUTH_AMERICA)
    val australia = Country("AU", "Australia", OCEANIA)
    val austria = Country("AT", "Austria", EUROPE)
    val belgium = Country("BE", "Belgium", EUROPE)
    val bosnia = Country("BA", "Bosnia & Herz.", EUROPE)
    val brazil = Country("BR", "Brazil", SOUTH_AMERICA)
    val canada = Country("CA", "Canada", NORTH_AMERICA)
    val chile = Country("CL", "Chile", SOUTH_AMERICA)
    val china = Country("CN", "China", ASIA)
    val czechia = Country("CZ", "Czechia", EUROPE)
    val denmark = Country("DK", "Denmark", EUROPE)
    val egypt = Country("EG", "Egypt", AFRICA)
    val estonia = Country("EE", "Estonia", EUROPE)
    val finland = Country("FI", "Finland", EUROPE)
    val france = Country("FR", "France", EUROPE)
    val germany = Country("DE", "Germany", EUROPE)
    val greece = Country("GR", "Greece", EUROPE)
    val iceland = Country("IS", "Iceland", EUROPE)
    val india = Country("IN", "India", ASIA)
    val indonesia = Country("ID", "Indonesia", ASIA)
    val iran = Country("IR", "Iran", ASIA)
    val ireland = Country("IE", "Ireland", EUROPE)
    val italy = Country("IT", "Italy", EUROPE)
    val japan = Country("JP", "Japan", ASIA)
    val kazakhstan = Country("KZ", "Kazakhstan", ASIA)
    val latvia = Country("LV", "Latvia", EUROPE)
    val luxembourg = Country("LU", "Luxembourg", EUROPE)
    val mexico = Country("MX", "Mexico", NORTH_AMERICA)
    val montenegro = Country("ME", "Montenegro", EUROPE)
    val morocco = Country("MA", "Morocco", AFRICA)
    val netherlands = Country("NL", "Netherlands", EUROPE)
    val newZealand = Country("NZ", "New Zealand", OCEANIA)
    val norway = Country("NO", "Norway", EUROPE)
    val philippines = Country("PH", "Philippines", ASIA)
    val poland = Country("PL", "Poland", EUROPE)
    val russia = Country("RU", "Russia", EUROPE)
    val serbia = Country("RS", "Serbia", EUROPE)
    val slovakia = Country("SK", "Slovakia", EUROPE)
    val slovenia = Country("SI", "Slovenia", EUROPE)
    val spain = Country("ES", "Spain", EUROPE)
    val sweden = Country("SE", "Sweden", EUROPE)
    val switzerland = Country("CH", "Switzerland", EUROPE)
    val turkey = Country("TR", "Türkiye", ASIA)
    val unitedKingdom = Country("GB", "United Kingdom", EUROPE)
    val unitedStates = Country("US", "United States", NORTH_AMERICA)
}
