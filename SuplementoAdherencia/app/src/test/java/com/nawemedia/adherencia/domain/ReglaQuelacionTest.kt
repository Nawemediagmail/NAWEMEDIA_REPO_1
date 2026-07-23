package com.nawemedia.adherencia.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime

class ReglaQuelacionTest {

    private val tar = LocalTime.of(10, 30)

    @Test
    fun `gate del PRD - magnesio a TAR mas 1h devuelve bloqueado`() {
        val resultado = ReglaQuelacion.evaluar(CITRATO_MAGNESIO, LocalTime.of(11, 30), tar)
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `borde inclusive - TAR mas 6h exacto esta permitido`() {
        val resultado = ReglaQuelacion.evaluar(CITRATO_MAGNESIO, LocalTime.of(16, 30), tar)
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `un minuto antes del borde de 6h todavia esta bloqueado`() {
        val resultado = ReglaQuelacion.evaluar(CITRATO_MAGNESIO, LocalTime.of(16, 29), tar)
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `borde inclusive - TAR menos 2h exacto esta permitido`() {
        val resultado = ReglaQuelacion.evaluar(CITRATO_MAGNESIO, LocalTime.of(8, 30), tar)
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `magnesio nocturno 22-00 con TAR 10-30 esta permitido, coincide con el seed del PRD`() {
        // §4 del PRD: magnesio nocturno a las 22:00 con TAR 10:30 = "TAR + 11,5h" marcado con check.
        val resultado = ReglaQuelacion.evaluar(CITRATO_MAGNESIO, LocalTime.of(22, 0), tar)
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `K2D3 a las 16-30 con TAR 10-30 esta permitido, coincide con el seed del PRD`() {
        val resultado = ReglaQuelacion.evaluar(FYNUTRITION_K2_D3, LocalTime.of(16, 30), tar)
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `treonato justo despues del TAR esta bloqueado`() {
        val resultado = ReglaQuelacion.evaluar(TREONATO_MAGNESIO, LocalTime.of(10, 35), tar)
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `item sin flag CONTIENE_MAGNESIO nunca se bloquea por quelacion`() {
        val resultado = ReglaQuelacion.evaluar(VITAMINA_C, LocalTime.of(10, 31), tar)
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `proxima ventana segura durante el bloqueo devuelve TAR mas 6h`() {
        val resultado = ReglaQuelacion.proximaVentanaSegura(LocalTime.of(11, 30), tar)
        assertEquals(LocalTime.of(16, 30), resultado)
    }

    @Test
    fun `proxima ventana segura ya en ventana permitida devuelve la hora actual`() {
        val ahora = LocalTime.of(22, 0)
        val resultado = ReglaQuelacion.proximaVentanaSegura(ahora, tar)
        assertEquals(ahora, resultado)
    }
}
