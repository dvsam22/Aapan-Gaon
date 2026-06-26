package com.dv.apna

import android.app.Application
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
            }
        } catch (e: Exception) {
            // If it fails (missing google-services.json), initialize with dummy options to prevent crash
            val options = FirebaseOptions.Builder()
                .setApplicationId("com.example.aapangav") // Should match your package
                .setApiKey("unused")
                .setProjectId("unused")
                .build()
            FirebaseApp.initializeApp(this, options)
        }
    }
}
