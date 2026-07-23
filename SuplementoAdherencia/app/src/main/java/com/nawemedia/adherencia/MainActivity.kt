package com.nawemedia.adherencia

import android.Manifest
import android.app.AlarmManager
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.nawemedia.adherencia.alarm.AlarmScheduler
import com.nawemedia.adherencia.ui.AdherenciaTheme
import com.nawemedia.adherencia.ui.HoyScreen
import com.nawemedia.adherencia.util.AlarmPrefs
import com.nawemedia.adherencia.util.NotificationHelper
import java.time.Instant
import java.time.ZoneId

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NotificationHelper.ensureChannel(this)
        setContent {
            AdherenciaTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppRoot(activity = this@MainActivity)
                }
            }
        }
    }
}

private enum class Pantalla(val etiqueta: String) { HOY("Hoy"), DIAGNOSTICO("Diagnóstico") }

@Composable
private fun AppRoot(activity: ComponentActivity) {
    // §9.4: rememberSaveable, no remember — la Activity se recrea al cambiar de
    // estado de plegado (cambia el WindowSizeClass), y la pestaña activa no debe perderse.
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.HOY) }
    Column(Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = pantalla.ordinal) {
            Pantalla.entries.forEach { p ->
                Tab(selected = pantalla == p, onClick = { pantalla = p }, text = { Text(p.etiqueta) })
            }
        }
        when (pantalla) {
            Pantalla.HOY -> HoyScreen(activity)
            Pantalla.DIAGNOSTICO -> Fase1Screen()
        }
    }
}

@Composable
private fun Fase1Screen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Estado que se refresca cada vez que la pantalla vuelve a primer plano
    // (p. ej. al volver de la pantalla de permisos del sistema).
    var refreshTick by remember { mutableIntStateOf(0) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) refreshTick++
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val hour = remember(refreshTick) { AlarmPrefs.getHour(context) }
    val minute = remember(refreshTick) { AlarmPrefs.getMinute(context) }
    val enabled = remember(refreshTick) { AlarmPrefs.isEnabled(context) }
    val nextTrigger = remember(refreshTick) { AlarmPrefs.getNextTrigger(context) }
    val eventLog = remember(refreshTick) { AlarmPrefs.getEventLog(context) }

    val notifGranted = remember(refreshTick) { hasNotificationPermission(context) }
    val exactGranted = remember(refreshTick) { canScheduleExact(context) }
    val batteryUnrestricted = remember(refreshTick) { isIgnoringBatteryOptimizations(context) }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { refreshTick++ }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Fase 1 · Motor de alarmas", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Prueba del gate más importante del proyecto: una alarma que sobrevive " +
                "48 h en reposo profundo y se reprograma tras reinicio.",
            style = MaterialTheme.typography.bodyMedium
        )

        // ---- §8.4 Robustez: los 4 checks ----
        ElevatedCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Robustez (§8.4)", style = MaterialTheme.typography.titleMedium)
                CheckRow(
                    label = "Notificaciones",
                    ok = notifGranted,
                    actionLabel = "Conceder"
                ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        openAppNotificationSettings(context)
                    }
                }
                CheckRow(
                    label = "Alarmas exactas",
                    ok = exactGranted,
                    actionLabel = "Conceder"
                ) { openExactAlarmSettings(context) }
                CheckRow(
                    label = "Batería sin restricciones",
                    ok = batteryUnrestricted,
                    actionLabel = "Ajustar"
                ) { requestIgnoreBatteryOptimizations(context) }
                Text(
                    "Paso manual adicional en Samsung: Device Care → Batería → " +
                        "quitar la app de «Apps en reposo profundo».",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // ---- Configuración de la alarma ----
        ElevatedCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Alarma diaria de prueba", style = MaterialTheme.typography.titleMedium)
                Text(
                    String.format("Hora: %02d:%02d", hour, minute),
                    style = MaterialTheme.typography.headlineMedium
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = {
                        TimePickerDialog(
                            context,
                            { _, h, m ->
                                AlarmPrefs.setTime(context, h, m)
                                if (AlarmPrefs.isEnabled(context)) AlarmScheduler.scheduleDaily(context)
                                refreshTick++
                            },
                            hour, minute, true
                        ).show()
                    }) { Text("Cambiar hora") }

                    Button(
                        onClick = {
                            AlarmPrefs.setEnabled(context, !enabled)
                            if (!enabled) AlarmScheduler.scheduleDaily(context)
                            else AlarmScheduler.cancel(context)
                            refreshTick++
                        }
                    ) { Text(if (enabled) "Desactivar" else "Activar") }
                }

                if (nextTrigger > 0L) {
                    Text(
                        "Próximo disparo: " + formatMillis(nextTrigger),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // ---- Prueba rápida de cableado ----
        OutlinedButton(
            onClick = {
                val at = System.currentTimeMillis() + 2 * 60_000L
                AlarmScheduler.scheduleAt(context, at, label = "Prueba (2 min)")
                refreshTick++
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Probar ahora: disparar en 2 minutos") }

        // ---- Log de eventos (auditoría del gate) ----
        ElevatedCard {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Text("Log de eventos", style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = {
                        AlarmPrefs.clearEventLog(context); refreshTick++
                    }) { Text("Limpiar") }
                }
                Text(
                    eventLog.ifBlank { "Sin eventos aún." },
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // ---- §3.4 Disclaimer obligatorio ----
        Text(
            "Herramienta de organización personal. No sustituye indicación médica. " +
                "Las reglas de separación horaria y los límites de dosis deben validarse " +
                "con el infectólogo o farmacéutico tratante antes de su uso.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp)
        )
    }
}

@Composable
private fun CheckRow(label: String, ok: Boolean, actionLabel: String, onAction: () -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text((if (ok) "✅ " else "⛔ ") + label, style = MaterialTheme.typography.bodyLarge)
        if (!ok) {
            TextButton(onClick = onAction) { Text(actionLabel) }
        }
    }
}

// ---------- Helpers de permisos / estado del sistema ----------

private fun hasNotificationPermission(context: Context): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
            android.content.pm.PackageManager.PERMISSION_GRANTED
    } else true

private fun canScheduleExact(context: Context): Boolean {
    val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) am.canScheduleExactAlarms() else true
}

private fun isIgnoringBatteryOptimizations(context: Context): Boolean {
    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, Uri.parse("package:${context.packageName}"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

@Suppress("BatteryLife")
private fun requestIgnoreBatteryOptimizations(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

private fun openAppNotificationSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
}

private fun formatMillis(millis: Long): String {
    val dt = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDateTime()
    return String.format(
        "%02d/%02d %02d:%02d",
        dt.dayOfMonth, dt.monthValue, dt.hour, dt.minute
    )
}
