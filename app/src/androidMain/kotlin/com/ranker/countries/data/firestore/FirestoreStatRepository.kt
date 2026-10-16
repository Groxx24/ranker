package com.ranker.countries.data.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.ranker.countries.data.dailyRotation
import com.ranker.countries.domain.model.DailyStats
import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.StatPublisher
import com.ranker.countries.domain.repository.StatRepository
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.LocalDate

/**
 * The pool of stats in the `stats` collection, one document per stat (see [StatDocument]), picked
 * from each day with [dailyRotation] in the order they were published.
 */
class FirestoreStatRepository(
    private val firestore: FirebaseFirestore,
) : StatRepository, StatPublisher {

    private val collection get() = firestore.collection(COLLECTION)

    override suspend fun statsFor(date: LocalDate): DailyStats {
        val pool = collection.orderBy(StatDocument.ORDER, Query.Direction.ASCENDING).get().await()
            .documents
            .mapNotNull { doc -> doc.data?.let { StatDocument.decode(doc.id, it) } }
        return dailyRotation(pool, date)
    }

    /**
     * Deletes every document in the collection and writes [stats], all in one batch, so readers see
     * either the old pool or the new one, never an empty or half-written collection.
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
