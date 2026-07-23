package com.nawemedia.adherencia.domain

import java.time.LocalTime

/**
 * §7 (adaptado, sin Room todavía). Un horario programado para un ítem.
 *
 * [grupoExcluyente] modela el selector excluyente del magnesio nocturno (§4, nota):
 * "elegir uno de los dos por día, no ambos a dosis plena". Dos Programacion con el
 * mismo grupo representan opciones alternativas del mismo slot horario; la UI decide
 * cuál está activa.
 */
data class Programacion(
    val id: String,
    val itemId: String,
    val hora: LocalTime,
    val condicion: Condicion,
    val esAncla: Boolean = false,
    val grupoExcluyente: String? = null
)
