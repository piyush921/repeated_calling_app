package com.first.project

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.content.ContextCompat

class CallService: Service() {

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val PHONE_NUMBER = "phone_number"
    }

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Calling Nitin")
            .setContentText("Running...")
            .setSmallIcon(R.drawable.ic_call)
            .build()
        startForeground(1, notification)
    }

    private fun startCall(phone: StringBuilder) {

        if (phone.length > 10 && !phone.startsWith("+")) {
            phone.insert(0, "+")
        } else if (phone.length == 10) {
            phone.insert(0, "+91")
        }

        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$phone")
        ContextCompat.startActivity(this, intent, null)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val phone = intent?.getStringExtra(PHONE_NUMBER)

        Handler(Looper.getMainLooper()).postDelayed({
            startCall(java.lang.StringBuilder(phone.toString()))
        }, 50000)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Call service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}