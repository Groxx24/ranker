package com.ranker.countries.data.firestore

import com.ranker.countries.data.dailyRotation
import com.ranker.countries.data.hardcoded.HardcodedStats
import kotlinx.datetime.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StatDocumentTest {

    @Test
    fun everySampleStatSurvivesTheRoundTrip() {
        HardcodedStats.all.forEachIndexed { order, stat ->
            assertEquals(stat, StatDocument.decode(stat.id, StatDocument.encode(stat, order)))
        }
    }

    @Test
    fun readsWholeNumbersFirestoreHandsBackAsLong() {
        val data = StatDocument.encode(HardcodedStats.islands, 0).toMutableMap()
        data["values"] = (data["values"] as List<*>).map { (it as Map<*, *>) + ("value" to 7L) }

        val stat = StatDocument.decode("islands", data)!!

        assertTrue(stat.values.all { it.value == 7.0 })
    }

    @Test
    fun skipsAStatWithoutATitleAndAValueWithAnUnknownContinent() {
        val data = StatDocument.encode(HardcodedStats.coffee, 0)

        assertNull(StatDocument.decode("coffee", data - "title"))
        val values = (data["values"] as List<*>).toMutableList()
        values[0] = (values[0] as Map<*, *>) + ("continent" to "ATLANTIS")
        assertEquals(values.size - 1, StatDocument.decode("coffee", data + ("values" to values))!!.values.size)
    }

    @Test
    fun anEmptyPoolGivesAnEmptyDay() {
        assertTrue(dailyRotation(emptyList(), LocalDate(2026, 10, 16)).stats.isEmpty())
    }
}
