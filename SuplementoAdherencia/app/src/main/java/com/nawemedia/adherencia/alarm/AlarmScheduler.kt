package com.nawemedia.adherencia.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.nawemedia.adherencia.MainActivity
import com.nawemedia.adherencia.util.AlarmPrefs
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * §8.1 Motor de scheduling de Fase 1.
 *
 * Se usa AlarmManager.setAlarmClock() como mecanismo primario: es la única API
 * de AlarmManager que el sistema trata con máxima prioridad, es inmune a Doze,
 * aparece en el panel de alarmas del sistema y está exenta de la restricción de
 * SCHEDULE_EXACT_ALARM. NO se usa WorkManager (no garantiza exactitud bajo Doze).
 *
 * setAlarmClock() es de un solo disparo, así que la repetición diaria se logra
 * reprogramando la próxima ocurrencia cada vez que la alarma suena (AlarmReceiver)
 * y en cada BOOT_COMPLETED (BootReceiver).
 */
object AlarmScheduler {

    private const val TAG = "AlarmScheduler"
    const val REQUEST_CODE = 1001
    const val ACTION_ALARM = "com.nawemedia.adherencia.ACTION_ALARM_FIRED"
    const val EXTRA_LABEL = "extra_label"

    private fun alarmManager(context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /** PendingIntent que ejecuta el broadcast cuando suena la alarma. */
    private fun operationIntent(context: Context, label: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = ACTION_ALARM
            putExtra(EXTRA_LABEL, label)
        }
        return PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** PendingIntent "show" del AlarmClockInfo: abre la app desde el panel de alarmas. */
    private fun showIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            REQUEST_CODE + 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /** Próxima ocurrencia de la hora configurada, a partir de [from]. */
    fun computeNextTrigger(hour: Int, minute: Int, from: LocalDateTime = LocalDateTime.now()): Long {
        var next = from.toLocalDate().atTime(LocalTime.of(hour, minute))
        if (!next.isAfter(from)) {
            next = next.plusDays(1)
        }
        return next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /**
     * Programa la próxima ocurrencia de la hora guardada en prefs.
     * Idempotente: FLAG_UPDATE_CURRENT reemplaza cualquier alarma previa.
     */
    fun scheduleDaily(context: Context) {
        if (!AlarmPrefs.isEnabled(context)) {
            Log.i(TAG, "scheduleDaily: alarma deshabilitada, no se programa")
            return
        }
        val hour = AlarmPrefs.getHour(context)
        val minute = AlarmPrefs.getMinute(context)
        val triggerAt = computeNextTrigger(hour, minute)
        scheduleAt(context, triggerAt, label = "TAR (prueba diaria)")
    }

    /** Programa un disparo exacto en [triggerAtMillis] (usado también por "probar en 2 min"). */
    fun scheduleAt(context: Context, triggerAtMillis: Long, label: String) {
        val am = alarmManager(context)

        // setAlarmClock() está exento de SCHEDULE_EXACT_ALARM, pero dejamos traza
        // si en el futuro se migra a setExactAndAllowWhileIdle (Fase 2+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            Log.w(TAG, "canScheduleExactAlarms=false — setAlarmClock sigue funcionando, " +
                    "pero conviene conceder el permiso para fases futuras")
        }

        val info = AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent(context))
        am.setAlarmClock(info, operationIntent(context, label))

        AlarmPrefs.setNextTrigger(context, triggerAtMillis)
        AlarmPrefs.appendEvent(
            context,
            "Programada '$label' para ${formatMillis(triggerAtMillis)}"
        )
        Log.i(TAG, "Alarma programada para $triggerAtMillis ($label)")
    }

    fun cancel(context: Context) {
        alarmManager(context).cancel(operationIntent(context, ""))
        AlarmPrefs.setNextTrigger(context, 0L)
        AlarmPrefs.appendEvent(context, "Alarma cancelada")
    }

    private fun formatMillis(millis: Long): String {
        val dt = java.time.Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault()).toLocalDateTime()
        return String.format("%02d/%02d %02d:%02d", dt.dayOfMonth, dt.monthValue, dt.hour, dt.minute)
    }
}
