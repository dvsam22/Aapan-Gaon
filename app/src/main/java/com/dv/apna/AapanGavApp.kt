package com.dv.apna

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AapanGavApp : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("FirebaseInit", "Firebase initialized successfully with google-services.json")
            }
        } catch (e: Exception) {
            Log.e("FirebaseInit", "Firebase init failed, using fallback: ${e.message}")
            // If it fails (missing google-services.json), initialize with dummy options to prevent crash
            val options = FirebaseOptions.Builder()
                .setApplicationId("com.dv.apna") // Updated to match your package
                .setApiKey("unused")
                .setProjectId("unused")
                .build()
            FirebaseApp.initializeApp(this, options)
        }
    }
}
