package com.example.hmwrk10

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat

class NotificationService : Service() {
    private var isServiceRunning = false
    private val notificationId = 1
    private val channelId = "notification_channel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceRunning) {
            showNotification()
            isServiceRunning = true
        }

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "CHANNEL_NAME"
            val descriptionText = "CHANNEL_DESCRIPTION"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    @SuppressLint("LaunchActivityFromNotification", "ForegroundServiceType")
    private fun showNotification() {
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            val intent = Intent(this, NotificationService::class.java)
            val pendingIntent = PendingIntent.getService(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val disableIntent = Intent(this, NotificationService::class.java)
            disableIntent.action = "ACTION_DISABLE"
            val disablePendingIntent = PendingIntent.getService(
                this, 0, disableIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val notification = NotificationCompat.Builder(this, channelId)
                .setContentTitle("Notification Title")
                .setContentText("This is the notification content")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_launcher_foreground, "Disable", disablePendingIntent)
                .build()

            val serviceIntent = Intent(this, NotificationService::class.java)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                startForegroundService(serviceIntent.putExtras(notification.extras))
            } else {
                startForeground(notificationId, notification)
            }
        }, 2000) // Delay in milliseconds (2 seconds)
    }


    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
    }
}
