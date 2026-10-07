# FuelGuard Platform Backend

API de la plataforma FuelGuard con Spring Boot 4.0.6 y Java 26, diseñada según las especificaciones del Trabajo Final y TB2 para la digitalización logística B2B de hidrocarburos con integración IoT.

## Requisitos

- JDK 26
- MySQL 8
- Maven Wrapper incluido en el repositorio
- Docker (opcional, solo para construir la imagen)

## Configuración local

Configura estas dos variables antes de iniciar la aplicación (o usa los valores predeterminados en `application-dev.properties`):

- `AUTHORIZATION_JWT_SECRET`: secreto aleatorio de al menos 32 bytes. Spring lo carga con `Keys.hmacShaKeyFor`.
- `DATABASE_PASSWORD`: contraseña del usuario MySQL.

Las demás variables tienen valores predeterminados para desarrollo: `DATABASE_URL=localhost`, `DATABASE_PORT=3306`, `DATABASE_NAME=fuelguard_platform`, `DATABASE_USER=root` y `PORT=8080`.

En PowerShell:

```powershell
$env:JAVA_HOME = 'C:\ruta\a\jdk-26'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
$env:AUTHORIZATION_JWT_SECRET = 'reemplazar-por-un-secreto-aleatorio-de-32-bytes-minimo'
$env:DATABASE_PASSWORD = 'contraseña-local'
./mvnw.cmd spring-boot:run
```

Swagger UI está en [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) y el documento OpenAPI en `/api-docs`.

## Despliegue (producción)

El backend se despliega en **Google Cloud Run** como contenedor Docker, usando el `Dockerfile` del repositorio. La base de datos es un **MySQL 8 instalado en una máquina virtual de Microsoft Azure**. El frontend está en Firebase Hosting y consume la API por HTTPS.

Activa el perfil `prod` con `SPRING_PROFILES_ACTIVE=prod` y entrega estas variables de entorno. **No las escribas en el repositorio**:

| Variable | Descripción |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `prod` |
| `DATABASE_URL` | Host o IP del servidor MySQL |
| `DATABASE_PORT` | Puerto de MySQL (3306 por defecto) |
| `DATABASE_NAME` | Nombre de la base (`fuelguard_platform`) |
| `DATABASE_USER` | Usuario de la aplicación |
| `DATABASE_PASSWORD` | Contraseña del usuario |
| `AUTHORIZATION_JWT_SECRET` | Secreto aleatorio del JWT (mínimo 32 bytes) |
| `SERVER_URL` | URL pública del servicio, usada por Swagger UI |
| `CORS_ALLOWED_ORIGINS` | Orígenes del frontend, separados por comas |

El perfil `prod` se conecta a MySQL con `sslMode=REQUIRED`, por lo que el usuario de la base debe aceptar conexiones cifradas.

Despliegue desde Google Cloud Shell, en la carpeta del proyecto (Cloud Build construye la imagen con el `Dockerfile`):

```bash
gcloud run deploy fuelguard \
  --source . \
  --region us-central1 \
  --allow-unauthenticated \
  --memory 1Gi --cpu 1 --cpu-boost \
  --min-instances 0 --max-instances 2 \
  --set-env-vars "SPRING_PROFILES_ACTIVE=prod,DATABASE_URL=<host>,DATABASE_PORT=3306,DATABASE_NAME=fuelguard_platform,DATABASE_USER=<usuario>,DATABASE_PASSWORD=<contraseña>,AUTHORIZATION_JWT_SECRET=<secreto>"
```

Si Cloud Build responde `PERMISSION_DENIED`, concede a la cuenta de servicio de Compute del proyecto los roles `roles/cloudbuild.builds.builder`, `roles/storage.objectAdmin`, `roles/artifactregistry.writer` y `roles/logging.logWriter`.

Para cambiar variables sin volver a compilar:

```bash
gcloud run services update fuelguard --region us-central1 --update-env-vars SERVER_URL=<url-del-servicio>
```

Los valores que contienen comas (por ejemplo `CORS_ALLOWED_ORIGINS` con varios orígenes) requieren un delimitador distinto: `--update-env-vars "^@^CORS_ALLOWED_ORIGINS=https://a.com@https://b.com"`.

En producción, Swagger UI queda en `<SERVER_URL>/swagger-ui.html`.

## Primer administrador

Crea el primer administrador con el procedimiento manual de `scripts/seed-first-admin.sql`. Inicia sesión de nuevo después del cambio para obtener un JWT con los roles actualizados.

El script no crea usuarios, solo asigna `ROLE_ADMIN` a uno existente: primero registra el usuario desde Swagger UI (`POST /api/authentication/sign-up`) y luego ejecuta el script con ese nombre de usuario.

## Esquema de base de datos

Flyway es responsable de los cambios de esquema (V1–V38). Hibernate usa `ddl-auto=validate`; no crea ni modifica tablas en ejecución. No edites migraciones que ya se hayan aplicado: agrega una nueva migración con el siguiente número. Las tablas `drivers` y `vehicles` se mantienen porque Fleet v2 las utiliza.

Flyway se ejecuta al iniciar la aplicación y crea las tablas cuando la base está vacía.
