package com.nawemedia.adherencia.domain

enum class TipoItem { MEDICACION, SUPLEMENTO }

enum class FuenteDato { ETIQUETA, INFERENCIA, USUARIO }

enum class EstadoItem { ACTIVO, INACTIVO, VENCIDO, EXCLUIDO }

enum class Condicion { AYUNAS, CON_COMIDA, CON_GRASA, PRE_ENTRENO, NOCTURNO }

/**
 * Categoría visual (Fase 3, §9 del PRD lo llama "identidad" del ítem en la ficha).
 * Es independiente de ItemFlag: un ítem puede tener flags de riesgo (p. ej.
 * CONTIENE_MAGNESIO) y aun así categorizarse por su identidad principal de etiqueta
 * (p. ej. FYNUTRITION K2+D3 categoriza como LIPOSOLUBLES, no como MAGNESIO).
 * OTROS es el default para ítems sintéticos/de test que no necesitan categorizarse.
 */
enum class Categoria {
    MEDICACION_TAR, MAGNESIO, LIPOSOLUBLES, VITAMINA_C,
    AMINOACIDOS, ADAPTOGENOS, ANTIOXIDANTES, EXCLUIDO, OTROS
}

/**
 * Flags tal como están transcriptos en §1 del PRD, por ítem de inventario.
 * No se colapsan variantes (p. ej. CONTIENE_CAFEINA_TE_VERDE) para no perder
 * la granularidad con la que están documentadas en el inventario original.
 */
enum class ItemFlag {
    CONTIENE_MAGNESIO,
    QUELANTE_CATIONICO,
    LIPOSOLUBLE,
    INTERACCION_ANTICOAGULANTES,
    AYUNAS,
    VASODILATADOR,
    REQUIERE_ESTOMAGO_VACIO,
    CONDICIONAL_A_ENTRENAMIENTO,
    VASODILATADOR_INDIRECTO,
    MULTIDOSIS_DIARIA,
    DOSIS_NO_ESPECIFICADA,
    VENCIDO,
    DATOS_INCOMPLETOS,
    LIQUIDO_SUBLINGUAL_U_ORAL,
    CONTIENE_CAFEINA_TE_VERDE,
    LIMITE_SUPERIOR_NIACINA,
    HEPATOTOXICIDAD_TE_VERDE,
    LIMITE_SUPERIOR,
    RIESGO_OXALATO_RENAL,
    VALOR_ELEMENTAL_ESTIMADO,
    EXCLUIDO
}
