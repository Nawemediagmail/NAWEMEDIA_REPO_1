package com.nawemedia.adherencia.domain

import java.time.LocalDate
import java.time.LocalTime

data class ContextoEvaluacion(
    val tar: LocalTime,
    val itemsActivosHoy: List<Item>,
    val hoy: LocalDate = LocalDate.now(),
    val capsulasVitaminaD: Int = 1,
    val capsulasNicotinamida: Int = 1,
    val confirmacionNicotinamida: Boolean = false,
    val modoEntrenoActivo: Boolean = false
)

/**
 * Combina todas las reglas de §3 aplicables a un ítem programado, en orden de
 * severidad: primero los bloqueos "duros" de estado (excluido/vencido/gate de
 * entreno), después §3.1 (quelación), y por último las advertencias de §3.3.
 */
object MotorReglas {

    fun evaluar(item: Item, programacion: Programacion, contexto: ContextoEvaluacion): ResultadoRegla {
        if (item.estado == EstadoItem.EXCLUIDO) {
            return ResultadoRegla.Bloqueado(item.notaEstado ?: "Ítem excluido del protocolo.")
        }

        if (item.id == NAC.id) {
            val resultado = ReglasLimites.evaluarNac(item, contexto.hoy)
            if (resultado is ResultadoRegla.Bloqueado) return resultado
        } else if (item.estaVencido(contexto.hoy)) {
            return ResultadoRegla.Bloqueado("${item.nombre} está vencido (${item.vencimiento}).")
        }

        if (item.id == L_ARGININA.id) {
            val resultado = ReglasLimites.evaluarLArginina(contexto.modoEntrenoActivo)
            if (resultado is ResultadoRegla.Bloqueado) return resultado
        }

        // §3.1 — regla crítica de quelación.
        val quelacion = ReglaQuelacion.evaluar(item, programacion.hora, contexto.tar)
        if (quelacion is ResultadoRegla.Bloqueado) return quelacion

        // §3.3 — resto de advertencias/límites, por ítem.
        if (item.id == FYNUTRITION_K2_D3.id) {
            val resultado = ReglasLimites.evaluarVitaminaD(contexto.itemsActivosHoy, contexto.capsulasVitaminaD)
            if (resultado !is ResultadoRegla.Permitido) return resultado
        }
        if (item.id == VITAMINA_C.id) {
            return ReglasLimites.advertenciaVitaminaC()
        }
        if (item.id == NAD_RESVERATROL.id) {
            val nicotinamida = ReglasLimites.evaluarNicotinamida(contexto.capsulasNicotinamida, contexto.confirmacionNicotinamida)
            if (nicotinamida !is ResultadoRegla.Permitido) return nicotinamida
            return ReglasLimites.evaluarTeVerde(programacion.condicion)
        }

        return ResultadoRegla.Permitido
    }
}
