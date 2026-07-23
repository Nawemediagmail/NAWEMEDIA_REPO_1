package com.nawemedia.adherencia.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nawemedia.adherencia.util.AlarmPrefs
import com.nawemedia.adherencia.util.ConfirmacionesPrefs
import com.nawemedia.adherencia.util.NotificationHelper

/**
 * F2 — confirmación de toma en un tap desde la notificación, sin abrir la app.
 * Marca las Programacion.id recibidas como confirmadas (ConfirmacionesPrefs) y
 * descarta la notificación.
 */
class ConfirmarTomaReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CONFIRMAR = "com.nawemedia.adherencia.ACTION_CONFIRMAR_TOMA"
        const val EXTRA_PROGRAMACION_IDS = "extra_programacion_ids"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val ids = intent.getStringArrayExtra(EXTRA_PROGRAMACION_IDS) ?: emptyArray()
        ids.forEach { ConfirmacionesPrefs.confirmar(context, it) }
        NotificationHelper.dismiss(context)
        AlarmPrefs.appendEvent(context, "✅ Confirmado desde notificación (${ids.joinToString()})")
    }
}
