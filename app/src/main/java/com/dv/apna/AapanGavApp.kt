package com.dv.apna

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.dv.apna.core.ads.AdManager
import com.dv.apna.core.ads.AppOpenAdManager
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.notification.local.LocalNotificationManager
import com.dv.apna.core.notification.local.LocalNotificationScheduler
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AapanGavApp : Application() {

    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager

    @Inject
    lateinit var adManager: AdManager

    override fun onCreate() {
        super.onCreate()
        // Force Light Mode globally to prevent dark window background flashing on recreation
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }
        } catch (e: Exception) {
            val options = FirebaseOptions.Builder()
                .setApplicationId("com.dv.apna")
                .setApiKey("unused")
                .setProjectId("unused")
                .build()
            FirebaseApp.initializeApp(this, options)
        }

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) {}

        // Initialize App Open Ad Manager & Preload Interstitial Ads
        appOpenAdManager.initialize(this)
        adManager.preloadInterstitialAd(this)

        // Initialize Daily 2x Local Notifications (Morning 8:30 AM & Evening 7:00 PM)
        LocalNotificationScheduler.scheduleNextDailyWorker(this)

        // Track App Lifecycle to suppress local notifications while user is actively inside the app
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                LocalNotificationManager.isAppInForeground.set(true)
            }

            override fun onStop(owner: LifecycleOwner) {
                LocalNotificationManager.isAppInForeground.set(false)
            }
        })
    }
}
