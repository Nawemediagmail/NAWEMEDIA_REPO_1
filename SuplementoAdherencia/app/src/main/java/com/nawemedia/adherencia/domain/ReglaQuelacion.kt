package com.nawemedia.adherencia.domain

import java.time.LocalTime

/**
 * §3.1 — REGLA CRÍTICA: Quelación Dolutegravir <-> cationes polivalentes.
 *
 * permitido SI (t_item <= t_TAR - 2h) O (t_item >= t_TAR + 6h)
 * bloqueado en caso contrario
 *
 * Ventana prohibida: (TAR-2h, TAR+6h), abierta en los extremos — los bordes exactos
 * (TAR-2h y TAR+6h) están permitidos según el pseudocódigo del PRD (<= y >=).
 *
 * El peor escenario del sistema: subterapia de dolutegravir por administración conjunta
 * con cationes polivalentes → riesgo de resistencia a la clase INSTI. Por eso este bloqueo
 * es un diálogo bloqueante en la UI, no un aviso descartable (F3, criterio de aceptación).
 */
object ReglaQuelacion {

    private const val MINUTOS_ANTES = 120   // 2h
    private const val MINUTOS_DESPUES = 360 // 6h
    private const val MINUTOS_DIA = 24 * 60

    /** Minutos transcurridos desde [tar] hasta [hora], en sentido horario, en un ciclo de 24h. */
    private fun minutosDesdeAncla(hora: LocalTime, tar: LocalTime): Int {
        val h = hora.hour * 60 + hora.minute
        val t = tar.hour * 60 + tar.minute
        return ((h - t) % MINUTOS_DIA + MINUTOS_DIA) % MINUTOS_DIA
    }

    /** true si [hora] cae fuera de la ventana prohibida alrededor de [tar]. */
    fun permitido(hora: LocalTime, tar: LocalTime): Boolean {
        val diff = minutosDesdeAncla(hora, tar)
        return diff in MINUTOS_DESPUES..(MINUTOS_DIA - MINUTOS_ANTES)
    }

    fun evaluar(item: Item, hora: LocalTime, tar: LocalTime): ResultadoRegla {
        if (ItemFlag.CONTIENE_MAGNESIO !in item.flags) return ResultadoRegla.Permitido

        return if (permitido(hora, tar)) {
            ResultadoRegla.Permitido
        } else {
            ResultadoRegla.Bloqueado(
                "Quelación con dolutegravir: ${item.nombre} debe tomarse hasta 2 h antes " +
                    "o desde 6 h después del TAR ($tar)."
            )
        }
    }

    /**
     * §9.2 — "próxima ventana segura", para el widget persistente del panel expandido.
     * Si [ahora] ya está en ventana permitida, devuelve [ahora] mismo; si no, el
     * próximo horario en que se abre (TAR+6h).
     */
    fun proximaVentanaSegura(ahora: LocalTime, tar: LocalTime): LocalTime =
        if (permitido(ahora, tar)) ahora else tar.plusHours(6)
}
