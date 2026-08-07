package com.dv.apna.core.config

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class RemoteConfigManager @Inject constructor() {

    private val remoteConfig: FirebaseRemoteConfig by lazy { Firebase.remoteConfig }

    companion object {
        private const val TAG = "RemoteConfigManager"

        // Ad Config Keys
        const val KEY_ADS_ENABLED = "ads_enabled"
        const val KEY_APP_OPEN_ADS_ENABLED = "app_open_ads_enabled"
        const val KEY_BANNER_ADS_ENABLED = "banner_ads_enabled"
        const val KEY_INTERSTITIAL_ADS_ENABLED = "interstitial_ads_enabled"
        const val KEY_REWARDED_ADS_ENABLED = "rewarded_ads_enabled"

        const val KEY_APP_OPEN_AD_UNIT_ID = "app_open_ad_unit_id"
        const val KEY_BANNER_AD_UNIT_ID = "banner_ad_unit_id"
        const val KEY_INTERSTITIAL_AD_UNIT_ID = "interstitial_ad_unit_id"
        const val KEY_REWARDED_AD_UNIT_ID = "rewarded_ad_unit_id"

        const val KEY_AD_SHOW_PROBABILITY = "ad_show_probability"

        // Module Notification Config Keys
        const val KEY_MODULE_NOTIFICATIONS_CONFIG = "module_notifications_config"

        // AdMob Test Unit IDs
        const val DEFAULT_APP_OPEN_AD_UNIT = "ca-app-pub-3940256099942544/9257395921"
        const val DEFAULT_BANNER_AD_UNIT = "ca-app-pub-3940256099942544/6300978111"
        const val DEFAULT_INTERSTITIAL_AD_UNIT = "ca-app-pub-3940256099942544/1033173712"
        const val DEFAULT_REWARDED_AD_UNIT = "ca-app-pub-3940256099942544/5224354917"

        // Production Fallback Live Ad Unit IDs
        const val FALLBACK_APP_OPEN_AD_UNIT = "ca-app-pub-2632394200654942/2336849258"
        const val FALLBACK_BANNER_AD_UNIT = "ca-app-pub-2632394200654942/9055400925"
        const val FALLBACK_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/2170428374"
        const val FALLBACK_REWARDED_AD_UNIT = "ca-app-pub-2632394200654942/2953027000"
    }

    init {
        setupRemoteConfig()
    }

    private fun setupRemoteConfig() {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (com.dv.apna.BuildConfig.DEBUG) 0L else 3600L
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        val isDebug = com.dv.apna.BuildConfig.DEBUG
        val defaults = mapOf(
            KEY_ADS_ENABLED to true,
            KEY_APP_OPEN_ADS_ENABLED to true,
            KEY_BANNER_ADS_ENABLED to true,
            KEY_INTERSTITIAL_ADS_ENABLED to true,
            KEY_REWARDED_ADS_ENABLED to true,
            KEY_APP_OPEN_AD_UNIT_ID to if (isDebug) DEFAULT_APP_OPEN_AD_UNIT else FALLBACK_APP_OPEN_AD_UNIT,
            KEY_BANNER_AD_UNIT_ID to if (isDebug) DEFAULT_BANNER_AD_UNIT else FALLBACK_BANNER_AD_UNIT,
            KEY_INTERSTITIAL_AD_UNIT_ID to if (isDebug) DEFAULT_INTERSTITIAL_AD_UNIT else FALLBACK_INTERSTITIAL_AD_UNIT,
            KEY_REWARDED_AD_UNIT_ID to if (isDebug) DEFAULT_REWARDED_AD_UNIT else FALLBACK_REWARDED_AD_UNIT,
            KEY_AD_SHOW_PROBABILITY to 1.0,
            KEY_MODULE_NOTIFICATIONS_CONFIG to """
                {
                    "construction": true,
                    "labour": true,
                    "transport": true,
                    "mandi": true,
                    "health": true,
                    "family": true,
                    "news": true,
                    "probability": 1.0
                }
            """.trimIndent()
        )
        remoteConfig.setDefaultsAsync(defaults)
        fetchAndActivate()
    }

    fun fetchAndActivate(onComplete: ((Boolean) -> Unit)? = null) {
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener { task ->
                val updated = task.isSuccessful
                Log.d(TAG, "RemoteConfig fetchAndActivate status: $updated, ad_show_probability: ${getAdShowProbability()}")
                onComplete?.invoke(updated)
            }
    }

    fun isAdsEnabled(): Boolean = remoteConfig.getBoolean(KEY_ADS_ENABLED)
    fun isAppOpenAdsEnabled(): Boolean = isAdsEnabled() && remoteConfig.getBoolean(KEY_APP_OPEN_ADS_ENABLED)
    fun isBannerAdsEnabled(): Boolean = isAdsEnabled() && remoteConfig.getBoolean(KEY_BANNER_ADS_ENABLED)
    fun isInterstitialAdsEnabled(): Boolean = isAdsEnabled() && remoteConfig.getBoolean(KEY_INTERSTITIAL_ADS_ENABLED)
    fun isRewardedAdsEnabled(): Boolean = isAdsEnabled() && remoteConfig.getBoolean(KEY_REWARDED_ADS_ENABLED)

    fun getAppOpenAdUnitId(): String {
        if (com.dv.apna.BuildConfig.DEBUG) return DEFAULT_APP_OPEN_AD_UNIT
        return remoteConfig.getString(KEY_APP_OPEN_AD_UNIT_ID).ifBlank { FALLBACK_APP_OPEN_AD_UNIT }
    }

    fun getBannerAdUnitId(): String {
        if (com.dv.apna.BuildConfig.DEBUG) return DEFAULT_BANNER_AD_UNIT
        return remoteConfig.getString(KEY_BANNER_AD_UNIT_ID).ifBlank { FALLBACK_BANNER_AD_UNIT }
    }

    fun getInterstitialAdUnitId(): String {
        if (com.dv.apna.BuildConfig.DEBUG) return DEFAULT_INTERSTITIAL_AD_UNIT
        return remoteConfig.getString(KEY_INTERSTITIAL_AD_UNIT_ID).ifBlank { FALLBACK_INTERSTITIAL_AD_UNIT }
    }

    fun getRewardedAdUnitId(): String {
        if (com.dv.apna.BuildConfig.DEBUG) return DEFAULT_REWARDED_AD_UNIT
        return remoteConfig.getString(KEY_REWARDED_AD_UNIT_ID).ifBlank { FALLBACK_REWARDED_AD_UNIT }
    }

    fun getAdShowProbability(): Double {
        val strVal = remoteConfig.getString(KEY_AD_SHOW_PROBABILITY)
        return strVal.toDoubleOrNull() ?: try {
            remoteConfig.getDouble(KEY_AD_SHOW_PROBABILITY)
        } catch (e: Exception) {
            1.0
        }
    }

    /**
     * Checks whether an ad should be displayed based on probability check & feature flag.
     */
    fun shouldShowAdWithProbability(isEnabled: Boolean): Boolean {
        if (!isEnabled) return false
        val prob = getAdShowProbability()
        if (prob >= 1.0) return true
        if (prob <= 0.0) return false
        return Random.nextDouble() <= prob
    }

    /**
     * Checks if notification for a specific module is enabled from Firebase Remote Config.
     */
    fun isModuleNotificationEnabled(moduleKey: String): Boolean {
        return try {
            val jsonStr = remoteConfig.getString(KEY_MODULE_NOTIFICATIONS_CONFIG)
            if (jsonStr.isBlank()) return true
            val jsonObj = JSONObject(jsonStr)
            if (jsonObj.has(moduleKey)) {
                jsonObj.getBoolean(moduleKey)
            } else {
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing module notifications config", e)
            true
        }
    }
}
