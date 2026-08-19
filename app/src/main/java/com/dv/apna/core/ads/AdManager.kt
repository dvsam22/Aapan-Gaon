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
    private var preloadedAdUnitId: String? = null
    private var isInterstitialLoading = false

    companion object {
        private const val TAG = "AdManager"
    }

    /**
     * Preloads Interstitial Ad for specific category (defaults to GENERAL).
     */
    fun preloadInterstitialAd(context: Context, category: ServiceAdCategory = ServiceAdCategory.GENERAL) {
        if (!remoteConfigManager.isInterstitialAdsEnabled() || isInterstitialLoading) {
            return
        }

        val adUnitId = remoteConfigManager.getInterstitialAdUnitId(category)
        if (interstitialAd != null && preloadedAdUnitId == adUnitId) {
            return
        }

        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    preloadedAdUnitId = adUnitId
                    isInterstitialLoading = false
                    Log.d(TAG, "Interstitial Ad preloaded successfully for category: $category ($adUnitId)")
                    FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad preloaded successfully for $category ($adUnitId)")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    preloadedAdUnitId = null
                    isInterstitialLoading = false
                    Log.e(TAG, "Interstitial Ad preload failed for $category: ${error.message}")
                    FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad preload failed for $category: ${error.message} [code=${error.code}]")
                    FirebaseCrashlytics.getInstance().recordException(
                        Exception("AdMob Interstitial Preload Failed ($category) [code=${error.code}]: ${error.message}")
                    )
                }
            }
        )
    }

    /**
     * Shows Interstitial Ad if preloaded ad is available for category.
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
        val targetAdUnitId = remoteConfigManager.getInterstitialAdUnitId(category)

        if (interstitialAd != null && preloadedAdUnitId == targetAdUnitId) {
            displayInterstitial(activity, interstitialAd!!, category, onAdDismissed)
            interstitialAd = null
            preloadedAdUnitId = null
        } else {
            // Instantly navigate to prevent UI freeze on slow network
            Log.d(TAG, "Interstitial Ad not ready yet for category $category ($targetAdUnitId). Navigating immediately and preloading in background.")
            FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad not preloaded yet for category $category ($targetAdUnitId). Navigating instantly.")
            onAdDismissed()
            preloadInterstitialAd(activity.applicationContext, category)
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
                preloadInterstitialAd(activity.applicationContext, category)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                Log.e(TAG, "Interstitial Ad failed to show for category $category: ${adError.message}")
                FirebaseCrashlytics.getInstance().log("AdMob: Interstitial Ad failed to show for category $category: ${adError.message}")
                FirebaseCrashlytics.getInstance().recordException(
                    Exception("AdMob Interstitial Show Failed ($category) [code=${adError.code}]: ${adError.message}")
                )
                preloadInterstitialAd(activity.applicationContext, category)
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
