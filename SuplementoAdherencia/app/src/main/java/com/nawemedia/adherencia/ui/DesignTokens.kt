package com.nawemedia.adherencia.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.nawemedia.adherencia.domain.Categoria

/**
 * Sistema de color por categoría — identidad visual del ítem, para escaneo rápido.
 *
 * A propósito en tonos muted/pastel: los colores saturados de Material3
 * (error/success/warning, ver Theme.kt y EstadoVisual.kt) quedan reservados
 * para el ESTADO de la regla (permitido/advertencia/bloqueado). Si la categoría
 * usara colores igual de saturados, competiría visualmente con la seguridad,
 * que es lo que más importa en esta app. Cada categoría además lleva un ícono
 * propio: el color nunca es el único indicador (accesibilidad / daltonismo).
 */
data class CategoriaEstilo(
    val color: Color,
    val colorOscuro: Color,
    val icono: ImageVector,
    val etiqueta: String
)

private val ESTILOS_CATEGORIA: Map<Categoria, CategoriaEstilo> = mapOf(
    Categoria.MEDICACION_TAR to CategoriaEstilo(
        Color(0xFF3D5A80), Color(0xFF8AB4E8), Icons.Filled.MedicalServices, "TAR"
    ),
    Categoria.MAGNESIO to CategoriaEstilo(
        Color(0xFF7E6BC4), Color(0xFFC3B8F0), Icons.Filled.Bolt, "Magnesio"
    ),
    Categoria.LIPOSOLUBLES to CategoriaEstilo(
        Color(0xFFB8952E), Color(0xFFE6C863), Icons.Filled.WbSunny, "Liposolubles"
    ),
    Categoria.VITAMINA_C to CategoriaEstilo(
        Color(0xFFC96E2E), Color(0xFFF0B380), Icons.Filled.LocalFlorist, "Vitamina C"
    ),
    Categoria.AMINOACIDOS to CategoriaEstilo(
        Color(0xFF3E8A7D), Color(0xFF8FCFC3), Icons.Filled.FitnessCenter, "Aminoácidos"
    ),
    Categoria.ADAPTOGENOS to CategoriaEstilo(
        Color(0xFF5C7A4C), Color(0xFFA8CC96), Icons.Filled.Grass, "Adaptógenos"
    ),
    Categoria.ANTIOXIDANTES to CategoriaEstilo(
        Color(0xFFA14E63), Color(0xFFE0A0B0), Icons.Filled.Spa, "Antioxidantes"
    ),
    Categoria.EXCLUIDO to CategoriaEstilo(
        Color(0xFF757575), Color(0xFFAAAAAA), Icons.Filled.Block, "Excluido"
    ),
    Categoria.OTROS to CategoriaEstilo(
        Color(0xFF757575), Color(0xFFAAAAAA), Icons.Filled.Block, "Otros"
    )
)

fun estiloDeCategoria(categoria: Categoria): CategoriaEstilo = ESTILOS_CATEGORIA.getValue(categoria)
