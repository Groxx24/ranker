package com.ranker.countries.domain.repository

import com.ranker.countries.domain.model.Stat

/** Deletes the published set of stats and writes [stats], in that order, in its place. */
interface StatPublisher {
    suspend fun publish(stats: List<Stat>)
}
