package com.dv.apna.feature.home.presentation.component

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dv.apna.R
import com.dv.apna.core.utils.sdp
import com.dv.apna.core.utils.ssp

private val BrandGreen = Color(0xFF38C792)
private val LightGreenBg = Color(0xFFEFFAF6)
private val TextDark = Color(0xFF1E232C)
private val TextMuted = Color(0xFF8391A1)

fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}

fun isNotificationPermissionGranted(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    } else {
        NotificationManagerCompat.from(context).areNotificationsEnabled()
    }
}

fun openAppNotificationSettings(context: Context) {
    try {
        val intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", context.packageName, null)
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (_: Exception) {
        val fallback = Intent(Settings.ACTION_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallback)
    }
}

@Composable
fun NotificationPermissionDialog(
    onDismiss: () -> Unit,
    onPermissionResult: (Boolean) -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val prefs = context.getSharedPreferences("apna_notif_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_prompted_system_notif", true).apply()

        if (isGranted) {
            onPermissionResult(true)
        } else {
            val act = context.findActivity()
            val isPermanentlyDenied = act != null &&
                    !ActivityCompat.shouldShowRequestPermissionRationale(
                        act,
                        Manifest.permission.POST_NOTIFICATIONS
                    )
            if (isPermanentlyDenied) {
                Toast.makeText(
                    context,
                    context.getString(R.string.notif_open_settings_toast),
                    Toast.LENGTH_LONG
                ).show()
                openAppNotificationSettings(context)
                onDismiss()
            } else {
                onPermissionResult(false)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.sdp()),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.sdp()),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.sdp())
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.sdp())
                        .background(LightGreenBg, RoundedCornerShape(8.sdp()))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.sdp()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Top Bell Icon Badge
                        Box(
                            modifier = Modifier
                                .size(56.sdp())
                                .clip(CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🔔",
                                fontSize = 28.ssp()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.sdp()))

                        // Title
                        Text(
                            text = stringResource(id = R.string.notif_dialog_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.ssp()
                            ),
                            color = TextDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.sdp()))

                        // Subtitle
                        Text(
                            text = stringResource(id = R.string.notif_dialog_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 12.ssp(),
                                color = TextMuted
                            ),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.sdp()))

                        // Top Action (Turn On Notifications) - Matching App Pill Shape
                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val act = context.findActivity()
                                    val isPermanentlyDenied = act != null &&
                                            !ActivityCompat.shouldShowRequestPermissionRationale(
                                                act,
                                                Manifest.permission.POST_NOTIFICATIONS
                                            ) &&
                                            ContextCompat.checkSelfPermission(
                                                context,
                                                Manifest.permission.POST_NOTIFICATIONS
                                            ) != PackageManager.PERMISSION_GRANTED

                                    val prefs = context.getSharedPreferences("apna_notif_prefs", Context.MODE_PRIVATE)
                                    val hasAskedOnce = prefs.getBoolean("has_prompted_system_notif", false)

                                    if (hasAskedOnce && isPermanentlyDenied) {
                                        Toast.makeText(
                                            context,
                                            context.getString(R.string.notif_open_settings_toast),
                                            Toast.LENGTH_LONG
                                        ).show()
                                        openAppNotificationSettings(context)
                                        onDismiss()
                                    } else {
                                        prefs.edit().putBoolean("has_prompted_system_notif", true).apply()
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    openAppNotificationSettings(context)
                                    onDismiss()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.sdp()),
                            shape = RoundedCornerShape(27.sdp()),
                            contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.sdp()),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                        ) {
                            Text(
                                text = stringResource(id = R.string.notif_turn_on_btn),
                                color = Color.White,
                                fontSize = 13.ssp(),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.sdp()))

                        // Bottom Action (Later) - Matching App Pill Shape
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.sdp()),
                            shape = RoundedCornerShape(27.sdp()),
                            contentPadding = PaddingValues(horizontal = 12.sdp(), vertical = 0.sdp()),
                            border = BorderStroke(1.sdp(), BrandGreen),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandGreen)
                        ) {
                            Text(
                                text = stringResource(id = R.string.notif_later_btn),
                                color = BrandGreen,
                                fontSize = 13.ssp(),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
