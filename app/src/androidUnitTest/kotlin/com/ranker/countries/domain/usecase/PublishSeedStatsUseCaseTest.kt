package com.ranker.countries.domain.usecase

import com.ranker.countries.data.hardcoded.SeedStats
import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.DebugLock
import com.ranker.countries.domain.repository.StatPublisher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PublishSeedStatsUseCaseTest {

    private val lock = FakeLock(password = "secret")
    private val publisher = FakePublisher()
    private val publishSeedStats = PublishSeedStatsUseCase(lock, publisher, SeedStats.all)

    @Test
    fun refusesToPublishWhileLocked() = runTest {
        try {
            publishSeedStats()
            fail("published while locked")
        } catch (expected: IllegalStateException) {
        }
        assertNull(publisher.published)
    }

    @Test
    fun aWrongPasswordKeepsItLocked() = runTest {
        assertFalse(publishSeedStats.unlock("guess"))
        assertFalse(publishSeedStats.isUnlocked)
    }

    @Test
    fun publishesTheSeedStatsOnceUnlocked() = runTest {
        assertTrue(publishSeedStats.unlock("secret"))

        assertEquals(SeedStats.all.size, publishSeedStats())
        assertEquals(SeedStats.all, publisher.published)
    }

    private class FakeLock(private val password: String) : DebugLock {
        override var isOpen = false
        override suspend fun opens(passphrase: String) = (passphrase == password).also { if (it) isOpen = true }
    }

    private class FakePublisher : StatPublisher {
        var published: List<Stat>? = null
        override suspend fun publish(stats: List<Stat>) {
            published = stats
        }
    }
}
