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

    /** Replaces the whole collection with [stats] in one batch, so readers never see half of it. */
    override suspend fun publish(stats: List<Stat>) {
        val ids = stats.map { it.id }.toSet()
        val stale = collection.get().await().documents.filterNot { it.id in ids }
        firestore.batch().apply {
            stale.forEach { delete(it.reference) }
            stats.forEachIndexed { order, stat -> set(collection.document(stat.id), StatDocument.encode(stat, order)) }
        }.commit().await()
    }

    private companion object {
        const val COLLECTION = "stats"
    }
}
