package com.example.myapplication.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        android.util.Log.d(
            "GEOFENCE",
            "브로드캐스트 수신"
        )

        val geofencingEvent =
            GeofencingEvent.fromIntent(intent)

        if (geofencingEvent == null) {

            android.util.Log.d(
                "GEOFENCE",
                "event null"
            )

            return
        }

        if (geofencingEvent.hasError()) {

            android.util.Log.e(
                "GEOFENCE",
                "error = ${geofencingEvent.errorCode}"
            )

            return
        }

        val transition =
            geofencingEvent.geofenceTransition

        android.util.Log.d(
            "GEOFENCE",
            "transition = $transition"
        )

        when (transition) {

            Geofence.GEOFENCE_TRANSITION_ENTER -> {

                android.util.Log.d(
                    "GEOFENCE",
                    "ENTER 발생"
                )

                val triggeringGeofences =
                    geofencingEvent.triggeringGeofences

                triggeringGeofences?.forEach { geofence ->

                    android.util.Log.d(
                        "GEOFENCE",
                        "도착 감지 : ${geofence.requestId}"
                    )
                }

                if (android.os.Build.VERSION.SDK_INT >=
                    android.os.Build.VERSION_CODES.TIRAMISU
                ) {

                    if (
                        androidx.core.content.ContextCompat.checkSelfPermission(
                            context,
                            android.Manifest.permission.POST_NOTIFICATIONS
                        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                    ) {

                        NotificationHelper.sendEnterNotification(context)
                    }

                } else {

                    NotificationHelper.sendEnterNotification(context)
                }
            }

            Geofence.GEOFENCE_TRANSITION_EXIT -> {

                android.util.Log.d(
                    "GEOFENCE",
                    "EXIT 발생"
                )
            }

            Geofence.GEOFENCE_TRANSITION_DWELL -> {

                android.util.Log.d(
                    "GEOFENCE",
                    "DWELL 발생"
                )
            }

            else -> {

                android.util.Log.d(
                    "GEOFENCE",
                    "알 수 없는 이벤트 : $transition"
                )
            }
        }
    }
}