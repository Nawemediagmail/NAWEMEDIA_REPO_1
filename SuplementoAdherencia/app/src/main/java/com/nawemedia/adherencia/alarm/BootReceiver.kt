package com.nawemedia.adherencia.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.nawemedia.adherencia.util.AlarmPrefs

/**
 * §8.3 Persistencia tras reinicio.
 * Las alarmas de AlarmManager se pierden en cada reboot; este receiver las
 * reprograma desde el estado guardado en cuanto el sistema termina de arrancar.
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        Log.i("BootReceiver", "onReceive: $action")

        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                if (AlarmPrefs.isEnabled(context)) {
                    AlarmPrefs.appendEvent(context, "♻️ Reprogramando tras: $action")
                    AlarmScheduler.scheduleDaily(context)
                } else {
                    AlarmPrefs.appendEvent(context, "Boot ($action) — alarma deshabilitada")
                }
            }
        }
    }
}
