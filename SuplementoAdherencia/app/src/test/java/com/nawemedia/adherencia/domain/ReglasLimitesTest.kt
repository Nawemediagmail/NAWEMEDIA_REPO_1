package com.nawemedia.adherencia.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ReglasLimitesTest {

    @Test
    fun `NAC vencido en mayo 2026 esta bloqueado si hoy es posterior`() {
        val resultado = ReglasLimites.evaluarNac(NAC, hoy = LocalDate.of(2026, 7, 23))
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `NAC no esta bloqueado antes de su vencimiento`() {
        val resultado = ReglasLimites.evaluarNac(NAC, hoy = LocalDate.of(2026, 4, 1))
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `segunda fuente de vitamina D queda bloqueada`() {
        val segundaFuente = FYNUTRITION_K2_D3.copy(id = "otra_fuente_d")
        val resultado = ReglasLimites.evaluarVitaminaD(
            itemsActivosHoy = listOf(FYNUTRITION_K2_D3, segundaFuente),
            capsulasProgramadas = 1
        )
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `mas de 1 capsula por dia de vitamina D genera advertencia`() {
        val resultado = ReglasLimites.evaluarVitaminaD(
            itemsActivosHoy = listOf(FYNUTRITION_K2_D3),
            capsulasProgramadas = 2
        )
        assertTrue(resultado is ResultadoRegla.Advertencia)
    }

    @Test
    fun `2 capsulas de nicotinamida sin confirmacion quedan bloqueadas`() {
        val resultado = ReglasLimites.evaluarNicotinamida(capsulasProgramadas = 2, confirmacionExplicita = false)
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `2 capsulas de nicotinamida con confirmacion explicita quedan permitidas`() {
        val resultado = ReglasLimites.evaluarNicotinamida(capsulasProgramadas = 2, confirmacionExplicita = true)
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `te verde nocturno genera advertencia especifica de horario`() {
        val resultado = ReglasLimites.evaluarTeVerde(Condicion.NOCTURNO)
        assertTrue(resultado is ResultadoRegla.Advertencia)
    }

    @Test
    fun `l-arginina bloqueada hasta activar modo entreno`() {
        val resultado = ReglasLimites.evaluarLArginina(modoEntrenoActivo = false)
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `l-arginina permitida con modo entreno activo`() {
        val resultado = ReglasLimites.evaluarLArginina(modoEntrenoActivo = true)
        assertEquals(ResultadoRegla.Permitido, resultado)
    }
}
