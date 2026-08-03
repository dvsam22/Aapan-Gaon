package com.dv.apna.core.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.dv.apna.core.config.RemoteConfigManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppOpenAdManager @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager
) : Application.ActivityLifecycleCallbacks, DefaultLifecycleObserver {

    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    private var isLoadingAd = false
    private var loadTime: Long = 0
    private var currentActivity: Activity? = null
    private var myApplication: Application? = null

    private var hasShownAdOnLaunch = false
    private var isAppInBackground = false

    companion object {
        private const val TAG = "AppOpenAdManager"
    }

    fun initialize(application: Application) {
        this.myApplication = application
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        fetchAd()
    }

    /**
     * Request an App Open ad.
     */
    fun fetchAd() {
        if (isAdAvailable() || isLoadingAd || !remoteConfigManager.isAppOpenAdsEnabled()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        val adUnitId = remoteConfigManager.getAppOpenAdUnitId()

        AppOpenAd.load(
            myApplication ?: return,
            adUnitId,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time
                    Log.d(TAG, "App Open Ad Loaded successfully")

                    // On cold start, show ad as soon as first load finishes
                    if (!hasShownAdOnLaunch) {
                        hasShownAdOnLaunch = true
                        currentActivity?.let { activity ->
                            if (!activity.isFinishing && !activity.isDestroyed) {
                                showAdIfAvailable(activity)
                            }
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.e(TAG, "App Open Ad Failed to Load: ${loadAdError.message}")
                }
            }
        )
    }

    /**
     * Check if ad exists and is not expired (older than 4 hours).
     */
    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    /**
     * Show ad if available.
     */
    fun showAdIfAvailable(activity: Activity) {
        val isEnabled = remoteConfigManager.isAppOpenAdsEnabled()

        if (!isEnabled) return

        if (!isShowingAd && isAdAvailable()) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    fetchAd()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    fetchAd()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                }
            }
            isShowingAd = true
            appOpenAd?.show(activity)
        } else {
            fetchAd()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Only show ad when returning from background (home button press)
        if (isAppInBackground) {
            isAppInBackground = false
            currentActivity?.let { activity ->
                if (!activity.isFinishing && !activity.isDestroyed) {
                    showAdIfAvailable(activity)
                }
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        // Mark app as in background when process stops
        isAppInBackground = true
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}
