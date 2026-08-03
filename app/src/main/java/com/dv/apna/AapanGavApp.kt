package com.dv.apna

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.dv.apna.core.ads.AdManager
import com.dv.apna.core.ads.AppOpenAdManager
import com.dv.apna.core.config.RemoteConfigManager
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
                Log.d("FirebaseInit", "Firebase initialized successfully with google-services.json")
            }
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Firebase init failed, using fallback: ${e.message}")
            val options = FirebaseOptions.Builder()
                .setApplicationId("com.dv.apna")
                .setApiKey("unused")
                .setProjectId("unused")
                .build()
            FirebaseApp.initializeApp(this, options)
        }

        // Initialize Google Mobile Ads SDK
        MobileAds.initialize(this) { status ->
            Log.d("MobileAdsInit", "MobileAds initialization complete: ${status.adapterStatusMap}")
        }

        // Initialize App Open Ad Manager & Preload Interstitial & Rewarded Ads
        appOpenAdManager.initialize(this)
        adManager.preloadInterstitialAd(this)
        adManager.preloadRewardedAd(this)
    }
}
