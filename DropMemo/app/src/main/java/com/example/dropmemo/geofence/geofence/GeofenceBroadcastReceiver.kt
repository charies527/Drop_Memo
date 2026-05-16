package com.example.dropmemo.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            android.util.Log.e("GeofenceReceiver", "지오펜싱 에러 코드: ${geofencingEvent.errorCode}")
            return
        }

        val transition = geofencingEvent.geofenceTransition

        if (transition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            val triggeringGeofences = geofencingEvent.triggeringGeofences

            triggeringGeofences?.forEach { geofence ->
                android.util.Log.d("GeofenceReceiver", "구역 이탈 감지: ${geofence.requestId}")
            }

            NotificationHelper.sendExitNotification(context)
        }
    }
}
