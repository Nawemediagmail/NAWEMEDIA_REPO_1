package com.nawemedia.adherencia.util

import android.content.Context

/** Preferencias de usuario de Fase 3: selector excluyente de magnesio nocturno, modo entreno. */
object PreferenciasUsuario {
    private const val FILE = "preferencias_usuario"

    private fun prefs(context: Context) = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun seleccionGrupo(context: Context, grupo: String, defaultId: String): String =
        prefs(context).getString("grupo_$grupo", defaultId) ?: defaultId

    fun elegirGrupo(context: Context, grupo: String, programacionId: String) {
        prefs(context).edit().putString("grupo_$grupo", programacionId).apply()
    }

    fun modoEntrenoActivo(context: Context): Boolean = prefs(context).getBoolean("modo_entreno", false)

    fun setModoEntrenoActivo(context: Context, activo: Boolean) {
        prefs(context).edit().putBoolean("modo_entreno", activo).apply()
    }
}
