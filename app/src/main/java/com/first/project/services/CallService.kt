package com.first.project.services

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
import com.first.project.Constants
import com.first.project.PermissionUtils
import com.first.project.R
import com.first.project.model.Contact

class CallService : Service() {

    private lateinit var contact: Contact
    private var reCallTime: Int = 50
    private val timerHandler = Handler(Looper.getMainLooper())
    private val recallHandler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

    }

    private fun startCall() {
        val intent = Intent(Intent.ACTION_CALL).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        intent.data = Uri.parse("tel:${contact.phone}")
        ContextCompat.startActivity(this, intent, null)

        showNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        contact = intent?.getParcelableExtra<Contact>(Constants.PHONE_NUMBER) as Contact
        startCall()
        startReCallTimer()

        timerHandler.postDelayed(object: Runnable {
            override fun run() {
                startCall()
                reCallTime = 50
               timerHandler.postDelayed(this, 50000)
            }
        }, 50000)

        return START_STICKY
    }

    private fun startReCallTimer() {
        recallHandler.postDelayed(object: Runnable {
            override fun run() {
                reCallTime--
                updateNotificationTimer()
                timerHandler.postDelayed(this, 1000)
            }
        }, 1000)
    }

    private fun showNotification() {
        val notification: Notification = Notification.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle("Calling ${contact.name}")
            .setContentText("Re-call in ")
            .setSmallIcon(R.drawable.ic_call)
            .setOngoing(true)
            .build()
        startForeground(Constants.NOTIFICATION_ID, notification)
    }

    private fun updateNotificationTimer() {
        val updatedNotification = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_call)
            .setContentTitle("Calling ${contact.name}")
            .setContentText("Re-calling in $reCallTime seconds")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(this)
        if (PermissionUtils.checkNotificationPermission(this)) {
            notificationManager.notify(Constants.NOTIFICATION_ID, updatedNotification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        timerHandler.removeCallbacksAndMessages(null)
        recallHandler.removeCallbacksAndMessages(null)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.CHANNEL_ID,
            "Call service",
            NotificationManager.IMPORTANCE_NONE
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

}