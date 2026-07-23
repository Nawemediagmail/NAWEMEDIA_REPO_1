package com.nawemedia.adherencia.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ContadorMagnesioTest {

    @Test
    fun `gate del PRD - K2D3 mas citrato suman 174 mg`() {
        val resultado = calcularMagnesioElementalTotal(listOf(FYNUTRITION_K2_D3, CITRATO_MAGNESIO))
        assertEquals(174.0, resultado.totalMg, 0.001)
        assertFalse(resultado.excedeLimite)
    }

    @Test
    fun `las 3 fuentes de magnesio del inventario suman 249 mg y no superan el limite`() {
        val resultado = calcularMagnesioElementalTotal(
            listOf(FYNUTRITION_K2_D3, CITRATO_MAGNESIO, TREONATO_MAGNESIO)
        )
        assertEquals(249.0, resultado.totalMg, 0.001)
        assertFalse(resultado.excedeLimite)
    }

    @Test
    fun `superar 350 mg dispara la alerta de limite`() {
        val itemSintetico = Item(
            id = "test_mg_alto",
            nombre = "Fuente sintética de prueba",
            marca = "N/A",
            tipo = TipoItem.SUPLEMENTO,
            formato = "N/A",
            estado = EstadoItem.ACTIVO,
            flags = setOf(ItemFlag.CONTIENE_MAGNESIO),
            nutrientes = listOf(Nutriente("Magnesio", 400.0, "mg", FuenteDato.USUARIO, esCationPolivalente = true))
        )
        val resultado = calcularMagnesioElementalTotal(listOf(itemSintetico))
        assertTrue(resultado.excedeLimite)
    }

    @Test
    fun `items sin magnesio no aportan al contador`() {
        val resultado = calcularMagnesioElementalTotal(listOf(VITAMINA_C, NAD_RESVERATROL))
        assertEquals(0.0, resultado.totalMg, 0.001)
    }
}
