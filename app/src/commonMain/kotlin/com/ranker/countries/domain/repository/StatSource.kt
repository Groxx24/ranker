package com.ranker.countries.domain.repository

import com.ranker.countries.domain.model.Stat

/** Where a new set to publish comes from, freshly made on every call. */
interface StatSource {
    /** [count] different stats, none with an id in [avoid] while there are enough others to pick. */
    suspend fun freshStats(count: Int, avoid: Set<String>): List<Stat>
}
