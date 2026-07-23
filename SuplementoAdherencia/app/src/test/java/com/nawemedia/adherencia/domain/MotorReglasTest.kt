package com.nawemedia.adherencia.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class MotorReglasTest {

    private fun contexto(
        hoy: LocalDate = LocalDate.of(2026, 7, 23),
        modoEntreno: Boolean = false
    ) = ContextoEvaluacion(
        tar = TAR_ANCLA_DEFAULT,
        itemsActivosHoy = INVENTARIO.filter { it.estado != EstadoItem.EXCLUIDO },
        hoy = hoy,
        modoEntrenoActivo = modoEntreno
    )

    @Test
    fun `NAC vencido queda bloqueado antes de evaluar cualquier otra regla`() {
        val programacion = Programacion("p_nac", NAC.id, LocalTime.of(10, 0), Condicion.AYUNAS)
        val resultado = MotorReglas.evaluar(NAC, programacion, contexto())
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `citrato de magnesio a TAR mas 1h queda bloqueado por quelacion`() {
        val programacion = Programacion("p_mg", CITRATO_MAGNESIO.id, LocalTime.of(11, 30), Condicion.NOCTURNO)
        val resultado = MotorReglas.evaluar(CITRATO_MAGNESIO, programacion, contexto())
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `citrato de magnesio a las 22-00 con TAR 10-30 esta permitido`() {
        val programacion = CRONOGRAMA_SEED.first { it.id == "p_mg_nocturno_citrato" }
        val resultado = MotorReglas.evaluar(CITRATO_MAGNESIO, programacion, contexto())
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `vitamina C siempre devuelve advertencia, nunca bloqueo`() {
        val programacion = CRONOGRAMA_SEED.first { it.id == "p_vitc" }
        val resultado = MotorReglas.evaluar(VITAMINA_C, programacion, contexto())
        assertTrue(resultado is ResultadoRegla.Advertencia)
    }

    @Test
    fun `l-arginina bloqueada mientras el modo entreno este inactivo`() {
        val programacion = Programacion("p_arg", L_ARGININA.id, LocalTime.of(18, 0), Condicion.PRE_ENTRENO)
        val resultado = MotorReglas.evaluar(L_ARGININA, programacion, contexto(modoEntreno = false))
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }

    @Test
    fun `l-arginina permitida con modo entreno activo y fuera de ventana de quelacion`() {
        val programacion = Programacion("p_arg", L_ARGININA.id, LocalTime.of(18, 0), Condicion.PRE_ENTRENO)
        val resultado = MotorReglas.evaluar(L_ARGININA, programacion, contexto(modoEntreno = true))
        assertEquals(ResultadoRegla.Permitido, resultado)
    }

    @Test
    fun `item excluido siempre bloqueado sin importar la hora`() {
        val programacion = Programacion("p_bhb", BHB_MAGNESIO_EXCLUIDO.id, LocalTime.of(9, 0), Condicion.CON_COMIDA)
        val resultado = MotorReglas.evaluar(BHB_MAGNESIO_EXCLUIDO, programacion, contexto())
        assertTrue(resultado is ResultadoRegla.Bloqueado)
    }
}
