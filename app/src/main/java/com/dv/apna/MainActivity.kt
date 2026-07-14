package com.dv.apna

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.dv.apna.core.utils.LocaleUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        applySavedLocale()
        enableEdgeToEdge()
        askNotificationPermission()
        subscribeToSavedVillageTopic()

        // Extract notification data from Intent Bundle
        val id = intent?.getStringExtra("id") ?: intent?.getStringExtra("notification_id")
        val type = intent?.getStringExtra("type") ?: intent?.getStringExtra("notification_type")
        
        Log.d("FCM_DEBUG", "MainActivity Intent data: id=$id, type=$type")

        setContent {
            AapanGavTheme {
                val navController = rememberNavController()
                // Start with Splash carrying the notification data
                RootNavGraph(
                    navController = navController, 
                    startDestination = Route.Splash(notificationId = id, notificationType = type)
                )
            }
        }
    }

    private fun applySavedLocale() {
        lifecycleScope.launch {
            val languageCode = preferenceManager.languageCode.firstOrNull()
            if (languageCode != null) {
                LocaleUtils.setLocale(languageCode)
            }
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

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
