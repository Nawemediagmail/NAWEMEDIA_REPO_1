package com.nawemedia.adherencia.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF00658F),
    onPrimary = Color.White,
    secondary = Color(0xFF4F616E),
    error = Color(0xFFBA1A1A),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF84CFFF),
    onPrimary = Color(0xFF00344C),
    secondary = Color(0xFFB7C9D8),
    error = Color(0xFFFFB4AB),
)

/**
 * Roles de color semánticos que Material3 no trae por defecto (solo primary/
 * secondary/tertiary/error). Se usan exclusivamente para el ESTADO de una regla
 * (permitido/advertencia), nunca para identidad de categoría — ver DesignTokens.kt.
 */
data class ColoresExtendidos(
    val success: Color,
    val onSuccess: Color,
    val warning: Color,
    val onWarning: Color
)

private val ColoresExtendidosLight = ColoresExtendidos(
    success = Color(0xFF2E7D32),
    onSuccess = Color.White,
    warning = Color(0xFFB26A00),
    onWarning = Color.White
)

private val ColoresExtendidosDark = ColoresExtendidos(
    success = Color(0xFF81C995),
    onSuccess = Color(0xFF00390F),
    warning = Color(0xFFFFB951),
    onWarning = Color(0xFF4A2800)
)

private val LocalColoresExtendidos = staticCompositionLocalOf { ColoresExtendidosLight }

object AppTheme {
    val colors: ColoresExtendidos
        @Composable get() = LocalColoresExtendidos.current
}

@Composable
fun AdherenciaTheme(content: @Composable () -> Unit) {
    val oscuro = isSystemInDarkTheme()
    val colors = if (oscuro) DarkColors else LightColors
    val extendidos = if (oscuro) ColoresExtendidosDark else ColoresExtendidosLight
    CompositionLocalProvider(LocalColoresExtendidos provides extendidos) {
        MaterialTheme(colorScheme = colors, content = content)
    }
}
