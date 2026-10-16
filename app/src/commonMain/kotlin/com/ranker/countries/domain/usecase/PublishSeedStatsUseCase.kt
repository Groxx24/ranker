package com.ranker.countries.domain.usecase

import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.DebugLock
import com.ranker.countries.domain.repository.StatPublisher

/**
 * Replaces the published stats with [stats], once [lock] is open. The lock is only a convenience in
 * front of the real guard, the source's own rules, which refuse writes from anyone else.
 */
class PublishSeedStatsUseCase(
    private val lock: DebugLock,
    private val publisher: StatPublisher,
    private val stats: List<Stat>,
) {
    val isUnlocked: Boolean get() = lock.isOpen

    suspend fun unlock(passphrase: String): Boolean = lock.opens(passphrase)

    /** How many stats were published. */
    suspend operator fun invoke(): Int {
        check(lock.isOpen) { "Unlock before publishing" }
        publisher.publish(stats)
        return stats.size
    }
}
