package com.ranker.countries

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.ranker.countries.data.firestore.FirestoreStatRepository
import com.ranker.countries.di.AppContainer

class RankerApplication : Application() {
    val container by lazy {
        val firestore = FirestoreStatRepository(Firebase.firestore)
        AppContainer(statRepository = firestore, statPublisher = firestore.takeIf { BuildConfig.DEBUG })
    }
}
