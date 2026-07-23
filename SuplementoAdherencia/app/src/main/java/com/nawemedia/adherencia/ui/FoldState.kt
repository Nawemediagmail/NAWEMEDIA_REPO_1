package com.nawemedia.adherencia.ui

import android.app.Activity
import android.graphics.Rect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo

/**
 * §9.3 — Postura "tabletop": bisagra horizontal, medio abierta (~90°), teléfono
 * parado en la mesa. Cualquier otro caso (cerrado, desplegado plano, bisagra
 * vertical en modo libro) se trata como layout normal (compacto/expandido).
 */
data class FoldInfo(val esTabletop: Boolean, val bisagra: Rect?)

private val SIN_PLIEGUE = FoldInfo(esTabletop = false, bisagra = null)

@Composable
fun rememberFoldInfo(activity: Activity): FoldInfo {
    val windowInfoTracker = remember(activity) { WindowInfoTracker.getOrCreate(activity) }
    val layoutInfo by windowInfoTracker.windowLayoutInfo(activity)
        .collectAsState(initial = WindowLayoutInfo(emptyList()))

    val bisagra = layoutInfo.displayFeatures.filterIsInstance<FoldingFeature>().firstOrNull()
        ?: return SIN_PLIEGUE

    return if (
        bisagra.state == FoldingFeature.State.HALF_OPENED &&
        bisagra.orientation == FoldingFeature.Orientation.HORIZONTAL
    ) {
        FoldInfo(esTabletop = true, bisagra = bisagra.bounds)
    } else {
        SIN_PLIEGUE
    }
}
