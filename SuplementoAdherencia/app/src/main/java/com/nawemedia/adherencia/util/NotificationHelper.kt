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

    fun showTarAlarm(context: Context, label: String) {
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

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
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
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(NOTIFICATION_ID, notification)
    }

    fun dismiss(context: Context) {
        context.getSystemService(NotificationManager::class.java).cancel(NOTIFICATION_ID)
    }
}
