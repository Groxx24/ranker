package com.ranker.countries.domain.usecase

import com.ranker.countries.data.hardcoded.HardcodedStats
import com.ranker.countries.domain.model.Stat
import com.ranker.countries.domain.repository.DebugLock
import com.ranker.countries.domain.repository.StatPublisher
import com.ranker.countries.domain.repository.StatSource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class PublishSeedStatsUseCaseTest {

    private val lock = FakeLock(password = "secret")
    private val publisher = FakePublisher(publishedIds = setOf("islands"))
    private val source = FakeSource(HardcodedStats.all.take(5))
    private val publishSeedStats = PublishSeedStatsUseCase(lock, publisher, source)

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
    fun publishesAFreshSetOnceUnlocked() = runTest {
        assertTrue(publishSeedStats.unlock("secret"))

        assertEquals(5, publishSeedStats())
        assertEquals(source.stats, publisher.published)
    }

    @Test
    fun asksForStatsOtherThanThePublishedOnes() = runTest {
        publishSeedStats.unlock("secret")
        publishSeedStats()

        assertEquals(5, source.askedFor)
        assertEquals(setOf("islands"), source.avoided)
    }

    @Test
    fun publishesNothingWhenTheSourceComesUpShort() = runTest {
        val short = PublishSeedStatsUseCase(lock, publisher, FakeSource(HardcodedStats.all.take(3)))
        short.unlock("secret")
        try {
            short()
            fail("published a short set")
        } catch (expected: IllegalStateException) {
        }
        assertNull(publisher.published)
    }

    private class FakeLock(private val password: String) : DebugLock {
        override var isOpen = false
        override suspend fun opens(passphrase: String) = (passphrase == password).also { if (it) isOpen = true }
    }

    private class FakePublisher(private val publishedIds: Set<String>) : StatPublisher {
        var published: List<Stat>? = null
        override suspend fun publishedIds() = publishedIds
        override suspend fun publish(stats: List<Stat>) {
            published = stats
        }
    }

    private class FakeSource(val stats: List<Stat>) : StatSource {
        var askedFor: Int? = null
        var avoided: Set<String>? = null
        override suspend fun freshStats(count: Int, avoid: Set<String>): List<Stat> {
            askedFor = count
            avoided = avoid
            return stats
        }
    }
}
