package com.nawemedia.adherencia.domain

import java.time.LocalDate

data class Nutriente(
    val nombre: String,
    val cantidad: Double,
    val unidad: String,
    val fuente: FuenteDato,
    val esCationPolivalente: Boolean = false
)

data class Item(
    val id: String,
    val nombre: String,
    val marca: String,
    val tipo: TipoItem,
    val formato: String,
    val estado: EstadoItem,
    val flags: Set<ItemFlag>,
    val nutrientes: List<Nutriente> = emptyList(),
    /** Último día válido según etiqueta (inclusive). Null = sin vencimiento declarado. */
    val vencimiento: LocalDate? = null,
    val notaEstado: String? = null
) {
    fun estaVencido(hoy: LocalDate = LocalDate.now()): Boolean =
        vencimiento != null && hoy.isAfter(vencimiento)

    fun magnesioElementalMg(): Double =
        nutrientes.filter { it.esCationPolivalente }.sumOf { it.cantidad }
}
