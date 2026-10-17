package com.ranker.countries.ui.ads

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation.ConsentStatus
import com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics.ConsentStatus.DENIED
import com.google.firebase.analytics.FirebaseAnalytics.ConsentStatus.GRANTED
import com.google.firebase.analytics.FirebaseAnalytics.ConsentType
import com.google.firebase.analytics.analytics
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Asks for consent with Google's form (in the EEA, UK and Switzerland, where AdMob requires one),
 * then starts the Mobile Ads SDK and tells Firebase Analytics what the user allowed. No ad is
 * requested until [canRequestAds] is true, and Analytics stays in its denied defaults (see the
 * manifest) until the user has answered.
 */
class AdsConsent(private val activity: Activity) {
    private val consentInformation = UserMessagingPlatform.getConsentInformation(activity)

    var canRequestAds by mutableStateOf(false)
        private set

    /** Whether the user must be offered a way to change their choice, through [showPrivacyOptions]. */
    var privacyOptionsRequired by mutableStateOf(false)
        private set

    /** Checks the consent status, as Google asks on every start, and shows the form if needed. */
    fun gather() {
        consentInformation.requestConsentInfoUpdate(
            activity,
            ConsentRequestParameters.Builder().build(),
            { UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { update() } },
            { update() },
        )
        // A choice made on an earlier start lets ads load without waiting for the check.
        update()
    }

    fun showPrivacyOptions() {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { update() }
    }

    private fun update() {
        privacyOptionsRequired =
            consentInformation.privacyOptionsRequirementStatus == PrivacyOptionsRequirementStatus.REQUIRED
        val status = consentInformation.consentStatus
        if (status == ConsentStatus.OBTAINED || status == ConsentStatus.NOT_REQUIRED) {
            updateAnalyticsConsent(activity)
        }
        if (consentInformation.canRequestAds()) {
            startMobileAds(activity.applicationContext)
            canRequestAds = true
        }
    }

    private companion object {
        val mobileAdsStarted = AtomicBoolean(false)

        /** Once per process, off the main thread as Google recommends. */
        fun startMobileAds(context: Context) {
            if (mobileAdsStarted.getAndSet(true)) return
            Thread { MobileAds.initialize(context) }.start()
        }

        /**
         * Consent Mode from the answer the form saved in the standard IAB TCF keys, mapped the way
         * Google documents it: storage needs purpose 1, ad user data purposes 1 and 7, ad
         * personalisation purposes 3 and 4. Where GDPR doesn't apply, everything is granted.
         */
        fun updateAnalyticsConsent(context: Context) {
            val prefs = context.getSharedPreferences("${context.packageName}_preferences", Context.MODE_PRIVATE)
            val gdprApplies = prefs.getInt("IABTCF_gdprApplies", 0) == 1
            val purposes = prefs.getString("IABTCF_PurposeConsents", "").orEmpty()
            fun allowed(purpose: Int) = !gdprApplies || purposes.getOrNull(purpose - 1) == '1'
            fun status(granted: Boolean) = if (granted) GRANTED else DENIED

            Firebase.analytics.setConsent(
                mapOf(
                    ConsentType.ANALYTICS_STORAGE to status(allowed(1)),
                    ConsentType.AD_STORAGE to status(allowed(1)),
                    ConsentType.AD_USER_DATA to status(allowed(1) && allowed(7)),
                    ConsentType.AD_PERSONALIZATION to status(allowed(3) && allowed(4)),
                ),
            )
        }
    }
}
