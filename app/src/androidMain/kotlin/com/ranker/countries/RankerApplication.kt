package com.ranker.countries

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.crashlytics.crashlytics
import com.google.firebase.firestore.firestore
import com.ranker.countries.data.firebase.FirebaseDebugLock
import com.ranker.countries.data.firestore.FirestoreStatRepository
import com.ranker.countries.data.worldbank.WorldBankStatSource
import com.ranker.countries.di.AppContainer

class RankerApplication : Application() {
    val container by lazy {
        val firestore = FirestoreStatRepository(Firebase.firestore)
        AppContainer(
            statRepository = firestore,
            debug = if (BuildConfig.DEBUG) {
                AppContainer.DebugPublishing(
                    publisher = firestore,
                    source = WorldBankStatSource(),
                    lock = FirebaseDebugLock(Firebase.auth),
                )
            } else {
                null
            },
        )
    }

    override fun onCreate() {
        super.onCreate()
        // Crashes while developing would only bury the real ones.
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
    }
}
