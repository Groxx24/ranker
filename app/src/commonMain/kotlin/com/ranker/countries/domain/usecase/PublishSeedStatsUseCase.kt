package com.ranker.countries.domain.usecase

import com.ranker.countries.domain.repository.DebugLock
import com.ranker.countries.domain.repository.StatPublisher
import com.ranker.countries.domain.repository.StatSource
import com.ranker.countries.domain.repository.StatRepository.Companion.DAILY_STAT_COUNT

/**
 * Replaces the published stats with a new set from [source], none of them in the set it replaces,
 * once [lock] is open. The lock is only a convenience in front of the real guard, the publisher's own
 * rules, which refuse writes from anyone else.
 */
class PublishSeedStatsUseCase(
    private val lock: DebugLock,
    private val publisher: StatPublisher,
    private val source: StatSource,
) {
    val isUnlocked: Boolean get() = lock.isOpen

    suspend fun unlock(passphrase: String): Boolean = lock.opens(passphrase)

    /** How many stats were published. */
    suspend operator fun invoke(): Int {
        check(lock.isOpen) { "Unlock before publishing" }
        val stats = source.freshStats(DAILY_STAT_COUNT, avoid = publisher.publishedIds())
        check(stats.size == DAILY_STAT_COUNT) { "A published set has $DAILY_STAT_COUNT stats, not ${stats.size}" }
        publisher.publish(stats)
        return stats.size
    }
}
