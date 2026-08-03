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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdManager @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager
) {

    private var interstitialAd: InterstitialAd? = null
    private var isInterstitialLoading = false

    private var rewardedAd: RewardedAd? = null
    private var isRewardedLoading = false

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
        val adUnitId = remoteConfigManager.getInterstitialAdUnitId()
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
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                    isInterstitialLoading = false
                    Log.e(TAG, "Interstitial Ad preload failed: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows Interstitial Ad if available. If preloaded ad is null, loads on-demand and shows it immediately.
     */
    fun showInterstitialAd(activity: Activity, onAdDismissed: () -> Unit) {
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
            // Load on demand
            val adUnitId = remoteConfigManager.getInterstitialAdUnitId()
            val adRequest = AdRequest.Builder().build()

            InterstitialAd.load(
                activity,
                adUnitId,
                adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        Log.d(TAG, "On-demand Interstitial Ad loaded")
                        displayInterstitial(activity, ad, onAdDismissed)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e(TAG, "On-demand Interstitial Ad failed: ${error.message}")
                        onAdDismissed()
                    }
                }
            )
        }
    }

    private fun displayInterstitial(activity: Activity, ad: InterstitialAd, onAdDismissed: () -> Unit) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                preloadInterstitialAd(activity.applicationContext)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                preloadInterstitialAd(activity.applicationContext)
                onAdDismissed()
            }
        }
        ad.show(activity)
    }

    /**
     * Preloads Rewarded Ad if enabled and not already loaded.
     */
    fun preloadRewardedAd(context: Context) {
        if (!remoteConfigManager.isRewardedAdsEnabled() || rewardedAd != null || isRewardedLoading) {
            return
        }

        isRewardedLoading = true
        val adUnitId = remoteConfigManager.getRewardedAdUnitId()
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(
            context,
            adUnitId,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    isRewardedLoading = false
                    Log.d(TAG, "Rewarded Ad preloaded successfully")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    rewardedAd = null
                    isRewardedLoading = false
                    Log.e(TAG, "Rewarded Ad preload failed: ${error.message}")
                }
            }
        )
    }

    /**
     * Shows Rewarded Ad if available. If preloaded ad is null, loads on-demand and shows it immediately.
     */
    fun showRewardedAd(activity: Activity, onUserEarnedReward: () -> Unit, onAdDismissed: () -> Unit) {
        val isEnabled = remoteConfigManager.isRewardedAdsEnabled()
        val shouldShow = remoteConfigManager.shouldShowAdWithProbability(isEnabled)

        if (!shouldShow) {
            onAdDismissed()
            return
        }

        if (rewardedAd != null) {
            displayRewarded(activity, rewardedAd!!, onUserEarnedReward, onAdDismissed)
            rewardedAd = null
        } else {
            // Load on demand
            val adUnitId = remoteConfigManager.getRewardedAdUnitId()
            val adRequest = AdRequest.Builder().build()

            RewardedAd.load(
                activity,
                adUnitId,
                adRequest,
                object : RewardedAdLoadCallback() {
                    override fun onAdLoaded(ad: RewardedAd) {
                        Log.d(TAG, "On-demand Rewarded Ad loaded")
                        displayRewarded(activity, ad, onUserEarnedReward, onAdDismissed)
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e(TAG, "On-demand Rewarded Ad failed: ${error.message}")
                        onAdDismissed()
                    }
                }
            )
        }
    }

    private fun displayRewarded(
        activity: Activity,
        ad: RewardedAd,
        onUserEarnedReward: () -> Unit,
        onAdDismissed: () -> Unit
    ) {
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                preloadRewardedAd(activity.applicationContext)
                onAdDismissed()
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                preloadRewardedAd(activity.applicationContext)
                onAdDismissed()
            }
        }
        ad.show(activity) { _ ->
            onUserEarnedReward()
        }
    }
}
