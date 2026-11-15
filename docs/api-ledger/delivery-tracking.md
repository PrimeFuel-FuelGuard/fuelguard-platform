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
