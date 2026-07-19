# Adherencia — Fase 1: Motor de alarmas

Prototipo de **Fase 1** del PRD *App de Adherencia a Suplementación y TAR*
(target: Samsung Galaxy Z Fold 7).

> **Alcance deliberadamente mínimo.** Esta fase construye **solo el motor de
> alarmas** con **una alarma de prueba** (§11 del PRD). No hay base cifrada,
> ni inventario, ni motor de reglas, ni UX plegable todavía. Eso es correcto:
> el PRD marca este gate como *"el más importante del proyecto"*. Si la alarma
> no sobrevive 48 h de reposo profundo de Samsung + un reinicio, nada de lo que
> se construya encima sirve. Primero se valida esto; después se sigue.

## Qué implementa

| Requisito PRD | Dónde |
|---|---|
| `setAlarmClock()` como mecanismo primario (inmune a Doze, §8.1) | `alarm/AlarmScheduler.kt` |
| Reprogramación en `BOOT_COMPLETED` (§8.3) | `alarm/BootReceiver.kt` |
| Notificación full-screen `CATEGORY_ALARM` + `VISIBILITY_PRIVATE` (§8.5) | `util/NotificationHelper.kt` |
| Pantalla de alarma que suena/vibra y aparece con el equipo bloqueado | `AlarmActivity.kt` |
| Los 4 checks de robustez con deep-link a ajustes (§8.4) | `MainActivity.kt` |
| Disclaimer obligatorio (§3.4) | `MainActivity.kt` |
| Log de eventos auditable para verificar el gate | `util/AlarmPrefs.kt` |

Repetición diaria: `setAlarmClock()` es de **un solo disparo**, así que la
próxima ocurrencia se reprograma cada vez que la alarma suena (`AlarmReceiver`)
y en cada arranque (`BootReceiver`).

## Stack

Kotlin · Jetpack Compose + Material 3 · minSdk 30 · targetSdk 35 ·
AGP 8.7.3 · Gradle 8.11.1. Sin red, sin analytics, sin backend (§6).

> Persistencia en Fase 1: `SharedPreferences` plano a propósito. Room + SQLCipher
> (§6/§7) es Fase 0/2 y **no** forma parte de este gate.

## Cómo compilar

Requiere Android Studio (o el Android SDK con `local.properties` apuntando a él).
**No se pudo compilar en el entorno donde se generó este código** — no había
Android SDK instalado —, así que el APK hay que generarlo en tu máquina:

```bash
# 1. Abrir la carpeta SuplementoAdherencia/ en Android Studio, o por CLI:
./gradlew assembleDebug
# 2. Instalar en el Z Fold 7 conectado por USB:
./gradlew installDebug
```

## EL GATE (lo que tenés que correr vos en el Z Fold 7)

Esto es lo único que valida Fase 1 y **solo se puede correr en el dispositivo
físico** — ningún emulador reproduce el Doze agresivo de Samsung.

### Preparación (una vez)
1. Instalar la app y abrirla.
2. En la tarjeta **Robustez**, dejar los 3 checks en ✅:
   - Notificaciones
   - Alarmas exactas
   - Batería sin restricciones
3. Paso manual de Samsung que la app **no** puede togglear por vos:
   *Device Care → Batería → Límites de uso en segundo plano* → sacar la app de
   **"Apps en reposo profundo"** y de **"Apps en reposo"**.

### Prueba rápida de cableado (2 minutos)
- Tocar **"Probar ahora: disparar en 2 minutos"**.
- Bloquear el teléfono y esperar. A los 2 min debe aparecer la pantalla de
  alarma a pantalla completa, con sonido, **sobre la pantalla bloqueada**.
- Si esto no pasa, no tiene sentido seguir con el test de 48 h: revisar permisos.

### Gate real (48 horas)
1. Poner una **hora** de alarma y tocar **Activar**.
2. **Cerrar la app** (sacarla de recientes) y **no volver a abrirla**.
3. Dejar el teléfono quieto, sin cargador, ~48 h. Idealmente atravesar la hora
   de la alarma **dos noches**.
4. **Reiniciar el teléfono** en algún momento del período (valida `BootReceiver`).
5. Criterios de aprobación:
   - [ ] La alarma disparó **todos** los días a la hora fijada, con la app cerrada.
   - [ ] Disparó también **después** del reinicio (sin reabrir la app).
   - [ ] Al reabrir, el **Log de eventos** muestra las líneas `🔔 DISPARÓ` y
         `♻️ Reprogramando tras: android.intent.action.BOOT_COMPLETED`.

El log de eventos (pantalla principal) es tu evidencia: registra cada
programación, cada disparo y cada reprogramación por boot, con timestamp.

### Si el gate falla
El sospechoso #1 en Samsung es el reposo profundo / optimización de batería.
Reconfirmar el paso manual de Device Care. Si aun así falla, el diagnóstico es
parte de esta fase — no se avanza a Fase 2 hasta que dispare limpio.

---

*Herramienta de organización personal. No sustituye indicación médica.*
