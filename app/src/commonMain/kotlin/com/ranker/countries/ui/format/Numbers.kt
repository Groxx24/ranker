package com.ranker.countries.ui.format

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToLong

/** [value] with [decimals] decimals and thousands grouped by commas: `267,570`, `12.0`. */
fun formatNumber(value: Double, decimals: Int): String {
    val factor = 10.0.pow(decimals).toLong()
    val scaled = (abs(value) * factor).roundToLong()
    val whole = (scaled / factor).toString().reversed().chunked(3).joinToString(",").reversed()
    val sign = if (value < 0 && scaled != 0L) "-" else ""
    if (decimals == 0) return sign + whole
    val fraction = (scaled % factor).toString().padStart(decimals, '0')
    return "$sign$whole.$fraction"
}

/** A value followed by its unit, if it has one: `12.0 kg`, `267,570`. */
fun formatValue(value: Double, decimals: Int, unit: String): String =
    formatNumber(value, decimals).let { if (unit.isEmpty()) it else "$it $unit" }

/** A percentage without its sign, with a decimal only when it is small enough to need one: `21`, `0.3`. */
fun formatPercent(value: Double): String = formatNumber(value, if (abs(value) < 10) 1 else 0)
