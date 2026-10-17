package com.ranker.countries

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.ranker.countries.data.firebase.FirebaseDebugLock
import com.ranker.countries.data.firestore.FirestoreStatRepository
import com.ranker.countries.di.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RankerApplication : Application() {
    val container by lazy {
        val firestore = FirestoreStatRepository(Firebase.firestore)
        AppContainer(
            statRepository = firestore,
            debug = if (BuildConfig.DEBUG) {
                AppContainer.DebugPublishing(publisher = firestore, lock = FirebaseDebugLock(Firebase.auth))
            } else {
                null
            },
        )
    }

    override fun onCreate() {
        super.onCreate()
        // Off the main thread, as Google recommends: initialization does disk and network work.
        CoroutineScope(Dispatchers.IO).launch { MobileAds.initialize(this@RankerApplication) }
    }
}
