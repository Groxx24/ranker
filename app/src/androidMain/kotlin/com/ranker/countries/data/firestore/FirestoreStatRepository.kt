package com.ranker.countries.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ranker.countries.domain.model.DailyStats
import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.StatPublisher
import com.ranker.countries.domain.repository.StatRepository
import com.ranker.countries.domain.repository.StatRepository.Companion.DAILY_STAT_COUNT
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDate

/**
 * The set of stats in the `stats` collection, one document per stat (see [StatDocument]), shown
 * every day in the order it was published until the next [publish] replaces it.
 */
class FirestoreStatRepository(
    private val firestore: FirebaseFirestore,
) : StatRepository, StatPublisher {

    private val collection get() = firestore.collection(COLLECTION)

    override suspend fun statsFor(date: LocalDate): DailyStats {
        val stats = collection.orderBy(StatDocument.ORDER, Query.Direction.ASCENDING).get().await()
            .documents
            .mapNotNull { doc -> doc.data?.let { StatDocument.decode(doc.id, it) } }
        return DailyStats(date, stats.take(DAILY_STAT_COUNT))
    }

    /**
     * Deletes every document in the collection and writes [stats], all in one batch, so readers see
     * either the old set or the new one, never an empty or half-written collection.
     */
    override suspend fun publish(stats: List<Stat>) {
        val existing = collection.get().await().documents
        firestore.batch().apply {
            existing.forEach { delete(it.reference) }
            stats.forEachIndexed { order, stat -> set(collection.document(stat.id), StatDocument.encode(stat, order)) }
        }.commit().await()
    }

    private companion object {
        const val COLLECTION = "stats"
    }
}
