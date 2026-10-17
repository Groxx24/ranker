package com.ranker.countries.ui.ads

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.ranker.countries.BuildConfig

private const val BANNER_AD_UNIT_ID = "ca-app-pub-4164526058911953/9565542904"

// Google's sample adaptive banner unit. Debug builds load it so clicks while testing don't count as
// invalid traffic against the real account.
private const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"

/**
 * An anchored adaptive banner as wide as the screen. It sits above the navigation bar and owns that
 * inset, so whatever is laid out above it doesn't need to pad for the bar itself.
 */
@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    BoxWithConstraints(
        modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .navigationBarsPadding(),
    ) {
        val context = LocalContext.current
        val widthDp = maxWidth.value.toInt()
        val adView = remember(widthDp) {
            AdView(context).apply {
                adUnitId = if (BuildConfig.DEBUG) TEST_BANNER_AD_UNIT_ID else BANNER_AD_UNIT_ID
                setAdSize(AdSize.getLargeAnchoredAdaptiveBannerAdSize(context, widthDp))
                loadAd(AdRequest.Builder().build())
            }
        }
        LifecycleResumeEffect(adView) {
            adView.resume()
            onPauseOrDispose { adView.pause() }
        }
        DisposableEffect(adView) {
            onDispose { adView.destroy() }
        }
        AndroidView(factory = { adView }, modifier = Modifier.fillMaxWidth())
    }
}
