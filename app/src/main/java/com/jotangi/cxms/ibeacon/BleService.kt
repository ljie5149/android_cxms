package com.ptv.ibeacon.receiver.ibeacon

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.core.app.NotificationCompat
import com.jotangi.cxms.MainActivity

class BleService : Service() {
    private val NOTIFICATION_ID = 1  // Unique ID for the notification
    private val CHANNEL_ID = "ibeacon_scanner_channel"  // Unique ID for the notification channel

    private lateinit var iBeaconScanner: IBeaconScanner
    override fun onCreate() {
        super.onCreate()
        // Setup as a foreground service
        iBeaconScanner = IBeaconScanner(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()  // Create the notification channel

        val contentIntent: PendingIntent

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Specify FLAG_IMMUTABLE or FLAG_MUTABLE based on your needs
            contentIntent = PendingIntent.getBroadcast(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE);
        } else {
            contentIntent = PendingIntent.getBroadcast(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("IBeacon scanner")
            .setContentText("IBeacon scanner service is running.")
//            .setSmallIcon(R.drawable.ic_notification)  // Ensure this icon exists
            .setContentIntent(contentIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        Log.i("IBeaconService", "Service started ====")
        iBeaconScanner.startScanning()
        return START_STICKY
    }

    override fun onDestroy() {
        iBeaconScanner.stopScanning()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null  // We don't need to bind the service
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "IBeacon Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "IBeacon scanner"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }
}