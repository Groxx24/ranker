package com.ranker.countries.data.firebase

import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.ranker.countries.domain.repository.DebugLock
import kotlinx.coroutines.tasks.await

/**
 * Opens when the passphrase is the password of the publisher's Firebase account, [email], and leaves
 * that account signed in. Firestore's rules (`firestore.rules`) allow writes to the stats only from
 * it, so the lock also holds against anyone using the Firebase config without the app.
 */
class FirebaseDebugLock(
    private val auth: FirebaseAuth,
    private val email: String = PUBLISHER_EMAIL,
) : DebugLock {

    override val isOpen: Boolean get() = auth.currentUser?.email.equals(email, ignoreCase = true)

    override suspend fun opens(passphrase: String): Boolean = try {
        auth.signInWithEmailAndPassword(email, passphrase).await()
        true
    } catch (e: FirebaseException) {
        // A wrong password, too many tries, or no network: none of them opens.
        false
    }

    companion object {
        /** The account made in the Firebase console; its UID is the one `firestore.rules` lets write. */
        const val PUBLISHER_EMAIL = "insomniatft@gmail.com"
    }
}
