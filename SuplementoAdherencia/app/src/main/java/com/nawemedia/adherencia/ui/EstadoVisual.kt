package com.nawemedia.adherencia.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.nawemedia.adherencia.domain.ResultadoRegla

/** Traduce un ResultadoRegla del dominio a ícono + texto + color para la UI. */
sealed class EstadoVisual(val icono: ImageVector, val texto: String) {
    data object Permitido : EstadoVisual(Icons.Filled.CheckCircle, "Permitido")
    data class Advertencia(val mensaje: String) : EstadoVisual(Icons.Filled.WarningAmber, mensaje)
    data class Bloqueado(val motivo: String) : EstadoVisual(Icons.Filled.Block, motivo)
}

fun ResultadoRegla.aEstadoVisual(): EstadoVisual = when (this) {
    is ResultadoRegla.Permitido -> EstadoVisual.Permitido
    is ResultadoRegla.Advertencia -> EstadoVisual.Advertencia(mensaje)
    is ResultadoRegla.Bloqueado -> EstadoVisual.Bloqueado(motivo)
}

@Composable
fun colorDeEstado(estado: EstadoVisual): Color = when (estado) {
    is EstadoVisual.Permitido -> AppTheme.colors.success
    is EstadoVisual.Advertencia -> AppTheme.colors.warning
    is EstadoVisual.Bloqueado -> MaterialTheme.colorScheme.error
}
