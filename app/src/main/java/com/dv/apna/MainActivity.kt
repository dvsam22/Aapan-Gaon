package com.dv.apna

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.activity.result.contract.ActivityResultContracts
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

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    @Inject
    lateinit var appOpenAdManager: com.dv.apna.core.ads.AppOpenAdManager

    @Inject
    lateinit var remoteConfigManager: com.dv.apna.core.config.RemoteConfigManager

    private lateinit var inAppUpdateManager: com.dv.apna.core.utils.InAppUpdateManager

    private var navController: androidx.navigation.NavHostController? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        subscribeToSavedVillageTopic()

        // Check Google Play Store for App Updates & launch native bottom sheet
        inAppUpdateManager = com.dv.apna.core.utils.InAppUpdateManager(this)
        inAppUpdateManager.checkForUpdate()

        // Extract notification data from Intent Bundle
        val id = intent?.getStringExtra("id") ?: intent?.getStringExtra("notification_id")
        val type = intent?.getStringExtra("type") ?: intent?.getStringExtra("notification_type")
        
        Log.d("FCM_DEBUG", "MainActivity Intent data: id=$id, type=$type")

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
                object : android.content.ContextWrapper(context) {
                    override fun getResources(): android.content.res.Resources {
                        return configContext.resources
                    }
                    override fun getAssets(): android.content.res.AssetManager {
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

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val id = intent.getStringExtra("id") ?: intent.getStringExtra("notification_id")
        val type = intent.getStringExtra("type") ?: intent.getStringExtra("notification_type")
        Log.d("FCM_DEBUG", "onNewIntent received: id=$id, type=$type")
        if (!id.isNullOrBlank()) {
            navController?.let { nav ->
                when (type?.lowercase()) {
                    "news" -> nav.navigate(Route.NewsDetails(id))
                    "notice" -> nav.navigate(Route.NoticeDetails(id))
                    else -> nav.navigate(Route.NotificationDetails(id))
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
        lifecycleScope.launch {
            val villageId = preferenceManager.villageId.firstOrNull()
            if (villageId != null) {
                val topicName = if (villageId.startsWith("village_")) villageId else "village_$villageId"
                FirebaseMessaging.getInstance().subscribeToTopic(topicName)
            }
        }
    }
}
