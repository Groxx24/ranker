package com.ranker.countries.data.worldbank

import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.StatSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlin.random.Random

/**
 * New stats from the World Bank's open API (no key needed): a random pick of [indicators], each
 * with every country's latest figure, fetched on every call. An indicator without enough data is
 * passed over for another one.
 */
internal class WorldBankStatSource(
    private val indicators: List<Indicator> = Indicators.all,
    private val random: Random = Random.Default,
    private val get: suspend (url: String) -> String = ::httpGet,
) : StatSource {

    override suspend fun freshStats(count: Int, avoid: Set<String>): List<Stat> = coroutineScope {
        val countries = async { WorldBankJson.countries(get(COUNTRIES_URL)) }
        val unused = indicators.filter { it.id !in avoid }
        var queue = (if (unused.size >= count) unused else indicators).shuffled(random)
        val picked = mutableListOf<Stat>()
        while (picked.size < count && queue.isNotEmpty()) {
            val batch = queue.take(count - picked.size)
            queue = queue.drop(batch.size)
            picked += batch
                .map { indicator -> async { WorldBankJson.stat(indicator, get(urlOf(indicator)), countries.await()) } }
                .awaitAll()
                .filterNotNull()
        }
        if (picked.size < count) throw IOException("Only ${picked.size} of $count indicators had enough data")
        picked
    }

    private companion object {
        const val API = "https://api.worldbank.org/v2"
        const val COUNTRIES_URL = "$API/country?format=json&per_page=1000"

        fun urlOf(indicator: Indicator) =
            "$API/country/all/indicator/${indicator.code}?format=json&mrnev=1&per_page=1000"
    }
}

private suspend fun httpGet(url: String): String = withContext(Dispatchers.IO) {
    val connection = URL(url).openConnection() as HttpURLConnection
    try {
        connection.connectTimeout = 15_000
        connection.readTimeout = 30_000
        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            throw IOException("HTTP ${connection.responseCode} from $url")
        }
        connection.inputStream.bufferedReader().use { it.readText() }
    } finally {
        connection.disconnect()
    }
}
