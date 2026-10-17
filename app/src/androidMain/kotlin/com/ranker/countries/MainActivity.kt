package com.ranker.countries

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ranker.countries.ui.App
import com.ranker.countries.ui.ads.AdBanner
import com.ranker.countries.ui.ads.AdsConsent

class MainActivity : ComponentActivity() {
    private val container get() = (application as RankerApplication).container

    // Lazy because the activity has no context yet while its fields are set.
    private val adsConsent by lazy { AdsConsent(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        adsConsent.gather()
        setContent {
            App(
                container,
                // No slot at all until ads may load, so the screen keeps padding for the navigation bar.
                bottomBar = if (adsConsent.canRequestAds) {
                    { AdBanner() }
                } else {
                    null
                },
                onOpenPrivacyOptions = if (adsConsent.privacyOptionsRequired) adsConsent::showPrivacyOptions else null,
            )
        }
    }
}
