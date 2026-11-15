# Seguimiento de entregas: ubicación, carga y geocerca

Cuatro endpoints sobre `/api/deliveries/{deliveryId}` cubren el seguimiento de transporte de una entrega: el conductor informa su ubicación y sus hitos de carga, el distribuidor y el conductor consultan el último estado y el historial, y el distribuidor define el círculo de seguridad (geocerca) de la entrega.

| Endpoint | Controller | Quién lo usa |
|---|---|---|
| `GET /api/deliveries/{deliveryId}/tracking` | `DeliveryTrackingQueryController` (módulo `tracking`) | Distribuidor propietario o conductor asignado |
| `GET /api/deliveries/{deliveryId}/tracking/samples` | `DeliveryTrackingQueryController` (módulo `tracking`) | Distribuidor propietario o conductor asignado |
| `POST /api/deliveries/{deliveryId}/transport-evidence` | `TransportEvidenceController` (módulo `tracking`) | Solo el conductor asignado |
| `POST /api/deliveries/{deliveryId}/geofence-policies` | `GeofencePoliciesController` (módulo `safety`) | Solo el distribuidor propietario |

Las cuatro rutas ya figuran en `T01-A-rest-ledger.md`. Este documento no cambia ningún endpoint, resource, migración ni snapshot.

## Cómo se guarda el seguimiento

El módulo `tracking` guarda dos niveles de información:

- **Evidencia cruda** (`TransportEvidenceSample`): cada muestra aceptada, de tipo `POSITION` o `LOAD`, con `recordedAt` (hora informada por el dispositivo) y `receivedAt` (hora del servidor). Una muestra aceptada se conserva aunque llegue tarde.
- **Último estado** (`DeliveryTracking`): resumen de la última posición y del último hito de carga. Solo avanza con una muestra cuyo `recordedAt` sea estrictamente posterior al último guardado. Una muestra más vieja o con la misma hora se guarda como evidencia, pero no mueve el último estado.

El campo `latestAdvanced` de cada muestra indica si esa muestra movió el último estado en el momento de llegar.

## Cómo se decide quién entra

El distribuidor propietario sale de la entrega y el conductor asignado sale del catálogo de flota. Ninguno de los dos se acepta desde el cuerpo de la petición: el cuerpo de `transport-evidence` no tiene campo `driverId`. Los 403 y 404 de estos controllers no llevan cuerpo. Los errores de validación y de reglas de negocio usan el recurso compartido `{code, message, details}`.

## Consultar el seguimiento

Ambos `GET` aplican la misma regla de acceso. Pueden consultar:

- El distribuidor propietario: usuario con `ROLE_PROVIDER` cuyo `providerId` coincide con el de la entrega.
- El conductor asignado: usuario cuyo `userId` coincide con el del conductor de la entrega, siempre que el conductor pertenezca al mismo distribuidor que la entrega. No necesita identidad de distribuidor.

Si la entrega o su conductor no existen, responde 404. Si el usuario no es ninguno de los dos, responde 403.

### `GET /api/deliveries/{deliveryId}/tracking`

Devuelve el último estado confiable (los valores del ejemplo son ilustrativos):

```json
{"deliveryId":31,"providerId":4,"driverId":8,"lastLatitude":10.5,"lastLongitude":-66.9,"lastAccuracyMeters":8.0,"lastPositionAt":"2026-09-01T10:00:00Z","lastPositionEvidenceId":120,"loaded":true,"lastLoadMilestone":"LOADED","lastLoadAt":"2026-09-01T09:00:00Z","lastLoadVolume":120.0,"lastLoadUnit":"LITRE","lastLoadEvidenceId":118,"version":3}
```

Si la entrega existe pero todavía no recibió ninguna evidencia, responde 404. Los campos de posición y de carga son independientes: una entrega con solo un hito de carga tiene los campos de posición vacíos, según el modelo.

### `GET /api/deliveries/{deliveryId}/tracking/samples`

Devuelve todas las muestras de la entrega, ordenadas de la más antigua a la más reciente según `recordedAt`, no según el orden de llegada:

```json
[{"evidenceId":118,"kind":"LOAD","latitude":null,"longitude":null,"accuracyMeters":null,"milestone":"LOADED","volume":120.0,"unit":"LITRE","recordedAt":"2026-09-01T09:00:00Z","receivedAt":"2026-09-01T09:00:02Z","latestAdvanced":true},
 {"evidenceId":120,"kind":"POSITION","latitude":10.5,"longitude":-66.9,"accuracyMeters":8.0,"milestone":null,"volume":null,"unit":null,"recordedAt":"2026-09-01T10:00:00Z","receivedAt":"2026-09-01T10:00:01Z","latestAdvanced":true}]
```

`latestAdvanced` refleja lo ocurrido al llegar cada muestra. Si llegan tres posiciones con horas 10:10, 10:00 y 10:05 (en ese orden de llegada), la lista sale 10:00, 10:05, 10:10 y solo la de 10:10 figura con `latestAdvanced: true`. El resultado puede ser una lista vacía.

### Respuestas

| Código | Cuándo |
|---|---|
| 200 | Estado o historial devuelto |
| 401 | Sin autenticar (regla general de seguridad de la aplicación) |
| 403 | El usuario no es el distribuidor propietario ni el conductor asignado (el conductor solo cuenta si pertenece al mismo distribuidor que la entrega) |
| 404 | La entrega o su conductor no existen; en `/tracking`, también cuando la entrega aún no tiene seguimiento |

## Registrar evidencia de transporte

`POST /api/deliveries/{deliveryId}/transport-evidence` lo usa la aplicación del conductor para informar una ubicación o un hito de carga.

### Quién puede

Solo el conductor asignado. El controller exige un usuario autenticado cuyo `userId` coincida con el del conductor de la entrega, y que el conductor pertenezca al mismo distribuidor que la entrega. No comprueba un rol concreto, solo esa identidad. La entrega y el conductor se resuelven en el servidor, nunca desde el cuerpo.

### Cuerpo

El mismo cuerpo sirve para dos tipos de evidencia. Solo `type` es obligatorio a nivel de formato; el resto se exige según el tipo.

```json
{"type":"POSITION","latitude":10.5,"longitude":-66.9,"accuracyMeters":8.0,"recordedAt":"2026-09-01T10:00:00Z","eventId":"offline-retry-1"}
```

```json
{"type":"LOAD","milestone":"LOADED","volume":120.0,"unit":"LITRE","recordedAt":"2026-09-01T09:00:00Z"}
```

| Campo | Regla |
|---|---|
| `type` | Obligatorio. `POSITION` o `LOAD`, sin distinguir mayúsculas. Otro valor da 400 |
| `latitude`, `longitude` | Obligatorios en `POSITION`. Latitud entre -90 y 90, longitud entre -180 y 180. Fuera de rango o ausentes da 400 |
| `accuracyMeters` | Opcional. Si viene, no puede ser negativo |
| `recordedAt` | Obligatorio en `POSITION`. En `LOAD` es opcional y, si falta, se usa la hora del servidor. No puede estar más de 2 minutos en el futuro respecto al reloj del servidor; si lo está, da 400 |
| `milestone` | Obligatorio en `LOAD`: `LOADED` o `UNLOADED`. Otro valor da 400 |
| `volume`, `unit` | Opcionales en `LOAD`. Si hay `volume`, debe ser mayor que cero (si no, 400). `unit` vacío se toma como `LITRE`, y una unidad no reconocida también se toma como `LITRE` |
| `eventId` | Opcional, máximo 160 caracteres. Llave de idempotencia por entrega |

### Efectos

- **Posición:** guarda la muestra y, si es más nueva que la última, actualiza la posición del último estado. Publica el evento `delivery.telemetry.received.v1`.
- **Carga:** guarda la muestra y, si es más nueva que el último hito, actualiza el estado de carga. No publica ese evento.
- **Muestra tardía:** si su `recordedAt` no es estrictamente posterior al último de su tipo, se acepta (201), se conserva como evidencia y responde `latestAdvanced: false`. El último estado no cambia.
- **Secuencia de carga:** `UNLOADED` antes de que la entrega haya tenido un `LOADED` da 422. Solo se valida la secuencia en la muestra que avanzaría el último estado: una muestra de carga tardía no se rechaza aunque rompa la secuencia.
- **Reintento con `eventId`:** si la entrega ya tiene una muestra con ese `eventId`, responde 200 con la confirmación original, sin guardar otra muestra ni publicar otro evento. Un duplicado concurrente que choca con la restricción única también termina en 200 con la confirmación ya guardada.

### Respuesta

```json
{"evidenceId":120,"deliveryId":31,"kind":"POSITION","milestone":null,"latestAdvanced":true,"recordedAt":"2026-09-01T10:00:00Z"}
```

`milestone` solo se informa en las evidencias de carga.

| Código | Cuándo |
|---|---|
| 201 | Evidencia registrada (`latestAdvanced` indica si movió el último estado) |
| 200 | El `eventId` ya estaba registrado para la entrega; se devuelve la confirmación original |
| 400 | Cuerpo inválido: `type` o `milestone` desconocido, coordenadas ausentes o fuera de rango, `recordedAt` ausente en posición o demasiado adelantado, volumen no positivo |
| 401 | Sin autenticar (regla general de seguridad de la aplicación) |
| 403 | El usuario no es el conductor asignado, o la asignación cruza distribuidores, o no hay identidad de usuario |
| 404 | La entrega o el conductor asignado no existen |
| 409 | Dos escrituras actualizaron el último estado a la vez (`DELIVERYTRACKING_CONFLICT`); el cliente puede reintentar. No figura en el Swagger |
| 422 | Hito de carga imposible, por ejemplo descargar antes de cargar (`BUSINESS_RULE_VIOLATION`) |
