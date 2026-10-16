package com.ranker.app.ui.format

import org.junit.Assert.assertEquals
import org.junit.Test

class FormatTest {

    @Test
    fun groupsThousandsAndRounds() {
        assertEquals("267,570", formatNumber(267_570.0, 0))
        assertEquals("12.0", formatNumber(12.0, 1))
        assertEquals("3.16", formatNumber(3.159, 2))
        assertEquals("1,000.0", formatNumber(999.96, 1))
        assertEquals("0", formatNumber(0.0, 0))
    }

    @Test
    fun addsTheUnitOnlyWhenThereIsOne() {
        assertEquals("9.9 kg", formatValue(9.9, 1, "kg"))
        assertEquals("17,508", formatValue(17_508.0, 0, ""))
    }

    @Test
    fun givesSmallPercentagesADecimal() {
        assertEquals("0.3", formatPercent(0.27))
        assertEquals("21", formatPercent(20.6))
    }

    @Test
    fun turnsACodeIntoItsFlag() {
        assertEquals("🇧🇪", flagEmoji("BE"))
        assertEquals("🇫🇮", flagEmoji("fi"))
    }
}
