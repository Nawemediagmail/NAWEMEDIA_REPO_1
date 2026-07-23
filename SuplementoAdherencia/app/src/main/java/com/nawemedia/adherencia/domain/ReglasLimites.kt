package com.nawemedia.adherencia.domain

import java.time.LocalDate

/**
 * §3.3 — Límites superiores y alertas de seguridad, fuera de la regla de quelación
 * (§3.1, ver ReglaQuelacion.kt) y del contador de magnesio (ver ContadorMagnesio.kt).
 */
object ReglasLimites {

    /** NAC: bloquea la programación mientras el producto siga vencido (sin reposición). */
    fun evaluarNac(item: Item, hoy: LocalDate = LocalDate.now()): ResultadoRegla =
        if (item.estaVencido(hoy)) {
            ResultadoRegla.Bloqueado("${item.nombre} está vencido (${item.vencimiento}). Bloqueado hasta reposición.")
        } else {
            ResultadoRegla.Permitido
        }

    /** Vitamina D: 1 cápsula de §1.1 ya cubre el 2000% VD (límite superior tolerable). */
    fun evaluarVitaminaD(itemsActivosHoy: List<Item>, capsulasProgramadas: Int): ResultadoRegla {
        val fuentesDeVitaminaD = itemsActivosHoy.count { item ->
            item.nutrientes.any { it.nombre.equals("Vitamina D", ignoreCase = true) }
        }
        return when {
            fuentesDeVitaminaD > 1 ->
                ResultadoRegla.Bloqueado("Ya hay una fuente de vitamina D activa. No se permite dar de alta una segunda.")
            capsulasProgramadas > 1 ->
                ResultadoRegla.Advertencia("Más de 1 cápsula/día supera el límite superior tolerable de vitamina D.")
            else -> ResultadoRegla.Permitido
        }
    }

    const val PRESET_VITAMINA_C_REDUCIDA_MG = 1000.0

    /** Vitamina C: advertencia permanente (no bloqueante) por oxalato/renal bajo Tenofovir DF. */
    fun advertenciaVitaminaC(): ResultadoRegla = ResultadoRegla.Advertencia(
        "2 g/día está en el límite superior tolerable. El ácido ascórbico en dosis altas " +
            "aumenta la excreción de oxalato, relevante con Tenofovir DF (perfil de toxicidad renal " +
            "conocido). Considerá el preset de dosis reducida (1 g) y consultar función renal con el médico."
    )

    /** Nicotinamida: 2 cápsulas/día (600 mg) requieren confirmación explícita del usuario. */
    fun evaluarNicotinamida(capsulasProgramadas: Int, confirmacionExplicita: Boolean): ResultadoRegla =
        if (capsulasProgramadas >= 2 && !confirmacionExplicita) {
            ResultadoRegla.Bloqueado("300 mg x2 de nicotinamida supera ampliamente la IDR. Requiere confirmación explícita.")
        } else {
            ResultadoRegla.Permitido
        }

    /** Extracto de té verde: evitar horario nocturno; advertir monitoreo hepático siempre. */
    fun evaluarTeVerde(condicion: Condicion): ResultadoRegla =
        if (condicion == Condicion.NOCTURNO) {
            ResultadoRegla.Advertencia(
                "Contiene extracto de té verde (cafeína): evitar tomar de noche. Casos reportados de " +
                    "hepatotoxicidad idiosincrática — monitoreo hepático sugerido bajo TAR crónico."
            )
        } else {
            ResultadoRegla.Advertencia(
                "Casos reportados de hepatotoxicidad idiosincrática con extracto de té verde concentrado — " +
                    "monitoreo hepático sugerido bajo TAR crónico."
            )
        }

    const val ADVERTENCIA_PRIMERA_ACTIVACION_ARGININA =
        "La L-arginina es vasodilatadora. Antes de activarla por primera vez, controlá tu presión arterial."

    /** L-Arginina: gate condicional a entreno, inactiva hasta activación manual del usuario. */
    fun evaluarLArginina(modoEntrenoActivo: Boolean): ResultadoRegla =
        if (!modoEntrenoActivo) {
            ResultadoRegla.Bloqueado("Ítem inactivo por defecto. Activá \"modo entreno\" para habilitarlo.")
        } else {
            ResultadoRegla.Permitido
        }
}
