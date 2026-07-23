package com.nawemedia.adherencia.domain

sealed class ResultadoRegla {
    data object Permitido : ResultadoRegla()
    data class Advertencia(val mensaje: String) : ResultadoRegla()
    data class Bloqueado(val motivo: String) : ResultadoRegla()
}
