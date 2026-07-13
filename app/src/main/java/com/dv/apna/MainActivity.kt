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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.dv.apna.core.datastore.PreferenceManager
import com.dv.apna.core.theme.AapanGavTheme
import com.dv.apna.core.navigation.RootNavGraph
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
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applySavedLocale()
        enableEdgeToEdge()
        askNotificationPermission()
        subscribeToSavedVillageTopic()
        
        setContent {
            AapanGavTheme {
                val navController = rememberNavController()
                RootNavGraph(navController = navController)
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
                Log.d("FCM", "Ensuring subscription to topic: $topicName")
                FirebaseMessaging.getInstance().subscribeToTopic(topicName)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("FCM", "Successfully subscribed to topic: $topicName")
                        } else {
                            Log.e("FCM", "Failed to subscribe to topic: $topicName", task.exception)
                        }
                    }
            } else {
                Log.d("FCM", "No villageId found in preferences, skipping subscription")
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val status = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            Log.d("MainActivity", "Notification permission status: $status")
            if (status != PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Requesting notification permission")
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AapanGavTheme {
        Greeting("Android")
    }
}