package com.dv.apna.core.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.dv.apna.R
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

class InAppUpdateManager(
    private val activity: AppCompatActivity
) : DefaultLifecycleObserver {

    companion object {
        // Play Console update priority is an integer from 0 to 5.
        // Priority 4 or 5 denotes a critical / breaking release requiring IMMEDIATE update.
        // Priority 0 to 3 uses non-blocking FLEXIBLE background update.
        private const val HIGH_PRIORITY_THRESHOLD = 4
    }

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    private val updateLauncher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { _ ->
        }

    private var isDialogShowing = false

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showUpdateDownloadedDialog()
        }
    }

    init {
        appUpdateManager.registerListener(installStateUpdatedListener)
        activity.lifecycle.addObserver(this)
    }

    /**
     * Checks Google Play Store for available app updates.
     * Evaluates update priority:
     * - Critical updates (priority >= 4) use IMMEDIATE full-screen blocking update.
     * - Standard / minor updates use FLEXIBLE background update (if allowed).
     */
    fun checkForUpdate() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                val priority = appUpdateInfo.updatePriority()
                val isHighPriority = priority >= HIGH_PRIORITY_THRESHOLD

                val updateType = when {
                    isHighPriority && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                        AppUpdateType.IMMEDIATE
                    }
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) -> {
                        AppUpdateType.FLEXIBLE
                    }
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) -> {
                        AppUpdateType.IMMEDIATE
                    }
                    else -> null
                }

                if (updateType != null) {
                    startUpdateFlow(appUpdateInfo, updateType)
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
        } catch (_: Exception) {
        }
    }

    /**
     * Checks if a flexible update has finished downloading or if an immediate update is in progress.
     */
    fun resumeUpdateIfInProgress() {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                showUpdateDownloadedDialog()
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateLauncher,
                        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                    )
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun showUpdateDownloadedDialog() {
        if (activity.isFinishing || activity.isDestroyed || isDialogShowing) return

        isDialogShowing = true
        AlertDialog.Builder(activity)
            .setTitle(R.string.update_downloaded_title)
            .setMessage(R.string.update_downloaded_message)
            .setPositiveButton(R.string.update_restart_btn) { _, _ ->
                isDialogShowing = false
                appUpdateManager.completeUpdate()
            }
            .setNegativeButton(R.string.update_later_btn) { dialog, _ ->
                isDialogShowing = false
                dialog.dismiss()
            }
            .setOnDismissListener {
                isDialogShowing = false
            }
            .setCancelable(false)
            .show()
    }

    override fun onResume(owner: LifecycleOwner) {
        resumeUpdateIfInProgress()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}
