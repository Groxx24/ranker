package com.ranker.countries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ranker.countries.ui.App

class MainActivity : ComponentActivity() {
    private val container get() = (application as RankerApplication).container

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { App(container) }
    }
}
