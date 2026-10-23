package com.dv.apna.core.ads

import android.app.Activity
import android.content.Context
import com.dv.apna.core.config.RemoteConfigManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager
) {

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    /**
     * Preloads the global Interstitial Ad in background.
     */
    fun preloadInterstitialAd(context: Context) {
        if (!remoteConfigManager.isInterstitialAdsEnabled() || isInterstitialLoading || interstitialAd != null) {
            return
        }

        val adUnitId = remoteConfigManager.getInterstitialAdUnitId()
        isInterstitialLoading = true
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            adUnitId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    isInterstitialLoading = false
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                }
            }
        )
    }

    /**
     * Shows Interstitial Ad if preloaded ad is available.
     * Probability is evaluated strictly on interstitial ad display.
     * If preloaded ad is null, immediately invokes onAdDismissed() so navigation is never blocked.
     */
    fun showInterstitialAd(
        activity: Activity,
        onAdDismissed: () -> Unit
    ) {
        val isEnabled = remoteConfigManager.isInterstitialAdsEnabled()
        val shouldShow = remoteConfigManager.shouldShowAdWithProbability(isEnabled)

        if (!shouldShow) {
            onAdDismissed()
            return
        }

        if (interstitialAd != null) {
            displayInterstitial(activity, interstitialAd!!, onAdDismissed)
            interstitialAd = null
        } else {
            // Instantly navigate to prevent UI freeze on slow network
            onAdDismissed()
            preloadInterstitialAd(activity.applicationContext)
        }
    }

    private fun displayInterstitial(
        activity: Activity,
        ad: InterstitialAd,
        onAdDismissed: () -> Unit
    ) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                preloadInterstitialAd(activity.applicationContext)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                preloadInterstitialAd(activity.applicationContext)
                onAdDismissed()
            }

            override fun onAdShowedFullScreenContent() {
            }
        }
        ad.show(activity)
    }
}
