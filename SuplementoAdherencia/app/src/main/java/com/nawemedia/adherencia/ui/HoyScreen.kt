package com.nawemedia.adherencia.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nawemedia.adherencia.domain.CITRATO_MAGNESIO
import com.nawemedia.adherencia.domain.CRONOGRAMA_SEED
import com.nawemedia.adherencia.domain.Condicion
import com.nawemedia.adherencia.domain.ContextoEvaluacion
import com.nawemedia.adherencia.domain.EstadoItem
import com.nawemedia.adherencia.domain.INVENTARIO
import com.nawemedia.adherencia.domain.Item
import com.nawemedia.adherencia.domain.L_ARGININA
import com.nawemedia.adherencia.domain.LIMITE_MAGNESIO_ELEMENTAL_MG
import com.nawemedia.adherencia.domain.MotorReglas
import com.nawemedia.adherencia.domain.Programacion
import com.nawemedia.adherencia.domain.ReglasLimites
import com.nawemedia.adherencia.domain.ResultadoContadorMagnesio
import com.nawemedia.adherencia.domain.ResultadoRegla
import com.nawemedia.adherencia.domain.TAR_ANCLA_DEFAULT
import com.nawemedia.adherencia.domain.TREONATO_MAGNESIO
import com.nawemedia.adherencia.domain.calcularMagnesioElementalTotal
import com.nawemedia.adherencia.util.ConfirmacionesPrefs
import com.nawemedia.adherencia.util.PreferenciasUsuario
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val inventarioPorId: Map<String, Item> = INVENTARIO.associateBy { it.id }
private val horaFormatter = DateTimeFormatter.ofPattern("HH:mm")
private const val GRUPO_MAGNESIO_NOCTURNO = "magnesio_nocturno"

private data class FilaCronograma(val programacion: Programacion, val item: Item, val resultado: ResultadoRegla)

@Composable
fun HoyScreen() {
    val context = LocalContext.current
    var refresco by remember { mutableIntStateOf(0) }
    val hoy = remember { LocalDate.now() }

    val seleccionMagnesio = remember(refresco) {
        PreferenciasUsuario.seleccionGrupo(context, GRUPO_MAGNESIO_NOCTURNO, "p_mg_nocturno_citrato")
    }
    val modoEntreno = remember(refresco) { PreferenciasUsuario.modoEntrenoActivo(context) }

    val filas = remember(refresco, seleccionMagnesio, modoEntreno) {
        construirCronogramaDeHoy(seleccionMagnesio, modoEntreno, hoy)
    }

    val magnesioTotal = remember(filas) {
        calcularMagnesioElementalTotal(filas.map { it.item })
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ContadorMagnesioCard(magnesioTotal) }

        items(filas, key = { it.programacion.id }) { fila ->
            val confirmado = ConfirmacionesPrefs.estaConfirmado(context, fila.programacion.id, hoy)
            ItemDelDiaCard(
                fila = fila,
                confirmado = confirmado,
                onConfirmar = {
                    ConfirmacionesPrefs.confirmar(context, fila.programacion.id, hoy)
                    refresco++
                },
                onDesconfirmar = {
                    ConfirmacionesPrefs.quitarConfirmacion(context, fila.programacion.id, hoy)
                    refresco++
                },
                onElegirGrupoMagnesio = if (fila.programacion.grupoExcluyente == GRUPO_MAGNESIO_NOCTURNO) {
                    { nuevoId ->
                        PreferenciasUsuario.elegirGrupo(context, GRUPO_MAGNESIO_NOCTURNO, nuevoId)
                        refresco++
                    }
                } else null,
                onActivarModoEntreno = if (fila.item.id == L_ARGININA.id && !modoEntreno) {
                    {
                        PreferenciasUsuario.setModoEntrenoActivo(context, true)
                        refresco++
                    }
                } else null
            )
        }

        item {
            Text(
                "Herramienta de organización personal. No sustituye indicación médica.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        }
    }
}

private fun construirCronogramaDeHoy(
    seleccionMagnesioId: String,
    modoEntreno: Boolean,
    hoy: LocalDate
): List<FilaCronograma> {
    val contexto = ContextoEvaluacion(
        tar = TAR_ANCLA_DEFAULT,
        itemsActivosHoy = INVENTARIO.filter { it.estado != EstadoItem.EXCLUIDO },
        hoy = hoy,
        modoEntrenoActivo = modoEntreno
    )
    return CRONOGRAMA_SEED
        .filter { it.grupoExcluyente != GRUPO_MAGNESIO_NOCTURNO || it.id == seleccionMagnesioId }
        .mapNotNull { programacion ->
            val item = inventarioPorId[programacion.itemId] ?: return@mapNotNull null
            FilaCronograma(programacion, item, MotorReglas.evaluar(item, programacion, contexto))
        }
        .sortedBy { it.programacion.hora }
}

@Composable
private fun ContadorMagnesioCard(resultado: ResultadoContadorMagnesio) {
    val color = if (resultado.excedeLimite) MaterialTheme.colorScheme.error else AppTheme.colors.success
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Magnesio elemental hoy", style = MaterialTheme.typography.titleMedium)
            Text(
                "${resultado.totalMg.toInt()} mg / ${LIMITE_MAGNESIO_ELEMENTAL_MG.toInt()} mg",
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
            LinearProgressIndicator(
                progress = { (resultado.totalMg / LIMITE_MAGNESIO_ELEMENTAL_MG).toFloat().coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                color = color
            )
            if (resultado.excedeLimite) {
                Text(
                    "Supera el límite superior — posible efecto laxante/GI.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun ItemDelDiaCard(
    fila: FilaCronograma,
    confirmado: Boolean,
    onConfirmar: () -> Unit,
    onDesconfirmar: () -> Unit,
    onElegirGrupoMagnesio: ((String) -> Unit)?,
    onActivarModoEntreno: (() -> Unit)?
) {
    val oscuro = isSystemInDarkTheme()
    val estilo = estiloDeCategoria(fila.item.categoria)
    val colorCategoria = if (oscuro) estilo.colorOscuro else estilo.color
    val estadoVisual = fila.resultado.aEstadoVisual()
    val colorEstado = colorDeEstado(estadoVisual)
    val bloqueado = fila.resultado is ResultadoRegla.Bloqueado

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = if (confirmado) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(Modifier.fillMaxWidth()) {
            // Acento de categoría: franja lateral muted — identidad, no estado.
            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(colorCategoria)
            )
            Column(Modifier.padding(16.dp).weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        estilo.icono,
                        contentDescription = estilo.etiqueta,
                        tint = colorCategoria,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(estilo.etiqueta, style = MaterialTheme.typography.labelMedium, color = colorCategoria)
                    Spacer(Modifier.weight(1f))
                    Text(
                        fila.programacion.hora.format(horaFormatter),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    fila.item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    fila.programacion.condicion.name.replace('_', ' '),
                    style = MaterialTheme.typography.bodySmall
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Icon(estadoVisual.icono, contentDescription = null, tint = colorEstado, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(estadoVisual.texto, style = MaterialTheme.typography.bodySmall, color = colorEstado)
                }

                if (onElegirGrupoMagnesio != null) {
                    Row(modifier = Modifier.padding(top = 8.dp)) {
                        listOf(CITRATO_MAGNESIO to "p_mg_nocturno_citrato", TREONATO_MAGNESIO to "p_mg_nocturno_treonato")
                            .forEach { (item, programacionId) ->
                                val seleccionado = fila.item.id == item.id
                                FilterChip(
                                    selected = seleccionado,
                                    onClick = { if (!seleccionado) onElegirGrupoMagnesio(programacionId) },
                                    label = { Text(if (item.id == CITRATO_MAGNESIO.id) "Citrato" else "Treonato") },
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }
                    }
                }

                if (onActivarModoEntreno != null) {
                    OutlinedButton(onClick = onActivarModoEntreno, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Activar modo entreno")
                    }
                    Text(
                        ReglasLimites.ADVERTENCIA_PRIMERA_ACTIVACION_ARGININA,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(modifier = Modifier.padding(top = 12.dp), horizontalArrangement = Arrangement.End) {
                    if (confirmado) {
                        TextButton(onClick = onDesconfirmar) { Text("Deshacer") }
                    } else {
                        Button(onClick = onConfirmar, enabled = !bloqueado) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Tomé")
                        }
                    }
                }
            }
        }
    }
}
