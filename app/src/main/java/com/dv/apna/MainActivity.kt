package com.dv.apna

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.navigation.RootNavGraph
import com.dv.apna.core.navigation.Route
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import android.content.res.Configuration
import java.util.Locale
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Resources
import androidx.navigation.NavHostController
import com.dv.apna.core.ads.AppOpenAdManager
import com.dv.apna.core.config.RemoteConfigManager
import com.dv.apna.core.utils.InAppUpdateManager

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var appOpenAdManager: AppOpenAdManager

    @Inject
    lateinit var remoteConfigManager: RemoteConfigManager

    private lateinit var inAppUpdateManager: InAppUpdateManager

    private var navController: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        subscribeToSavedVillageTopic()

        // Check Google Play Store for App Updates & launch native bottom sheet
        inAppUpdateManager = InAppUpdateManager(this)
        inAppUpdateManager.checkForUpdate()

        // Extract notification data from Intent Bundle
        val id = intent?.getStringExtra("id") ?: intent?.getStringExtra("notification_id")
        val type = intent?.getStringExtra("type") ?: intent?.getStringExtra("notification_type")

        setContent {
            val languageCodeState = preferenceManager.languageCode.collectAsState(initial = "en")
            val languageCode = languageCodeState.value ?: "en"

            val context = LocalContext.current
            val locale = remember(languageCode) { Locale(languageCode) }
            val config = remember(languageCode) {
                Configuration(context.resources.configuration).apply {
                    setLocale(locale)
                }
            }
            val localizedContext = remember(languageCode) {
                val configContext = context.createConfigurationContext(config)
                object : ContextWrapper(context) {
                    override fun getResources(): Resources {
                        return configContext.resources
                    }
                    override fun getAssets(): AssetManager {
                        return configContext.assets
                    }
                }
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides config
            ) {
                AapanGavTheme {
                    val controller = rememberNavController()
                    navController = controller
                    RootNavGraph(
                        navController = controller, 
                        startDestination = Route.Splash(notificationId = id, notificationType = type)
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val id = intent.getStringExtra("id") ?: intent.getStringExtra("notification_id")
        val type = intent.getStringExtra("type") ?: intent.getStringExtra("notification_type")
        if (!type.isNullOrBlank() || !id.isNullOrBlank()) {
            navController?.let { nav ->
                when (type?.lowercase()) {
                    "news" -> if (!id.isNullOrBlank()) nav.navigate(Route.NewsDetails(id)) else nav.navigate(Route.News)
                    "notice" -> if (!id.isNullOrBlank()) nav.navigate(Route.NoticeDetails(id)) else nav.navigate(Route.News)
                    "mandi", "mandi_buyers" -> nav.navigate(Route.TodayMarket)
                    "family", "family_tent", "family_catering", "family_dj", "family_halls" -> nav.navigate(Route.FamilyFunction)
                    "labour" -> nav.navigate(Route.LabourBoard)
                    "construction" -> nav.navigate(Route.Construction)
                    "transport" -> nav.navigate(Route.Transport)
                    "health" -> nav.navigate(Route.Health)
                    "add_business" -> nav.navigate(Route.AddBusiness)
                    "home", "bounce" -> nav.navigate(Route.Home)
                    else -> if (!id.isNullOrBlank()) nav.navigate(Route.NotificationDetails(id)) else nav.navigate(Route.Home)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::inAppUpdateManager.isInitialized) {
            inAppUpdateManager.resumeUpdateIfInProgress()
        }
    }

    private fun subscribeToSavedVillageTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("news")
        FirebaseMessaging.getInstance().subscribeToTopic("mandi")
        lifecycleScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId != null) {
                val topicName = if (villageId.startsWith("village_")) villageId else "village_$villageId"
                FirebaseMessaging.getInstance().subscribeToTopic(topicName)
            }
        }
    }
}
