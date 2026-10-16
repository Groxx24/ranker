package com.ranker.countries

import android.app.Application
import com.ranker.countries.di.AppContainer

class RankerApplication : Application() {
    val container by lazy { AppContainer() }
}
