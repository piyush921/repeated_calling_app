package com.first.project

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun checkNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun askNotificationPermission(context: Context, launcher: ActivityResultLauncher<String>) {
        if (shouldShowRequestPermissionRationale(
                context as Activity,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            redirectToSettings(context)
            Toast.makeText(context, "please allow Notification", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    fun checkReadContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askReadContactsPermission(context: Context, launcher: ActivityResultLauncher<String>) {
        if (shouldShowRequestPermissionRationale(
                context as Activity,
                android.Manifest.permission.READ_CONTACTS
            )
        ) {
            redirectToSettings(context)
            Toast.makeText(context, "please provide contacts permission", Toast.LENGTH_SHORT).show()
        } else {
            launcher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

    fun checkCallPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CALL_PHONE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askCallPermission(context: Context, launcher: ActivityResultLauncher<String>) {
        if (shouldShowRequestPermissionRationale(
                context as Activity,
                android.Manifest.permission.CALL_PHONE
            )
        ) {
            redirectToSettings(context)
            Toast.makeText(context, "please provide call permission", Toast.LENGTH_SHORT).show()
        } else {
            launcher.launch(android.Manifest.permission.CALL_PHONE)
        }
    }

    private fun redirectToSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

}