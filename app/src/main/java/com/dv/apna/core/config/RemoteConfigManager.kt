package com.dv.apna.core.config

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import com.dv.apna.BuildConfig

@Singleton
class RemoteConfigManager @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig by lazy { Firebase.remoteConfig }

    companion object {
        // Remote Config Keys (Only for enable/disable & probability)
        const val KEY_ADS_ENABLED = "ads_enabled"
        const val KEY_APP_OPEN_ADS_ENABLED = "app_open_ads_enabled"
        const val KEY_BANNER_ADS_ENABLED = "banner_ads_enabled"
        const val KEY_INTERSTITIAL_ADS_ENABLED = "interstitial_ads_enabled"
        const val KEY_AD_SHOW_PROBABILITY = "ad_show_probability"

        // In-App AdMob Test Unit IDs (Debug)
        const val TEST_APP_OPEN_AD_UNIT = "ca-app-pub-3940256099942544/9257395921"
        const val TEST_BANNER_AD_UNIT = "ca-app-pub-3940256099942544/6300978111"
        const val TEST_INTERSTITIAL_AD_UNIT = "ca-app-pub-3940256099942544/1033173712"

        // In-App AdMob Production Unit IDs (Live)
        const val PROD_APP_OPEN_AD_UNIT = "ca-app-pub-2632394200654942/5199609871"
        const val PROD_BANNER_AD_UNIT = "ca-app-pub-2632394200654942/7474419425"
        const val PROD_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/3036375764"
    }

    init {
        setupRemoteConfig()
    }

    private fun setupRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0L else 3600L
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val defaults = mapOf(
            KEY_ADS_ENABLED to true,
            KEY_APP_OPEN_ADS_ENABLED to true,
            KEY_BANNER_ADS_ENABLED to true,
            KEY_INTERSTITIAL_ADS_ENABLED to true,
            KEY_AD_SHOW_PROBABILITY to 1.0
        )
        remoteConfig.setDefaultsAsync(defaults)
        fetchAndActivate()
    }

    fun fetchAndActivate(onComplete: ((Boolean) -> Unit)? = null) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                val updated = task.isSuccessful
                onComplete?.invoke(updated)
            }
    }

    fun isAdsEnabled(): Boolean = remoteConfig.getBoolean(KEY_ADS_ENABLED)
    fun isAppOpenAdsEnabled(): Boolean = isAdsEnabled() && remoteConfig.getBoolean(KEY_APP_OPEN_ADS_ENABLED)
    fun isBannerAdsEnabled(): Boolean = isAdsEnabled() && remoteConfig.getBoolean(KEY_BANNER_ADS_ENABLED)
    fun isInterstitialAdsEnabled(): Boolean = isAdsEnabled() && remoteConfig.getBoolean(KEY_INTERSTITIAL_ADS_ENABLED)

    fun getAppOpenAdUnitId(): String {
        return if (BuildConfig.DEBUG) TEST_APP_OPEN_AD_UNIT else PROD_APP_OPEN_AD_UNIT
    }

    fun getBannerAdUnitId(): String {
        return if (BuildConfig.DEBUG) TEST_BANNER_AD_UNIT else PROD_BANNER_AD_UNIT
    }

    fun getInterstitialAdUnitId(): String {
        return if (BuildConfig.DEBUG) TEST_INTERSTITIAL_AD_UNIT else PROD_INTERSTITIAL_AD_UNIT
    }

    private val interstitialClickCounter = java.util.concurrent.atomic.AtomicInteger(0)

    fun getAdShowProbability(): Double {
        val strVal = remoteConfig.getString(KEY_AD_SHOW_PROBABILITY)
        return strVal.toDoubleOrNull() ?: try {
            remoteConfig.getDouble(KEY_AD_SHOW_PROBABILITY)
        } catch (e: Exception) {
            1.0
        }
    }

    /**
     * Checks whether an interstitial ad should be displayed based on deterministic probability bucket & feature flag.
     * Guaranteed exact ratio for 0.1 to 1.0 values:
     * - 1.0 -> 100% (Every click)
     * - 0.5 -> 50% (Exactly 1 in 2 clicks)
     * - 0.3 -> 30% (Exactly 3 in 10 clicks)
     * - 0.2 -> 20% (Exactly 1 in 5 clicks)
     * - 0.1 -> 10% (Exactly 1 in 10 clicks)
     * - 0.0 -> 0% (Never)
     */
    fun shouldShowAdWithProbability(isEnabled: Boolean): Boolean {
        if (!isEnabled) return false
        val prob = getAdShowProbability()
        if (prob >= 1.0) return true
        if (prob <= 0.0) return false

        val count = kotlin.math.round(prob * 10).toInt()
        if (count <= 0) return false
        if (count >= 10) return true

        val currentClick = interstitialClickCounter.getAndIncrement()
        val step = Math.floorMod(currentClick, 10)
        
        val prevBucket = (step * count) / 10
        val nextBucket = ((step + 1) * count) / 10
        return prevBucket != nextBucket
    }
}
