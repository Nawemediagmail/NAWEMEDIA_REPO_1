package com.nawemedia.adherencia.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CronogramaSeedTest {

    @Test
    fun `no hay ids de programacion duplicados`() {
        val ids = CRONOGRAMA_SEED.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `todo itemId del cronograma existe en el inventario`() {
        val idsInventario = INVENTARIO.map { it.id }.toSet()
        CRONOGRAMA_SEED.forEach { assertTrue(it.itemId in idsInventario) }
    }

    @Test
    fun `el TAR tiene exactamente dos programaciones ancla, mivuten y zevuvir`() {
        val anclas = CRONOGRAMA_SEED.filter { it.esAncla }
        assertEquals(2, anclas.size)
        assertTrue(anclas.any { it.itemId == MIVUTEN.id })
        assertTrue(anclas.any { it.itemId == ZEVUVIR.id })
    }

    @Test
    fun `el grupo excluyente de magnesio nocturno tiene exactamente 2 opciones`() {
        val grupo = CRONOGRAMA_SEED.filter { it.grupoExcluyente == "magnesio_nocturno" }
        assertEquals(2, grupo.size)
    }
}
