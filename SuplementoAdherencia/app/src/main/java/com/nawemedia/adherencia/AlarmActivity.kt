package com.nawemedia.adherencia

import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.nawemedia.adherencia.ui.AdherenciaTheme
import com.nawemedia.adherencia.util.NotificationHelper

/**
 * §8.5 Pantalla de alarma a pantalla completa. Aparece incluso con el dispositivo
 * bloqueado (showWhenLocked/turnScreenOn en el manifest). En Fase 1 la acción
 * [Tomé] solo silencia y cierra; el registro persistente de tomas es Fase 2/3.
 */
class AlarmActivity : ComponentActivity() {

    companion object {
        const val EXTRA_LABEL = "extra_label"
    }

    private var ringtone: android.media.Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val label = intent.getStringExtra(EXTRA_LABEL) ?: getString(R.string.alarm_screen_generic)

        startAlarmSound()

        setContent {
            AdherenciaTheme {
                AlarmScreen(
                    label = label,
                    onTaken = {
                        stopAlarm()
                        NotificationHelper.dismiss(this)
                        finish()
                    }
                )
            }
        }
    }

    private fun startAlarmSound() {
        try {
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            ringtone = RingtoneManager.getRingtone(applicationContext, uri)?.apply { play() }
        } catch (_: Exception) { /* no bloquear la UI si falla el sonido */ }

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }
        val pattern = longArrayOf(0, 600, 400, 600, 400)
        vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
    }

    private fun stopAlarm() {
        ringtone?.stop()
        vibrator?.cancel()
    }

    override fun onDestroy() {
        stopAlarm()
        super.onDestroy()
    }
}

@Composable
private fun AlarmScreen(label: String, onTaken: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.errorContainer) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "⏰",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = label,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp)
            )
            Text(
                text = "Es la hora de tu toma.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 48.dp)
            )
            Button(
                onClick = onTaken,
                modifier = Modifier.size(width = 220.dp, height = 64.dp)
            ) {
                Text("Tomé ✓", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
