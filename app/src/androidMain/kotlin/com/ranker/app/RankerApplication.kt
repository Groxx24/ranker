package com.ranker.app

import android.app.Application
import com.ranker.app.di.AppContainer

class RankerApplication : Application() {
    val container by lazy { AppContainer() }
}
