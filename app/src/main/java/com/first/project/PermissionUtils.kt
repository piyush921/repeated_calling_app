package com.first.project

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun checkReadContactsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askReadContactsPermission(context: Context, launcher: ActivityResultLauncher<String?>) {
        if (shouldShowRequestPermissionRationale(context as Activity, android.Manifest.permission.READ_CONTACTS)) {
            //user has denied permission. Now show dialog with explaination.

        } else {
            launcher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

}