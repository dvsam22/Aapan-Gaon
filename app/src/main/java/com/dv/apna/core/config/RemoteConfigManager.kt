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

enum class ServiceAdCategory {
    GENERAL,
    CONSTRUCTION,
    LABOUR,
    TRANSPORT,
    MANDI,
    HEALTH,
    FAMILY,
    NEWS,
    NOTIFICATION
}

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
        const val KEY_APP_OPEN_AD_UNIT_ID = "app_open_ad_unit_id"
        const val KEY_BANNER_AD_UNIT_ID = "banner_ad_unit_id"
        const val KEY_INTERSTITIAL_AD_UNIT_ID = "interstitial_ad_unit_id"

        // Category-Specific Interstitial Ad Unit Keys
        const val KEY_INTERSTITIAL_CONSTRUCTION_AD_UNIT_ID = "interstitial_construction_ad_unit_id"
        const val KEY_INTERSTITIAL_LABOUR_AD_UNIT_ID = "interstitial_labour_ad_unit_id"
        const val KEY_INTERSTITIAL_TRANSPORT_AD_UNIT_ID = "interstitial_transport_ad_unit_id"
        const val KEY_INTERSTITIAL_MANDI_AD_UNIT_ID = "interstitial_mandi_ad_unit_id"
        const val KEY_INTERSTITIAL_HEALTH_AD_UNIT_ID = "interstitial_health_ad_unit_id"
        const val KEY_INTERSTITIAL_FAMILY_AD_UNIT_ID = "interstitial_family_ad_unit_id"
        const val KEY_INTERSTITIAL_NEWS_AD_UNIT_ID = "interstitial_news_ad_unit_id"

        const val KEY_NATIVE_ADS_ENABLED = "native_ads_enabled"
        const val KEY_NATIVE_AD_UNIT_ID = "native_ad_unit_id"

        const val KEY_AD_SHOW_PROBABILITY = "ad_show_probability"

        // AdMob Test Unit IDs
        const val DEFAULT_APP_OPEN_AD_UNIT = "ca-app-pub-3940256099942544/9257395921"
        const val DEFAULT_BANNER_AD_UNIT = "ca-app-pub-3940256099942544/6300978111"
        const val DEFAULT_INTERSTITIAL_AD_UNIT = "ca-app-pub-3940256099942544/1033173712"
        const val DEFAULT_NATIVE_AD_UNIT = "ca-app-pub-3940256099942544/2247696110"

        // Production Default Live Ad Unit IDs per Category (from AdMob Console)
        const val PROD_APP_OPEN_AD_UNIT = "ca-app-pub-2632394200654942/3242701422" // splash_open
        const val PROD_BANNER_AD_UNIT = "ca-app-pub-2632394200654942/2271300459" // Home_Banner
        const val PROD_GENERAL_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/7128161450" // common_Interstitial
        const val PROD_CONSTRUCTION_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/9910824193" // interstitial_construction
        const val PROD_LABOUR_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/5205292420" // interstitial_labour
        const val PROD_TRANSPORT_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/3700639065" // interstitial_transport
        const val PROD_MANDI_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/1266047417" // interstitial_mandi
        const val PROD_HEALTH_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/2035937672" // interstitial_health
        const val PROD_FAMILY_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/1593084545" // interstitial_family
        const val PROD_NEWS_INTERSTITIAL_AD_UNIT = "ca-app-pub-2632394200654942/6394251014" // interstitial_news

        // Production Default Live Native Ad Unit IDs per Category (from AdMob Console)
        const val PROD_CONSTRUCTION_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/9805024160"
        const val PROD_LABOUR_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/1935890978"
        const val PROD_TRANSPORT_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/7926584255"
        const val PROD_MANDI_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/4613920009"
        const val PROD_HEALTH_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/5586577558"
        const val PROD_FAMILY_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/4081924190"
        const val PROD_NEWS_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/8759535803"
        const val PROD_NOTIFICATION_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/9642885408"
        const val PROD_GENERAL_NATIVE_AD_UNIT = "ca-app-pub-2632394200654942/9642885408"
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
            KEY_APP_OPEN_AD_UNIT_ID to if (isDebug) DEFAULT_APP_OPEN_AD_UNIT else PROD_APP_OPEN_AD_UNIT,
            KEY_BANNER_AD_UNIT_ID to if (isDebug) DEFAULT_BANNER_AD_UNIT else PROD_BANNER_AD_UNIT,
            KEY_INTERSTITIAL_AD_UNIT_ID to if (isDebug) DEFAULT_INTERSTITIAL_AD_UNIT else PROD_GENERAL_INTERSTITIAL_AD_UNIT,
            KEY_INTERSTITIAL_CONSTRUCTION_AD_UNIT_ID to PROD_CONSTRUCTION_INTERSTITIAL_AD_UNIT,
            KEY_INTERSTITIAL_LABOUR_AD_UNIT_ID to PROD_LABOUR_INTERSTITIAL_AD_UNIT,
            KEY_INTERSTITIAL_TRANSPORT_AD_UNIT_ID to PROD_TRANSPORT_INTERSTITIAL_AD_UNIT,
            KEY_INTERSTITIAL_MANDI_AD_UNIT_ID to PROD_MANDI_INTERSTITIAL_AD_UNIT,
            KEY_INTERSTITIAL_HEALTH_AD_UNIT_ID to PROD_HEALTH_INTERSTITIAL_AD_UNIT,
            KEY_INTERSTITIAL_FAMILY_AD_UNIT_ID to PROD_FAMILY_INTERSTITIAL_AD_UNIT,
            KEY_INTERSTITIAL_NEWS_AD_UNIT_ID to PROD_NEWS_INTERSTITIAL_AD_UNIT,
            KEY_NATIVE_ADS_ENABLED to true,
            KEY_NATIVE_AD_UNIT_ID to DEFAULT_NATIVE_AD_UNIT,
            KEY_AD_SHOW_PROBABILITY to 1.0
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
    fun isNativeAdsEnabled(): Boolean = isAdsEnabled() && remoteConfig.getBoolean(KEY_NATIVE_ADS_ENABLED)

    fun getNativeAdUnitId(category: ServiceAdCategory = ServiceAdCategory.GENERAL): String {
        if (com.dv.apna.BuildConfig.DEBUG) return DEFAULT_NATIVE_AD_UNIT
        return when (category) {
            ServiceAdCategory.CONSTRUCTION -> PROD_CONSTRUCTION_NATIVE_AD_UNIT
            ServiceAdCategory.LABOUR -> PROD_LABOUR_NATIVE_AD_UNIT
            ServiceAdCategory.TRANSPORT -> PROD_TRANSPORT_NATIVE_AD_UNIT
            ServiceAdCategory.MANDI -> PROD_MANDI_NATIVE_AD_UNIT
            ServiceAdCategory.HEALTH -> PROD_HEALTH_NATIVE_AD_UNIT
            ServiceAdCategory.FAMILY -> PROD_FAMILY_NATIVE_AD_UNIT
            ServiceAdCategory.NEWS -> PROD_NEWS_NATIVE_AD_UNIT
            ServiceAdCategory.NOTIFICATION -> PROD_NOTIFICATION_NATIVE_AD_UNIT
            ServiceAdCategory.GENERAL -> PROD_GENERAL_NATIVE_AD_UNIT
        }
    }

    fun getAppOpenAdUnitId(): String {
        if (com.dv.apna.BuildConfig.DEBUG) return DEFAULT_APP_OPEN_AD_UNIT
        val id = remoteConfig.getString(KEY_APP_OPEN_AD_UNIT_ID)
        return id.ifBlank { PROD_APP_OPEN_AD_UNIT }
    }

    fun getBannerAdUnitId(): String {
        if (com.dv.apna.BuildConfig.DEBUG) return DEFAULT_BANNER_AD_UNIT
        val id = remoteConfig.getString(KEY_BANNER_AD_UNIT_ID)
        return id.ifBlank { PROD_BANNER_AD_UNIT }
    }

    fun getInterstitialAdUnitId(category: ServiceAdCategory = ServiceAdCategory.GENERAL): String {
        if (com.dv.apna.BuildConfig.DEBUG) return DEFAULT_INTERSTITIAL_AD_UNIT
        
        val (key, prodDefault) = when (category) {
            ServiceAdCategory.CONSTRUCTION -> KEY_INTERSTITIAL_CONSTRUCTION_AD_UNIT_ID to PROD_CONSTRUCTION_INTERSTITIAL_AD_UNIT
            ServiceAdCategory.LABOUR -> KEY_INTERSTITIAL_LABOUR_AD_UNIT_ID to PROD_LABOUR_INTERSTITIAL_AD_UNIT
            ServiceAdCategory.TRANSPORT -> KEY_INTERSTITIAL_TRANSPORT_AD_UNIT_ID to PROD_TRANSPORT_INTERSTITIAL_AD_UNIT
            ServiceAdCategory.MANDI -> KEY_INTERSTITIAL_MANDI_AD_UNIT_ID to PROD_MANDI_INTERSTITIAL_AD_UNIT
            ServiceAdCategory.HEALTH -> KEY_INTERSTITIAL_HEALTH_AD_UNIT_ID to PROD_HEALTH_INTERSTITIAL_AD_UNIT
            ServiceAdCategory.FAMILY -> KEY_INTERSTITIAL_FAMILY_AD_UNIT_ID to PROD_FAMILY_INTERSTITIAL_AD_UNIT
            ServiceAdCategory.NEWS -> KEY_INTERSTITIAL_NEWS_AD_UNIT_ID to PROD_NEWS_INTERSTITIAL_AD_UNIT
            ServiceAdCategory.NOTIFICATION -> KEY_INTERSTITIAL_AD_UNIT_ID to PROD_GENERAL_INTERSTITIAL_AD_UNIT
            ServiceAdCategory.GENERAL -> KEY_INTERSTITIAL_AD_UNIT_ID to PROD_GENERAL_INTERSTITIAL_AD_UNIT
        }

        val customId = remoteConfig.getString(key)
        if (customId.isNotBlank()) return customId

        return prodDefault
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
