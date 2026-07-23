package com.nawemedia.adherencia.domain

import java.time.LocalTime

/**
 * §4 — Cronograma default a sembrar. Ancla: TAR 10:30 (a confirmar con médico, §12.1).
 * L-Arginina y Ornitina no se siembran con hora fija: son "Variable", inactivas por
 * defecto hasta que el usuario active manualmente el modo entreno (§3.3, F12).
 */
val CRONOGRAMA_SEED: List<Programacion> = listOf(
    Programacion("p_nac", NAC.id, LocalTime.of(10, 0), Condicion.AYUNAS),
    Programacion("p_melena", MELENA_DE_LEON.id, LocalTime.of(10, 0), Condicion.AYUNAS),
    Programacion("p_tar_mivuten", MIVUTEN.id, TAR_ANCLA_DEFAULT, Condicion.CON_COMIDA, esAncla = true),
    Programacion("p_tar_zevuvir", ZEVUVIR.id, TAR_ANCLA_DEFAULT, Condicion.CON_COMIDA, esAncla = true),
    Programacion("p_vitc", VITAMINA_C.id, TAR_ANCLA_DEFAULT, Condicion.CON_COMIDA),
    Programacion("p_nad", NAD_RESVERATROL.id, TAR_ANCLA_DEFAULT, Condicion.CON_COMIDA),
    Programacion("p_k2d3", FYNUTRITION_K2_D3.id, LocalTime.of(16, 30), Condicion.CON_GRASA),
    // Selector excluyente (§4, nota): uno de los dos, no ambos a dosis plena.
    Programacion("p_mg_nocturno_citrato", CITRATO_MAGNESIO.id, LocalTime.of(22, 0), Condicion.NOCTURNO, grupoExcluyente = "magnesio_nocturno"),
    Programacion("p_mg_nocturno_treonato", TREONATO_MAGNESIO.id, LocalTime.of(22, 0), Condicion.NOCTURNO, grupoExcluyente = "magnesio_nocturno")
)
