package com.example.myapplication.geofence;

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.os.Build // Build 클래스 import 필요
object NotificationHelper {

    private const val CHANNEL_ID = "geofence_channel"
    private const val CHANNEL_NAME = "위치 기반 메모"
    private const val NOTIFICATION_ID = 1001

    fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "특정 위치에 도착하면 알림을 보냅니다"
            }

            val manager =
                context.getSystemService(NotificationManager::class.java)

            manager?.createNotificationChannel(channel)
        }
    }

    @androidx.annotation.RequiresPermission(
        android.Manifest.permission.POST_NOTIFICATIONS
    )
    fun sendEnterNotification(context: Context) {

        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("목적지 도착")
                .setContentText("등록한 위치에 도착했습니다.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(NOTIFICATION_ID, notification)
    }
}
