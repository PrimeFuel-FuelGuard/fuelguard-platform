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
