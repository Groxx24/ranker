package com.ranker.countries.domain.repository

import com.ranker.countries.domain.model.Stat

interface StatPublisher {
    /** The ids of the set published now; empty when nothing is. */
    suspend fun publishedIds(): Set<String>

    /** Deletes the published set of stats and writes [stats], in that order, in its place. */
    suspend fun publish(stats: List<Stat>)
}
