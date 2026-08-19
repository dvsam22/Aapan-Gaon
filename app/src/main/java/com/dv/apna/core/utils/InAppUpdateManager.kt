package com.dv.apna.core.utils

import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.dv.apna.core.config.RemoteConfigManager
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.crashlytics.FirebaseCrashlytics

class InAppUpdateManager(
    private val activity: AppCompatActivity
) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != AppCompatActivity.RESULT_OK) {
                Log.w(TAG, "In-App update flow cancelled or failed with result code: ${result.resultCode}")
                FirebaseCrashlytics.getInstance().log("InAppUpdate: Update flow cancelled/failed with code ${result.resultCode}")
            } else {
                Log.d(TAG, "In-App update flow completed successfully")
                FirebaseCrashlytics.getInstance().log("InAppUpdate: Update flow completed successfully")
            }
        }

    companion object {
        private const val TAG = "InAppUpdateManager"
    }

    /**
     * Checks Google Play Store for available app updates and launches the native Play Store bottom sheet / update dialog.
     */
    fun checkForUpdate() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startUpdateFlow(appUpdateInfo, AppUpdateType.IMMEDIATE)
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    startUpdateFlow(appUpdateInfo, AppUpdateType.FLEXIBLE)
                }
            } else {
                Log.d(TAG, "No app update available on Play Store")
            }
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to check Play Store app update", e)
            FirebaseCrashlytics.getInstance().log("InAppUpdate: Check failed: ${e.message}")
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        try {
            Log.d(TAG, "Starting native Google Play In-App Update flow (type: $updateType)")
            FirebaseCrashlytics.getInstance().log("InAppUpdate: Launching native Play Store update sheet (type: $updateType)")

            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.defaultOptions(updateType)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error launching native update flow", e)
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /**
     * Resumes immediate update if developer-triggered update is currently in progress.
     */
    fun resumeUpdateIfInProgress() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateLauncher,
                        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Error resuming update flow", e)
                }
            }
        }
    }
}
