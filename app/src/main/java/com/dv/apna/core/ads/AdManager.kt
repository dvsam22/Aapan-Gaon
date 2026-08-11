package com.dv.apna.core.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.dv.apna.core.config.RemoteConfigManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import javax.inject.Inject
import javax.inject.Singleton

import com.dv.apna.core.config.ServiceAdCategory
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Singleton
class AdManager @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager
) {

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    companion object {
        private const val TAG = "AdManager"
    }

    /**
     * Preloads Interstitial Ad if enabled and not already loaded.
     */
    fun preloadInterstitialAd(context: Context) {
        if (!remoteConfigManager.isInterstitialAdsEnabled() || interstitialAd != null || isInterstitialLoading) {
            return
        }

        isInterstitialLoading = true
        val adUnitId = remoteConfigManager.getInterstitialAdUnitId(ServiceAdCategory.GENERAL)
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial Ad preloaded successfully")
                    FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad preloaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.e(TAG, "Interstitial Ad preload failed: ${error.message}")
                    FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad preload failed: ${error.message} [code=${error.code}]")
                    FirebaseCrashlytics.getInstance().recordException(
                        Exception("AdMob Interstitial Preload Failed [code=${error.code}]: ${error.message}")
                    )
                }
            }
        )
    }

    /**
     * Shows Interstitial Ad if preloaded ad is available.
     * If preloaded ad is null (e.g. slow network), immediately invokes onAdDismissed() so navigation is never blocked,
     * and triggers a background preload for future transitions.
     */
    fun showInterstitialAd(
        activity: Activity,
        category: ServiceAdCategory = ServiceAdCategory.GENERAL,
        onAdDismissed: () -> Unit
    ) {
        val isEnabled = remoteConfigManager.isInterstitialAdsEnabled()
        val shouldShow = remoteConfigManager.shouldShowAdWithProbability(isEnabled)

        if (!shouldShow) {
            onAdDismissed()
            return
        }

        FirebaseCrashlytics.getInstance().setCustomKey("last_ad_category", category.name)

        if (interstitialAd != null) {
            displayInterstitial(activity, interstitialAd!!, category, onAdDismissed)
            interstitialAd = null
        } else {
            // Instantly navigate to prevent UI freeze on slow network
            Log.d(TAG, "Interstitial Ad not ready yet ($category). Navigating immediately and preloading in background.")
            FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad not preloaded yet for category $category. Navigating instantly.")
            onAdDismissed()
            preloadInterstitialAd(activity.applicationContext)
        }
    }

    private fun displayInterstitial(
        activity: Activity,
        ad: InterstitialAd,
        category: ServiceAdCategory = ServiceAdCategory.GENERAL,
        onAdDismissed: () -> Unit
    ) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Interstitial Ad dismissed for category: $category")
                FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad dismissed for category: $category")
                preloadInterstitialAd(activity.applicationContext)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Interstitial Ad failed to show for category $category: ${adError.message}")
                FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad failed to show for category $category: ${adError.message}")
                FirebaseCrashlytics.getInstance().recordException(
                    Exception("AdMob Interstitial Show Failed ($category) [code=${adError.code}]: ${adError.message}")
                )
                preloadInterstitialAd(activity.applicationContext)
                onAdDismissed()
            }

            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Interstitial Ad showed successfully for category: $category")
                FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad showed for category: $category")
            }
        }
        ad.show(activity)
    }
}
