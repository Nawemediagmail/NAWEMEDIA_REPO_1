package com.nawemedia.adherencia.ui

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nawemedia.adherencia.domain.CITRATO_MAGNESIO
import com.nawemedia.adherencia.domain.CRONOGRAMA_SEED
import com.nawemedia.adherencia.domain.ContextoEvaluacion
import com.nawemedia.adherencia.domain.EstadoItem
import com.nawemedia.adherencia.domain.FuenteDato
import com.nawemedia.adherencia.domain.INVENTARIO
import com.nawemedia.adherencia.domain.Item
import com.nawemedia.adherencia.domain.L_ARGININA
import com.nawemedia.adherencia.domain.LIMITE_MAGNESIO_ELEMENTAL_MG
import com.nawemedia.adherencia.domain.MotorReglas
import com.nawemedia.adherencia.domain.Programacion
import com.nawemedia.adherencia.domain.ReglaQuelacion
import com.nawemedia.adherencia.domain.ReglasLimites
import com.nawemedia.adherencia.domain.ResultadoContadorMagnesio
import com.nawemedia.adherencia.domain.ResultadoRegla
import com.nawemedia.adherencia.domain.TAR_ANCLA_DEFAULT
import com.nawemedia.adherencia.domain.TREONATO_MAGNESIO
import com.nawemedia.adherencia.domain.calcularMagnesioElementalTotal
import com.nawemedia.adherencia.util.ConfirmacionesPrefs
import com.nawemedia.adherencia.util.PreferenciasUsuario
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

private val inventarioPorId: Map<String, Item> = INVENTARIO.associateBy { it.id }
private val horaFormatter = DateTimeFormatter.ofPattern("HH:mm")
private const val GRUPO_MAGNESIO_NOCTURNO = "magnesio_nocturno"

private data class FilaCronograma(val programacion: Programacion, val item: Item, val resultado: ResultadoRegla)

/**
 * §9 — Punto de entrada de la capa adaptativa. Tres layouts posibles, no dos:
 * tabletop (bisagra horizontal medio-abierta) tiene prioridad sobre el ancho,
 * porque un Z Fold parado en la mesa puede reportar el mismo ancho "expandido"
 * que uno plano sobre el escritorio, pero necesita el layout partido (§9.3).
 */
@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun HoyScreen(activity: Activity) {
    val context = LocalContext.current
    var refresco by remember { mutableIntStateOf(0) }
    val hoy = remember { LocalDate.now() }
    val ahora = remember { LocalTime.now() }

    val seleccionMagnesio = remember(refresco) {
        PreferenciasUsuario.seleccionGrupo(context, GRUPO_MAGNESIO_NOCTURNO, "p_mg_nocturno_citrato")
    }
    val modoEntreno = remember(refresco) { PreferenciasUsuario.modoEntrenoActivo(context) }
    val filas = remember(refresco, seleccionMagnesio, modoEntreno) {
        construirCronogramaDeHoy(seleccionMagnesio, modoEntreno, hoy)
    }
    val magnesioTotal = remember(filas) { calcularMagnesioElementalTotal(filas.map { it.item }) }

    // §9.4: sobrevive la recreación de Activity al cambiar de estado de plegado.
    var seleccionId by rememberSaveable { mutableStateOf<String?>(null) }

    val onConfirmar: (String) -> Unit = { id -> ConfirmacionesPrefs.confirmar(context, id, hoy); refresco++ }
    val onDesconfirmar: (String) -> Unit = { id -> ConfirmacionesPrefs.quitarConfirmacion(context, id, hoy); refresco++ }
    val onElegirGrupoMagnesio: (String) -> Unit = { id ->
        PreferenciasUsuario.elegirGrupo(context, GRUPO_MAGNESIO_NOCTURNO, id); refresco++
    }
    val onActivarModoEntreno: () -> Unit = { PreferenciasUsuario.setModoEntrenoActivo(context, true); refresco++ }

    val windowSizeClass = calculateWindowSizeClass(activity)
    val foldInfo = rememberFoldInfo(activity)

    when {
        foldInfo.esTabletop && foldInfo.bisagra != null -> {
            val actual = remember(filas, refresco) { itemActual(filas, context, hoy, ahora) }
            val confirmadoActual = actual?.let { ConfirmacionesPrefs.estaConfirmado(context, it.programacion.id, hoy) } ?: false
            TabletopHoy(
                itemActual = actual,
                confirmado = confirmadoActual,
                bisagra = foldInfo.bisagra,
                onConfirmar = { actual?.let { onConfirmar(it.programacion.id) } },
                onDesconfirmar = { actual?.let { onDesconfirmar(it.programacion.id) } }
            )
        }

        windowSizeClass.widthSizeClass != WindowWidthSizeClass.Compact -> {
            ExpandidoHoy(
                filas = filas,
                magnesioTotal = magnesioTotal,
                context = context,
                hoy = hoy,
                tar = TAR_ANCLA_DEFAULT,
                seleccionId = seleccionId,
                onSeleccionar = { seleccionId = it },
                modoEntreno = modoEntreno,
                onConfirmar = onConfirmar,
                onDesconfirmar = onDesconfirmar,
                onElegirGrupoMagnesio = onElegirGrupoMagnesio,
                onActivarModoEntreno = onActivarModoEntreno
            )
        }

        else -> {
            CompactoHoy(
                filas = filas,
                magnesioTotal = magnesioTotal,
                context = context,
                hoy = hoy,
                modoEntreno = modoEntreno,
                onConfirmar = onConfirmar,
                onDesconfirmar = onDesconfirmar,
                onElegirGrupoMagnesio = onElegirGrupoMagnesio,
                onActivarModoEntreno = onActivarModoEntreno
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

/** El ítem "actual" (§9.1/§9.3): el pendiente más reciente ya vencido, o si no hay, el próximo. */
private fun itemActual(filas: List<FilaCronograma>, context: Context, hoy: LocalDate, ahora: LocalTime): FilaCronograma? {
    val pendientes = filas.filter { !ConfirmacionesPrefs.estaConfirmado(context, it.programacion.id, hoy) }
    if (pendientes.isEmpty()) return null
    val yaDeberianHaberseTomado = pendientes.filter { it.programacion.hora <= ahora }
    return yaDeberianHaberseTomado.maxByOrNull { it.programacion.hora } ?: pendientes.minByOrNull { it.programacion.hora }
}

// ---------------------------------------------------------------------------
// §9.1 — Cover screen (compacto, plegado cerrado): lista vertical simple.
// ---------------------------------------------------------------------------
@Composable
private fun CompactoHoy(
    filas: List<FilaCronograma>,
    magnesioTotal: ResultadoContadorMagnesio,
    context: Context,
    hoy: LocalDate,
    modoEntreno: Boolean,
    onConfirmar: (String) -> Unit,
    onDesconfirmar: (String) -> Unit,
    onElegirGrupoMagnesio: (String) -> Unit,
    onActivarModoEntreno: () -> Unit
) {
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
                onConfirmar = { onConfirmar(fila.programacion.id) },
                onDesconfirmar = { onDesconfirmar(fila.programacion.id) },
                onElegirGrupoMagnesio = grupoHandlerSiCorresponde(fila, onElegirGrupoMagnesio),
                onActivarModoEntreno = entrenoHandlerSiCorresponde(fila, modoEntreno, onActivarModoEntreno)
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

// ---------------------------------------------------------------------------
// §9.2 — Desplegado (expandido): two-pane list-detail.
// ---------------------------------------------------------------------------
@Composable
private fun ExpandidoHoy(
    filas: List<FilaCronograma>,
    magnesioTotal: ResultadoContadorMagnesio,
    context: Context,
    hoy: LocalDate,
    tar: LocalTime,
    seleccionId: String?,
    onSeleccionar: (String) -> Unit,
    modoEntreno: Boolean,
    onConfirmar: (String) -> Unit,
    onDesconfirmar: (String) -> Unit,
    onElegirGrupoMagnesio: (String) -> Unit,
    onActivarModoEntreno: () -> Unit
) {
    Row(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filas, key = { it.programacion.id }) { fila ->
                val confirmado = ConfirmacionesPrefs.estaConfirmado(context, fila.programacion.id, hoy)
                ItemDelDiaCard(
                    fila = fila,
                    confirmado = confirmado,
                    seleccionado = fila.programacion.id == seleccionId,
                    onConfirmar = { onConfirmar(fila.programacion.id) },
                    onDesconfirmar = { onDesconfirmar(fila.programacion.id) },
                    onElegirGrupoMagnesio = grupoHandlerSiCorresponde(fila, onElegirGrupoMagnesio),
                    onActivarModoEntreno = entrenoHandlerSiCorresponde(fila, modoEntreno, onActivarModoEntreno),
                    onSeleccionar = { onSeleccionar(fila.programacion.id) }
                )
            }
        }

        Box(
            Modifier
                .width(1.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        Box(Modifier.weight(1f).fillMaxHeight()) {
            val filaSeleccionada = filas.firstOrNull { it.programacion.id == seleccionId }
            val confirmadoSel = filaSeleccionada?.let {
                ConfirmacionesPrefs.estaConfirmado(context, it.programacion.id, hoy)
            } ?: false
            PanelDetalle(
                fila = filaSeleccionada,
                confirmado = confirmadoSel,
                magnesioTotal = magnesioTotal,
                tar = tar,
                modoEntreno = modoEntreno,
                onConfirmar = { filaSeleccionada?.let { onConfirmar(it.programacion.id) } },
                onDesconfirmar = { filaSeleccionada?.let { onDesconfirmar(it.programacion.id) } },
                onElegirGrupoMagnesio = onElegirGrupoMagnesio,
                onActivarModoEntreno = onActivarModoEntreno
            )
        }
    }
}

@Composable
private fun PanelDetalle(
    fila: FilaCronograma?,
    confirmado: Boolean,
    magnesioTotal: ResultadoContadorMagnesio,
    tar: LocalTime,
    modoEntreno: Boolean,
    onConfirmar: () -> Unit,
    onDesconfirmar: () -> Unit,
    onElegirGrupoMagnesio: (String) -> Unit,
    onActivarModoEntreno: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ContadorMagnesioCard(magnesioTotal)

        // Widget persistente §9.2: próxima ventana segura de magnesio.
        val ahora = remember { LocalTime.now() }
        val ventana = remember { ReglaQuelacion.proximaVentanaSegura(ahora, tar) }
        Text(
            "Próxima ventana segura de magnesio: ${ventana.format(horaFormatter)}" +
                if (ventana == ahora) " (ahora)" else "",
            style = MaterialTheme.typography.bodyMedium
        )

        if (fila == null) {
            Text(
                "Elegí un ítem del cronograma para ver su ficha completa.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 24.dp)
            )
            return@Column
        }

        ItemDelDiaCard(
            fila = fila,
            confirmado = confirmado,
            onConfirmar = onConfirmar,
            onDesconfirmar = onDesconfirmar,
            onElegirGrupoMagnesio = grupoHandlerSiCorresponde(fila, onElegirGrupoMagnesio),
            onActivarModoEntreno = entrenoHandlerSiCorresponde(fila, modoEntreno, onActivarModoEntreno)
        )

        if (fila.item.nutrientes.isNotEmpty()) {
            ElevatedCard(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Datos de etiqueta", style = MaterialTheme.typography.titleMedium)
                    fila.item.nutrientes.forEach { n ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(n.nombre, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                formatCantidad(n.cantidad) + " " + n.unidad +
                                    if (n.fuente == FuenteDato.INFERENCIA) " (estimado)" else "",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        if (fila.item.notaEstado != null) {
            Text(
                fila.item.notaEstado,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// §9.3 — Flex mode / tabletop: partido arriba/abajo evitando la bisagra.
// ---------------------------------------------------------------------------
@Composable
private fun TabletopHoy(
    itemActual: FilaCronograma?,
    confirmado: Boolean,
    bisagra: Rect,
    onConfirmar: () -> Unit,
    onDesconfirmar: () -> Unit
) {
    val density = LocalDensity.current
    val alturaSuperior = with(density) { bisagra.top.toDp() }
    val alturaBisagra = with(density) { (bisagra.bottom - bisagra.top).toDp() }

    Column(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(alturaSuperior)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (itemActual == null) {
                Text(
                    "Todo confirmado por ahora 🎉",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
            } else {
                val oscuro = isSystemInDarkTheme()
                val estilo = estiloDeCategoria(itemActual.item.categoria)
                val colorCategoria = if (oscuro) estilo.colorOscuro else estilo.color
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(estilo.etiqueta, style = MaterialTheme.typography.labelLarge, color = colorCategoria)
                    Text(
                        itemActual.item.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        "${itemActual.programacion.hora.format(horaFormatter)} · " +
                            itemActual.programacion.condicion.name.replace('_', ' '),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // No se dibuja contenido crítico sobre la bisagra (§9.3).
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(alturaBisagra)
        )

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            if (itemActual != null) {
                if (confirmado) {
                    OutlinedButton(onClick = onDesconfirmar, modifier = Modifier.size(220.dp, 64.dp)) {
                        Text("Deshacer", style = MaterialTheme.typography.titleMedium)
                    }
                } else {
                    Button(
                        onClick = onConfirmar,
                        enabled = itemActual.resultado !is ResultadoRegla.Bloqueado,
                        modifier = Modifier.size(220.dp, 64.dp)
                    ) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tomé", style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Tarjeta compartida por los tres layouts.
// ---------------------------------------------------------------------------
private fun grupoHandlerSiCorresponde(fila: FilaCronograma, handler: (String) -> Unit): ((String) -> Unit)? =
    if (fila.programacion.grupoExcluyente == GRUPO_MAGNESIO_NOCTURNO) handler else null

private fun entrenoHandlerSiCorresponde(fila: FilaCronograma, modoEntreno: Boolean, handler: () -> Unit): (() -> Unit)? =
    if (fila.item.id == L_ARGININA.id && !modoEntreno) handler else null

private fun formatCantidad(v: Double): String =
    if (v == v.toLong().toDouble()) v.toLong().toString() else v.toString()

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
    seleccionado: Boolean = false,
    onConfirmar: () -> Unit,
    onDesconfirmar: () -> Unit,
    onElegirGrupoMagnesio: ((String) -> Unit)?,
    onActivarModoEntreno: (() -> Unit)?,
    onSeleccionar: (() -> Unit)? = null
) {
    val oscuro = isSystemInDarkTheme()
    val estilo = estiloDeCategoria(fila.item.categoria)
    val colorCategoria = if (oscuro) estilo.colorOscuro else estilo.color
    val estadoVisual = fila.resultado.aEstadoVisual()
    val colorEstado = colorDeEstado(estadoVisual)
    val bloqueado = fila.resultado is ResultadoRegla.Bloqueado

    var modifierTarjeta = Modifier.fillMaxWidth() as Modifier
    if (onSeleccionar != null) modifierTarjeta = modifierTarjeta.clickable(onClick = onSeleccionar)

    ElevatedCard(
        modifier = modifierTarjeta,
        colors = CardDefaults.elevatedCardColors(
            containerColor = when {
                seleccionado -> MaterialTheme.colorScheme.secondaryContainer
                confirmado -> MaterialTheme.colorScheme.surfaceVariant
                else -> MaterialTheme.colorScheme.surface
            }
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
                                val seleccionadoGrupo = fila.item.id == item.id
                                FilterChip(
                                    selected = seleccionadoGrupo,
                                    onClick = { if (!seleccionadoGrupo) onElegirGrupoMagnesio(programacionId) },
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
