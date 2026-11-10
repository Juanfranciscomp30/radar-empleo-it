# Plan 000: Esqueleto del proyecto

**Estado:** Aprobado · **Fecha:** 2026-11-09 · **Spec:** `specs/000-esqueleto/spec.md` · **ADRs:** 0004, 0005, 0006

Este documento describe **cómo** se construye la spec 000. No añade requisitos: todo lo que aquí aparece sirve para cumplir algún criterio de aceptación (CA) de la spec.

## 1. Estructura del repositorio (monorepo)

```
radar-empleo-it/
├── backend/                  # Spring Boot (Maven Wrapper)
├── frontend/                 # Angular
├── docker-compose.yml
├── .env.example
├── .github/workflows/ci.yml
├── docs/
├── specs/
└── README.md
```

## 2. Backend

- **Tecnología:** Java 25, Spring Boot 4.1.x, Maven Wrapper (`./mvnw`). Generado con Spring Initializr.
- **Paquete base:** `com.juanfran.radar`.
- **Dependencias:**
  - `spring-boot-starter-webmvc`: servidor HTTP. En Spring Boot 4 sustituye al antiguo `spring-boot-starter-web`.
  - `spring-boot-starter-actuator`: endpoint de salud.
  - `spring-boot-starter-jdbc` + driver `postgresql`: conexión a la base de datos. La elección de JPA u otra capa de persistencia se decide en la spec 001.
  - Test: starters de test por módulo de Spring Boot 4 (p. ej. `spring-boot-starter-actuator-test`), Testcontainers (PostgreSQL) y su integración con Spring Boot.
- **Configuración** (`application.yml`, valores sensibles desde variables de entorno):
  - Datasource leído de `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` y `SPRING_DATASOURCE_PASSWORD`.
  - Actuator (cumple CA-2 y CA-3):
    - Ruta base de los endpoints de gestión: `/api` → el endpoint de salud queda en `/api/health`.
    - **Solo se expone `health`.** Ningún otro endpoint de Actuator es accesible.
    - Se muestran los componentes (estado de `db`) pero no los detalles internos.
    - Si la base de datos falla, Actuator responde `503` con estado `DOWN` (comportamiento por defecto).
- **No se escribe controlador propio** para la salud: lo resuelve Actuator.

## 3. Frontend

- **Tecnología:** Angular 22.x, componentes standalone, generado con Angular CLI.
- **`HealthService`:** hace `GET /api/health` y devuelve `true` si la respuesta es correcta y `false` ante cualquier error (backend caído, 503…).
- **Componente principal:** muestra el título "Radar de Empleo IT" y el texto "Conectado" o "Sin conexión" según el `HealthService` (cumple CA-4). Sin diseño visual.
- **Llamadas a la API con ruta relativa** (`/api/...`): nunca se escribe la URL del backend en el código.
- **Desarrollo local (`npm start`):** `proxy.conf.json` redirige `/api` a `http://localhost:8080` (backend en Docker). Ver ADR-0006.

## 4. Docker

### 4.1 Imágenes

- **Backend:** Dockerfile multi-etapa. Etapa 1: JDK 25 compila con `./mvnw package -DskipTests` (los tests corren en `./mvnw verify` en local y en CI, porque Testcontainers necesita Docker y no está disponible dentro de la build). Etapa 2: solo JRE 25 con el `.jar`.
- **Frontend:** Dockerfile multi-etapa (ADR-0006). Etapa 1: Node (LTS compatible con Angular 22) ejecuta el build de producción. Etapa 2: nginx sirve los estáticos.
- **nginx (`frontend/nginx.conf`):**
  - Sirve los ficheros de Angular y, ante rutas desconocidas, devuelve `index.html` (necesario en una SPA).
  - `location /api/` → `proxy_pass http://backend:8080` (por nombre de servicio, nunca `localhost`).
- Todas las imágenes base usan **etiquetas de versión concretas**, nunca `latest`, para que la build sea reproducible.

### 4.2 `docker-compose.yml`

| Servicio | Imagen | Puerto interno → PC | Depende de | Healthcheck |
|---|---|---|---|---|
| `db` | PostgreSQL (versión mayor estable actual, etiqueta fija) | 5432 → 5432 | — | `pg_isready` |
| `backend` | build de `./backend` | 8080 → 8080 | `db` sano | `GET /api/health` |
| `frontend` | build de `./frontend` | 80 → 4200 | `backend` sano | — |

- Las credenciales de la base de datos se leen de `.env`.
- El backend recibe `SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/${POSTGRES_DB}`.
- Volumen con nombre para los datos de PostgreSQL (no se pierden al parar los contenedores).

### 4.3 Variables de entorno (`.env.example`, CA-8)

```
POSTGRES_DB=radar
POSTGRES_USER=radar
POSTGRES_PASSWORD=cambia-esto
```

`.env` ya está en `.gitignore`.

## 5. Tests

- **Backend (CA-5):** test de integración `@SpringBootTest` que arranca PostgreSQL con Testcontainers y comprueba que `GET /api/health` responde `200` con estado `UP`. Se ejecuta con `./mvnw verify`.
- **Frontend (CA-6):** test del `HealthService` con `HttpTestingController`: un caso con respuesta correcta (`true`) y otro con error (`false`). Runner por defecto de Angular 22.
- **CA-3 (BD caída):** verificación manual en esta spec (parar el contenedor `db` y comprobar que `/api/health` devuelve `503`). Es comportamiento propio de Actuator, no código nuestro.

## 6. CI: GitHub Actions (`.github/workflows/ci.yml`, CA-7)

- **Se dispara en:** `push` a `main` y en cada `pull_request`.
- **Job `backend`:** `ubuntu-latest`, Java 25 (Temurin) con caché de Maven, `./mvnw verify` (incluye el test con Testcontainers; Docker ya está disponible en el runner).
- **Job `frontend`:** Node LTS con caché de npm, `npm ci`, tests en modo no interactivo y `npm run build`.
- **Job `docker`:** `docker compose build` para comprobar que las imágenes se construyen.
- Si cualquier job falla, el workflow falla.

## 7. README (CA-9)

Requisitos (Docker Desktop), pasos para arrancar (`copy .env.example .env`, `docker compose up`), URLs (`localhost:4200`, `localhost:8080/api/health`) y cómo desarrollar el frontend con `npm start`.

## 8. Trazabilidad: criterios → plan

| CA | Dónde se cumple |
|---|---|
| CA-1 | §4 Docker |
| CA-2 | §2 Actuator, §4.2 |
| CA-3 | §2 Actuator, §5 verificación manual |
| CA-4 | §3 Frontend, §4.1 nginx |
| CA-5 | §5 Tests backend |
| CA-6 | §5 Tests frontend |
| CA-7 | §6 CI |
| CA-8 | §4.3 Variables de entorno |
| CA-9 | §7 README |

## 9. Riesgos

- **Versiones muy recientes (Spring Boot 4, Angular 22):** puede haber menos ejemplos; ante dudas, consultar la documentación oficial.
- **Memoria del portátil:** Docker Desktop + 3 contenedores + IDE consumen bastante; cerrar lo que no se use.
- **Healthcheck del backend:** la herramienta usada para llamar a `/api/health` debe existir en la imagen final (comprobar al implementar).

## Historial

- **2026-11-09:** corrección durante T1: nombres de starters de Spring Boot 4 (`webmvc` y starters de test por módulo). Sin cambio de alcance.
- **2026-11-10:** corrección durante T4: la etapa de build del Dockerfile compila con `-DskipTests` (los tests se ejecutan con `verify` en local y CI). Sin cambio de alcance.
