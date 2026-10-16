package com.ranker.app.ui.format

/**
 * The flag emoji of an ISO 3166-1 alpha-2 code: each letter becomes its regional indicator symbol
 * (U+1F1E6 for A), written here as its UTF-16 surrogate pair.
 */
fun flagEmoji(countryCode: String): String = buildString {
    for (letter in countryCode.uppercase()) {
        if (letter !in 'A'..'Z') return countryCode
        append('\uD83C')
        append(Char(0xDDE6 + (letter - 'A')))
    }
}
