package com.dv.apna.core.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateManager(
    private val activity: AppCompatActivity
) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ ->
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
            }
        }
    }

    private fun startUpdateFlow(appUpdateInfo: AppUpdateInfo, updateType: Int) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                updateLauncher,
                AppUpdateOptions.defaultOptions(updateType)
            )
        } catch (e: Exception) {
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
                }
            }
        }
    }
}
