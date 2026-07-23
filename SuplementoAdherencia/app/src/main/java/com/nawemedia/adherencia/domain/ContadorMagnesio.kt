package com.nawemedia.adherencia.domain

/** §3.3 — Límite superior para magnesio suplementario total (todas las fuentes). */
const val LIMITE_MAGNESIO_ELEMENTAL_MG = 350.0

data class ResultadoContadorMagnesio(
    val totalMg: Double,
    val excedeLimite: Boolean,
    val fuentes: List<Pair<String, Double>>
)

/** §3.3 — Suma en vivo del magnesio elemental de los ítems activos/seleccionados del día. */
fun calcularMagnesioElementalTotal(items: List<Item>): ResultadoContadorMagnesio {
    val fuentes = items
        .filter { it.magnesioElementalMg() > 0.0 }
        .map { it.nombre to it.magnesioElementalMg() }
    val total = fuentes.sumOf { it.second }
    return ResultadoContadorMagnesio(
        totalMg = total,
        excedeLimite = total > LIMITE_MAGNESIO_ELEMENTAL_MG,
        fuentes = fuentes
    )
}
