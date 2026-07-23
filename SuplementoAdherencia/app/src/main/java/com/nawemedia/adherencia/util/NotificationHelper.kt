package com.nawemedia.adherencia.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.nawemedia.adherencia.AlarmActivity
import com.nawemedia.adherencia.R
import com.nawemedia.adherencia.alarm.ConfirmarTomaReceiver

/**
 * §8.5 Notificación del TAR.
 * - Canal de importancia HIGH (necesario para full-screen intent y heads-up).
 * - setFullScreenIntent con CATEGORY_ALARM: no se puede deslizar sin interacción.
 * - VISIBILITY_PRIVATE: el contenido no se lee con el teléfono bloqueado.
 */
object NotificationHelper {

    const val CHANNEL_ID = "tar_alarm_channel"
    const val NOTIFICATION_ID = 2001

    fun ensureChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                context.getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.channel_desc)
                enableVibration(true)
                setBypassDnd(false)
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * @param programacionIds ids de Programacion (dominio) que la acción "Tomé" de
     * esta notificación marca como confirmadas — F2: confirmación en un tap, sin
     * necesidad de abrir la app. Vacío = la notificación no ofrece esa acción directa.
     */
    fun showTarAlarm(context: Context, label: String, programacionIds: List<String> = emptyList()) {
        ensureChannel(context)

        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(AlarmActivity.EXTRA_LABEL, label)
        }
        val fullScreenPending = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_alarm)
            // VISIBILITY_PRIVATE: título genérico visible en lockscreen, sin nombre de fármaco.
            .setContentTitle(context.getString(R.string.alarm_notif_title))
            .setContentText(context.getString(R.string.alarm_notif_text))
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setOngoing(true)
            .setAutoCancel(false)
            .setFullScreenIntent(fullScreenPending, true)

        if (programacionIds.isNotEmpty()) {
            val confirmarIntent = Intent(context, ConfirmarTomaReceiver::class.java).apply {
                action = ConfirmarTomaReceiver.ACTION_CONFIRMAR
                putExtra(ConfirmarTomaReceiver.EXTRA_PROGRAMACION_IDS, programacionIds.toTypedArray())
            }
            val confirmarPending = PendingIntent.getBroadcast(
                context,
                1,
                confirmarIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(R.drawable.ic_alarm, context.getString(R.string.accion_tome), confirmarPending)
        }

        context.getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, builder.build())
    }

    fun dismiss(context: Context) {
        context.getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    }
}
