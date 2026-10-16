package com.ranker.countries.domain.repository

import com.ranker.countries.domain.model.Stat

/** Deletes the pool of stats a [StatRepository] picks each day's from and writes [stats] in its place. */
interface StatPublisher {
    suspend fun publish(stats: List<Stat>)
}
