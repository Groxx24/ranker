package com.ranker.countries.domain.repository

/** Guards publishing from the debug build: only the publisher may replace the stats. */
interface DebugLock {
    /** True once [opens] has succeeded, on this run or an earlier one. */
    val isOpen: Boolean

    /** True when [passphrase] opens the lock, which then stays open. */
    suspend fun opens(passphrase: String): Boolean
}
