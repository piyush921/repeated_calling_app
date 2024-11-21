package com.first.project

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.first.project.model.Contact

class CallService : Service() {

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        const val PHONE_NUMBER = "phone_number"
        const val NOTIFICATION_ID = 1
    }

    private lateinit var contact: Contact

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

    }

    private fun startCall() {

        val phone: StringBuilder = StringBuilder(contact.phone.toString())

        if (phone.length > 10 && !phone.startsWith("+")) {
            phone.insert(0, "+")
        } else if (phone.length == 10) {
            phone.insert(0, "+91")
        }

        val intent = Intent(Intent.ACTION_CALL).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent.data = Uri.parse("tel:$phone")
        ContextCompat.startActivity(this, intent, null)

        showNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        contact = intent?.getParcelableExtra<Contact>(PHONE_NUMBER) as Contact
        startCall()

        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed(object: Runnable {
            override fun run() {
                startCall()
               handler.postDelayed(this, 10000)
            }
        }, 10000)

        return START_STICKY
    }

    private fun showNotification() {
        val notification: Notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Calling ${contact.name}")
            .setContentText("Re-call in ")
            .setSmallIcon(R.drawable.ic_call)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Call service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}