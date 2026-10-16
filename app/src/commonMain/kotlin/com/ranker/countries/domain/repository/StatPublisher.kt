package com.ranker.countries.domain.repository

import com.ranker.countries.domain.model.Stat

/** Writes the pool of stats a [StatRepository] picks each day's from, replacing what's there. */
interface StatPublisher {
    suspend fun publish(stats: List<Stat>)
}
