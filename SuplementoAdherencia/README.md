# Adherencia — Fase 1 + Fase 2

Prototipo del PRD *App de Adherencia a Suplementación y TAR*
(target: Samsung Galaxy Z Fold 7).

## Fase 1 — Motor de alarmas ✅ GATE PASADO

> **Alcance deliberadamente mínimo.** Esta fase construye **solo el motor de
> alarmas** con **una alarma de prueba** (§11 del PRD). El PRD marca este gate
> como *"el más importante del proyecto"*: si la alarma no sobrevive 48 h de
> reposo profundo de Samsung + un reinicio, nada de lo que se construya encima
> sirve.
>
> **Estado: verificado en dispositivo físico (Z Fold 7).** Corrió ~74 h con la
> app cerrada, disparó 3 noches consecutivas a horario exacto, y se reprogramó
> sola tras un reinicio real (`BOOT_COMPLETED` confirmado en el log de eventos).

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
Reconfirmar el paso manual de Device Care.

---

## Fase 2 — Motor de reglas (§3) + contador de magnesio

Capa de dominio en Kotlin puro (`domain/`), sin UI todavía — la vista "Hoy"
es Fase 3 (§11). No depende de Room ni de Android: es lógica de negocio
testeable con JUnit local, sin dispositivo.

| Archivo | Qué implementa |
|---|---|
| `domain/Inventario.kt` | Los 12 ítems de §1/§2, transcriptos de etiqueta. Único valor [INFERENCIA]: magnesio elemental del treonato (§1.3, sin desglose en etiqueta) |
| `domain/ReglaQuelacion.kt` | §3.1 — ventana de bloqueo Mg↔dolutegravir: permitido solo `≤TAR-2h` o `≥TAR+6h` |
| `domain/ContadorMagnesio.kt` | §3.3 — suma de magnesio elemental de todas las fuentes activas, alerta a 350 mg |
| `domain/ReglasLimites.kt` | §3.3 resto: NAC vencido bloqueado, 2ª fuente de vit. D bloqueada, nicotinamida 2 cáps requiere confirmación, té verde nocturno con advertencia, L-arginina inactiva hasta "modo entreno" |

### Gate de Fase 2 (§11) — ✅ verificado, no solo razonado

> *"Test unitario: programar magnesio a TAR+1h devuelve Bloqueado. Contador
> elemental suma correcto."*

Corrido y confirmado con `./gradlew testDebugUnitTest`: **21 tests, 21
pasaron, 0 fallas.** Incluye exactamente los dos casos del gate:
- `ReglaQuelacionTest > gate del PRD - magnesio a TAR mas 1h devuelve bloqueado`
- `ContadorMagnesioTest > gate del PRD - K2D3 mas citrato suman 174 mg`

Para volver a correrlos en Android Studio: click derecho sobre
`app/src/test/java/.../domain/` → **Run Tests**, o `./gradlew testDebugUnitTest`
desde la terminal del proyecto.

### Qué falta para que esto sea usable (no es parte de este gate)
El motor de reglas hoy es una librería de funciones puras, sin persistencia
(Fase 0: Room+SQLCipher) ni UI que las invoque (Fase 3). Ambas quedan
pendientes; este commit solo entrega la lógica de negocio validada.

---

*Herramienta de organización personal. No sustituye indicación médica. Las
reglas de separación horaria y los límites de dosis deben validarse con el
infectólogo o farmacéutico tratante antes de su uso — el TAR default (10:30,
ventana 2h/6h) sigue pendiente de confirmación médica (§12.1).*
