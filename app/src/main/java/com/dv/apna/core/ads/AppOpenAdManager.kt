package com.dv.apna.core.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.dv.apna.core.config.RemoteConfigManager
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Singleton
class AppOpenAdManager @Inject constructor(
    private val remoteConfigManager: RemoteConfigManager
) : Application.ActivityLifecycleCallbacks {

    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    private var isLoadingAd = false
    private var loadTime: Long = 0
    private var currentActivity: Activity? = null
    private var myApplication: Application? = null

    private var hasShownAdOnLaunch = false

    companion object {
        private const val TAG = "AppOpenAdManager"
    }

    fun initialize(application: Application) {
        this.myApplication = application
        application.registerActivityLifecycleCallbacks(this)
        fetchAd()
    }

    /**
     * Request an App Open ad for initial launch.
     */
    fun fetchAd() {
        if (hasShownAdOnLaunch || isAdAvailable() || isLoadingAd || !remoteConfigManager.isAppOpenAdsEnabled()) {
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
                    FirebaseCrashlytics.getInstance().log("AdMob: App Open Ad loaded successfully")

                    // Show ad ONLY ONCE on cold start
                    if (!hasShownAdOnLaunch) {
                        val act = currentActivity
                        if (act != null && !act.isFinishing && !act.isDestroyed && !isAdActivity(act)) {
                            hasShownAdOnLaunch = true
                            showAdIfAvailable(act)
                        }
                    }
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    isLoadingAd = false
                    Log.e(TAG, "App Open Ad Failed to Load: ${loadAdError.message}")
                    FirebaseCrashlytics.getInstance().log("AdMob: App Open Ad Failed to Load: ${loadAdError.message} [code=${loadAdError.code}]")
                }
            }
        )
    }

    private fun isAdActivity(activity: Activity): Boolean {
        val name = activity.javaClass.name
        return name.contains("AdActivity") || name.contains("GoogleMobileAds")
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

        if (!isEnabled || isAdActivity(activity)) return

        if (!isShowingAd && isAdAvailable()) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "App Open Ad dismissed")
                    FirebaseCrashlytics.getInstance().log("AdMob: App Open Ad dismissed")
                    appOpenAd = null
                    isShowingAd = false
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "App Open Ad failed to show: ${adError.message}")
                    FirebaseCrashlytics.getInstance().log("AdMob: App Open Ad failed to show: ${adError.message}")
                    appOpenAd = null
                    isShowingAd = false
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                    Log.d(TAG, "App Open Ad showed successfully")
                    FirebaseCrashlytics.getInstance().log("AdMob: App Open Ad showed successfully")
                }
            }
            isShowingAd = true
            appOpenAd?.show(activity)
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (!isAdActivity(activity)) {
            currentActivity = activity
            if (!hasShownAdOnLaunch && isAdAvailable() && !isShowingAd) {
                hasShownAdOnLaunch = true
                showAdIfAvailable(activity)
            }
        }
    }
    override fun onActivityResumed(activity: Activity) {
        if (!isAdActivity(activity)) {
            currentActivity = activity
        }
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
