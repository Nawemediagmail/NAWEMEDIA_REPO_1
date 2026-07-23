package com.nawemedia.adherencia.util

import android.content.Context
import java.time.LocalDate

/**
 * Confirmaciones de toma del día, por Programacion.id. Fase 3: SharedPreferences
 * plano, igual que AlarmPrefs — Room+SQLCipher (persistencia real, histórico) es
 * Fase 0/5, no forma parte de este alcance.
 */
object ConfirmacionesPrefs {
    private const val FILE = "confirmaciones_prefs"

    private fun prefs(context: Context) = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
    private fun key(hoy: LocalDate, programacionId: String) = "${hoy}_$programacionId"

    fun estaConfirmado(context: Context, programacionId: String, hoy: LocalDate = LocalDate.now()): Boolean =
        prefs(context).getBoolean(key(hoy, programacionId), false)

    fun confirmar(context: Context, programacionId: String, hoy: LocalDate = LocalDate.now()) {
        prefs(context).edit().putBoolean(key(hoy, programacionId), true).apply()
    }

    fun quitarConfirmacion(context: Context, programacionId: String, hoy: LocalDate = LocalDate.now()) {
        prefs(context).edit().remove(key(hoy, programacionId)).apply()
    }
}
