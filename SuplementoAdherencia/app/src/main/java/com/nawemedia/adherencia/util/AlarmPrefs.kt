package com.nawemedia.adherencia.util

import android.content.Context
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Almacenamiento mínimo para Fase 1 (una sola alarma de prueba).
 *
 * NOTA DE ALCANCE: en Fase 1 se usa SharedPreferences plano a propósito.
 * La base cifrada (Room + SQLCipher, §6/§7) corresponde a Fase 0/2 y NO forma
 * parte del gate de Fase 1. Aquí solo persistimos: hora de la alarma, si está
 * activa, y un log de disparos para poder auditar el gate de 48 h.
 */
object AlarmPrefs {
    private const val FILE = "fase1_alarm_prefs"
    private const val KEY_HOUR = "hour"
    private const val KEY_MINUTE = "minute"
    private const val KEY_ENABLED = "enabled"
    private const val KEY_NEXT_TRIGGER = "next_trigger_millis"
    private const val KEY_EVENT_LOG = "event_log"

    private val logFormatter: DateTimeFormatter =
        DateTimeFormatter.ofPattern("dd/MM HH:mm:ss").withZone(ZoneId.systemDefault())

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun setTime(context: Context, hour: Int, minute: Int) {
        prefs(context).edit()
            .putInt(KEY_HOUR, hour)
            .putInt(KEY_MINUTE, minute)
            .apply()
    }

    fun getHour(context: Context): Int = prefs(context).getInt(KEY_HOUR, 10)
    fun getMinute(context: Context): Int = prefs(context).getInt(KEY_MINUTE, 30)

    fun setEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun isEnabled(context: Context): Boolean = prefs(context).getBoolean(KEY_ENABLED, false)

    fun setNextTrigger(context: Context, millis: Long) {
        prefs(context).edit().putLong(KEY_NEXT_TRIGGER, millis).apply()
    }

    fun getNextTrigger(context: Context): Long = prefs(context).getLong(KEY_NEXT_TRIGGER, 0L)

    /** Registra un evento (disparo, reprogramación, boot) para auditar el gate. */
    fun appendEvent(context: Context, message: String) {
        val stamp = logFormatter.format(Instant.now())
        val previous = prefs(context).getString(KEY_EVENT_LOG, "").orEmpty()
        val line = "$stamp — $message"
        // Mantiene las últimas ~50 líneas; lo más nuevo arriba.
        val combined = (line + "\n" + previous).lines().take(50).joinToString("\n")
        prefs(context).edit().putString(KEY_EVENT_LOG, combined).apply()
    }

    fun getEventLog(context: Context): String =
        prefs(context).getString(KEY_EVENT_LOG, "").orEmpty()

    fun clearEventLog(context: Context) {
        prefs(context).edit().remove(KEY_EVENT_LOG).apply()
    }
}
