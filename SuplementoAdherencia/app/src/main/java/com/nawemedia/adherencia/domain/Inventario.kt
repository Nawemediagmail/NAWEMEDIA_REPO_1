package com.nawemedia.adherencia.domain

import java.time.LocalDate
import java.time.LocalTime

/**
 * Inventario transcripto de §1 del PRD. Todo valor sin marca [INFERENCIA] explícita
 * viene de la etiqueta (FuenteDato.ETIQUETA). No se redondea ni se completa con
 * conocimiento general — regla dura del documento (§0).
 */

// ---- §2 Medicación TAR (ancla del sistema) ----

val MIVUTEN = Item(
    id = "mivuten",
    nombre = "Mivuten (Lamivudina 300 mg + Tenofovir Disoproxil Fumarato 300 mg)",
    marca = "Richmond",
    tipo = TipoItem.MEDICACION,
    formato = "1 comprimido recubierto",
    estado = EstadoItem.ACTIVO,
    flags = emptySet()
)

val ZEVUVIR = Item(
    id = "zevuvir",
    nombre = "Zevuvir (Dolutegravir 50 mg)",
    marca = "Richmond",
    tipo = TipoItem.MEDICACION,
    formato = "1 comprimido recubierto",
    estado = EstadoItem.ACTIVO,
    flags = emptySet()
)

/** Ancla por defecto del seed (§4): 10:30. A confirmar con el médico (§12.1). */
val TAR_ANCLA_DEFAULT: LocalTime = LocalTime.of(10, 30)

// ---- §1 Inventario de suplementos ----

val FYNUTRITION_K2_D3 = Item(
    id = "fynutrition_k2_d3",
    nombre = "Vitamina K2 + D3 (con Citrato de Magnesio y Colágeno Tipo II)",
    marca = "FYNUTRITION",
    tipo = TipoItem.SUPLEMENTO,
    formato = "60 cápsulas, 1 cápsula/porción",
    estado = EstadoItem.ACTIVO,
    flags = setOf(ItemFlag.CONTIENE_MAGNESIO, ItemFlag.LIPOSOLUBLE, ItemFlag.INTERACCION_ANTICOAGULANTES),
    nutrientes = listOf(
        Nutriente("Vitamina D", 100.0, "µg", FuenteDato.ETIQUETA),
        Nutriente("Vitamina K2 (Menaquinona-7)", 100.0, "µg", FuenteDato.ETIQUETA),
        Nutriente("Magnesio", 39.0, "mg", FuenteDato.ETIQUETA, esCationPolivalente = true),
        Nutriente("Colágeno Tipo II", 40.0, "mg", FuenteDato.ETIQUETA)
    )
)

val CITRATO_MAGNESIO = Item(
    id = "citrato_magnesio",
    nombre = "Citrato de Magnesio",
    marca = "NF SUPPLEMENTS",
    tipo = TipoItem.SUPLEMENTO,
    formato = "500 g polvo, porción 1,2 g",
    estado = EstadoItem.ACTIVO,
    flags = setOf(ItemFlag.CONTIENE_MAGNESIO, ItemFlag.QUELANTE_CATIONICO),
    nutrientes = listOf(
        Nutriente("Magnesio", 135.0, "mg", FuenteDato.ETIQUETA, esCationPolivalente = true)
    )
)

val TREONATO_MAGNESIO = Item(
    id = "treonato_magnesio",
    nombre = "Treonato de Magnesio 1000 mg",
    marca = "ROWAN GROVE",
    tipo = TipoItem.SUPLEMENTO,
    formato = "90 comprimidos, 1 comprimido/porción",
    estado = EstadoItem.ACTIVO,
    flags = setOf(ItemFlag.CONTIENE_MAGNESIO, ItemFlag.QUELANTE_CATIONICO, ItemFlag.VALOR_ELEMENTAL_ESTIMADO),
    nutrientes = listOf(
        Nutriente("Magnesio Treonato", 1000.0, "mg", FuenteDato.ETIQUETA),
        // [INFERENCIA] ≈70-80 mg elemental, no desglosado en etiqueta (§1.3).
        // Se usa el punto medio (75), igual que el propio cálculo de ejemplo del PRD en §3.3.
        Nutriente("Magnesio (elemental, estimado)", 75.0, "mg", FuenteDato.INFERENCIA, esCationPolivalente = true)
    ),
    vencimiento = LocalDate.of(2027, 12, 31)
)

val VITAMINA_C = Item(
    id = "vitamina_c",
    nombre = "Vitamina C (Ácido Ascórbico 100% puro)",
    marca = "NF SUPPLEMENTS",
    tipo = TipoItem.SUPLEMENTO,
    formato = "500 g polvo, dosis 2 g/día",
    estado = EstadoItem.ACTIVO,
    flags = setOf(ItemFlag.LIMITE_SUPERIOR, ItemFlag.RIESGO_OXALATO_RENAL),
    nutrientes = listOf(
        Nutriente("Ácido ascórbico", 2000.0, "mg", FuenteDato.ETIQUETA)
    )
)

val L_ARGININA = Item(
    id = "l_arginina",
    nombre = "L-Arginina de Óxido Nítrico",
    marca = "NF SUPPLEMENTS",
    tipo = TipoItem.SUPLEMENTO,
    formato = "150 g polvo, porción 3,8 g",
    estado = EstadoItem.INACTIVO,
    flags = setOf(ItemFlag.VASODILATADOR, ItemFlag.REQUIERE_ESTOMAGO_VACIO, ItemFlag.CONDICIONAL_A_ENTRENAMIENTO),
    nutrientes = listOf(
        Nutriente("L-Arginina", 3800.0, "mg", FuenteDato.ETIQUETA)
    )
)

val ORNITINA = Item(
    id = "ornitina",
    nombre = "Ornitina",
    marca = "NITRO FUEL SUPPLEMENTS",
    tipo = TipoItem.SUPLEMENTO,
    formato = "100 g polvo, 500 mg 2-3 veces/día",
    estado = EstadoItem.INACTIVO,
    flags = setOf(ItemFlag.VASODILATADOR_INDIRECTO, ItemFlag.MULTIDOSIS_DIARIA),
    nutrientes = listOf(
        Nutriente("Ornitina", 500.0, "mg", FuenteDato.ETIQUETA)
    )
)

val NAC = Item(
    id = "nac",
    nombre = "N-Acetil-L-Cisteína (NAC) Pura",
    marca = "BREAKING LAB",
    tipo = TipoItem.SUPLEMENTO,
    formato = "100 g polvo",
    // El bloqueo real lo decide estaVencido(), no este campo — ver ReglasLimites.evaluarNac().
    estado = EstadoItem.ACTIVO,
    flags = setOf(ItemFlag.DOSIS_NO_ESPECIFICADA, ItemFlag.VENCIDO, ItemFlag.AYUNAS),
    // Vencimiento "05/2026" en etiqueta = mes/año sin día; se asume último día del mes declarado.
    vencimiento = LocalDate.of(2026, 5, 31),
    notaEstado = "Vencido — bloqueado hasta reposición (§1.7)"
)

val MELENA_DE_LEON = Item(
    id = "melena_de_leon",
    nombre = "Melena de León (Hericium erinaceus)",
    marca = "ALMA FUNGI",
    tipo = TipoItem.SUPLEMENTO,
    formato = "Extracto concentrado, 60 ml",
    estado = EstadoItem.ACTIVO,
    flags = setOf(ItemFlag.DATOS_INCOMPLETOS, ItemFlag.AYUNAS, ItemFlag.LIQUIDO_SUBLINGUAL_U_ORAL),
    notaEstado = "Datos de etiqueta incompletos — falta fotografiar contraetiqueta (§1.8)"
)

val NAD_RESVERATROL = Item(
    id = "nad_resveratrol",
    nombre = "NAD+ Resveratrol",
    marca = "DNA BOOST",
    tipo = TipoItem.SUPLEMENTO,
    formato = "60 cápsulas, 1-2 cápsulas/porción",
    estado = EstadoItem.ACTIVO,
    flags = setOf(ItemFlag.CONTIENE_CAFEINA_TE_VERDE, ItemFlag.LIMITE_SUPERIOR_NIACINA, ItemFlag.HEPATOTOXICIDAD_TE_VERDE),
    nutrientes = listOf(
        Nutriente("Vitamina B3 (Nicotinamida)", 300.0, "mg", FuenteDato.ETIQUETA),
        Nutriente("Extracto de té verde", 100.0, "mg", FuenteDato.ETIQUETA),
        Nutriente("Resveratrol", 55.0, "mg", FuenteDato.ETIQUETA),
        Nutriente("Vitamina B12", 5.0, "µg", FuenteDato.ETIQUETA)
    ),
    vencimiento = LocalDate.of(2028, 4, 8)
)

val BHB_MAGNESIO_EXCLUIDO = Item(
    id = "bhb_magnesio",
    nombre = "BHB Magnesio (Hidroxibutirato)",
    marca = "NATURAL WHEY",
    tipo = TipoItem.SUPLEMENTO,
    formato = "100 g",
    estado = EstadoItem.EXCLUIDO,
    flags = setOf(ItemFlag.EXCLUIDO),
    vencimiento = LocalDate.of(2028, 12, 31),
    notaEstado = "EXCLUIDO: etiqueta declara \"uso industrial exclusivo\", sin garantía de grado alimentario (§1.10)"
)

val INVENTARIO: List<Item> = listOf(
    MIVUTEN, ZEVUVIR,
    FYNUTRITION_K2_D3, CITRATO_MAGNESIO, TREONATO_MAGNESIO,
    VITAMINA_C, L_ARGININA, ORNITINA, NAC, MELENA_DE_LEON,
    NAD_RESVERATROL, BHB_MAGNESIO_EXCLUIDO
)
