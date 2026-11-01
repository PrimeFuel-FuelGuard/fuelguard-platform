# Pedidos B2B del distribuidor: listar, aceptar y rechazar

T09 (TS-15). Tres endpoints sobre `/api/replenishment-requests` permiten al distribuidor decidir las pre-órdenes que le llegan. Todos devuelven `ReplenishmentRequestResource` (los valores del ejemplo son ilustrativos):

```json
{"id":12,"organizationId":6,"customerAccountId":7,"tankId":23,"providerId":4,"fuelProductId":5,"quantity":100.0,"unit":"LITRE","unitPrice":4.25,"status":"PENDING","source":"MANUAL","rejectionReason":null,"orderId":null,"deliveryAddress":"Av. Prueba 123","deliveryDate":"2026-11-10","version":0}
```

| Endpoint | Controller | Resultado |
|---|---|---|
| `GET /api/replenishment-requests/inbox` | `ReplenishmentRequestsController` | Lista |
| `POST /api/replenishment-requests/{requestId}/accept` | `ReplenishmentAcceptanceController` (módulo `applicationflows`) | `PENDING` a `ACCEPTED` y se crea la orden |
| `POST /api/replenishment-requests/{requestId}/reject` | `ReplenishmentRequestsController` | `PENDING` a `REJECTED` |

`accept` vive en `applicationflows` porque coordina varios módulos (replenishment, ordering y equipment) en una sola transacción, sin que esos módulos se importen entre sí.

## Estados

`PENDING` es el único estado desde el que se puede decidir. `ACCEPTED`, `REJECTED` y `CANCELLED` son finales para este flujo. Dos decisiones en paralelo se resuelven con el campo `version` (bloqueo optimista): una gana y la otra recibe 409.

## Autorización

Solo `ROLE_PROVIDER` con identidad de distribuidor. El `providerId` sale del usuario autenticado mediante `TenantAccess`, nunca de la URL ni del cuerpo. En `accept` y `reject` el controller comprueba antes que la solicitud pertenezca a ese distribuidor.

## Inbox

`GET /inbox` devuelve todas las solicitudes dirigidas al distribuidor, de todos sus clientes y en cualquier estado, ordenadas por id descendente. Sin resultados devuelve `[]`. Sin identidad de distribuidor responde 403 (un comprador no entra aunque su principal traiga un `providerId`, porque se exige `ROLE_PROVIDER`). Sin autenticar responde 401.

## Aceptar

Sin cuerpo. En una sola transacción: comprueba que la solicitud tenga dirección y fecha de entrega, pasa a `ACCEPTED`, consume la aceptación una sola vez, busca el mapeo a la empresa del sistema anterior, crea la orden y guarda su `orderId` en la solicitud. Si cualquier paso falla se revierte todo y la solicitud sigue `PENDING`. Publica `replenishment.accepted.v1`. La respuesta incluye `orderId`, necesario después para `POST /api/deliveries`.

## Rechazar

Cuerpo obligatorio con el motivo:

```json
{"reason":"Sin stock disponible"}
```

`reason` no puede ser nulo ni estar en blanco. Guarda el motivo en `rejectionReason`, no crea orden y publica `replenishment.rejected.v1`.

## Códigos de respuesta reales

| Código | Cuándo |
|---|---|
| 200 | Decisión registrada |
| 400 | Rechazo sin `reason` o con `reason` en blanco |
| 403 | Sin identidad de distribuidor, solicitud de otro distribuidor, o `requestId` inexistente |
| 409 | La solicitud ya no está `PENDING`, se decidió en paralelo, falta dirección o fecha de entrega, o falta el mapeo heredado de la empresa (solo `accept`) |

Los errores de negocio usan el recurso compartido `{code, message, details}`. El 409 tiene `code` `REPLENISHMENTREQUEST_CONFLICT`; `message` se localiza y `details` trae la causa en inglés.

Nota sobre el 404: el Swagger de ambos endpoints declara 404 para una solicitud inexistente, pero el controller comprueba primero la propiedad del distribuidor y responde 403. Así no se revela qué ids existen. Los tests fijan este comportamiento. No se cambió el código ni la anotación en este alcance.

## Pruebas

- `ReplenishmentInboxIntegrationTest`: inbox con todos los clientes y estados, exclusión de otros distribuidores, orden y 403 para comprador.
- `ReplenishmentInboxAuthorizationIntegrationTest`: inbox con la identidad real (sin simular `TenantAccess`): lista vacía `[]`, aislamiento por distribuidor, contrato JSON completo, 403 para comprador o rol de distribuidor sin identidad, y 401 sin autenticar.
- `ReplenishmentRejectIntegrationTest`: rechazo correcto con motivo guardado, 400 por motivo vacío, otro distribuidor, sin identidad, id inexistente, doble rechazo, rechazo de una cancelada y aceptación posterior a un rechazo.
- `ReplenishmentAcceptanceIntegrationTest` y `StateLifecycleRetryCharacterizationTest`: aceptación con orden vinculada, reintento con 409 y rechazo de una ya aceptada.
- `AuthorizationContractTest`: un distribuidor ajeno recibe 403 al aceptar.

No hay migración. No se cambió ningún endpoint, resource ni snapshot de OpenAPI.
