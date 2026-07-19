package com.nawemedia.adherencia.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nawemedia.adherencia.util.AlarmPrefs
import com.nawemedia.adherencia.util.NotificationHelper

/**
 * Se dispara cuando suena la alarma exacta.
 * 1. Muestra la notificación full-screen del TAR (§8.5).
 * 2. Reprograma la próxima ocurrencia diaria (setAlarmClock es de un solo disparo).
 * 3. Deja traza en el log de eventos (auditoría del gate de 48 h).
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val label = intent.getStringExtra(AlarmScheduler.EXTRA_LABEL) ?: "TAR"
        Log.i("AlarmReceiver", "onReceive: alarma disparada ($label)")

        AlarmPrefs.appendEvent(context, "🔔 DISPARÓ '$label'")
        NotificationHelper.showTarAlarm(context, label)

        // Reprograma la siguiente ocurrencia diaria para mantener la cadena viva.
        AlarmScheduler.scheduleDaily(context)
    }
}
