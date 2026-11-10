# Tareas 000: Esqueleto del proyecto

**Estado:** Aprobado · **Fecha:** 2026-11-09 · **Spec:** `spec.md` · **Plan:** `plan.md`

## Cómo se trabaja con este fichero

1. Se hace **una tarea cada vez**, en orden.
2. Las tareas marcadas **[YO]** las hago yo a mano. Las marcadas **[IA]** se las pido a Claude Code con este mensaje:

   > Lee `specs/000-esqueleto/spec.md`, `plan.md` y `tasks.md`. Haz **solo la tarea TX**. No empieces la siguiente. Al terminar, explícame qué has hecho, qué ficheros has creado o cambiado y cómo lo compruebo.

3. Después de cada tarea: reviso lo que ha hecho, ejecuto la **comprobación**, y si todo está bien hago **commit** con el mensaje indicado y marco la casilla `[x]`.
4. Si una comprobación falla, no paso a la siguiente tarea hasta resolverlo.

---

## Preparación

- [x] **T0 [YO] Comprobar herramientas instaladas**
  - Qué: verificar que tengo Java 25 (JDK), Node.js (LTS compatible con Angular 22) y Docker Desktop arrancado.
  - Comprobación: `java -version` (debe decir 25), `node -v`, `npm -v`, `docker run hello-world`.
  - Commit: ninguno.

## Backend

- [x] **T1 [IA] Generar el proyecto Spring Boot**
  - Qué: crear el proyecto en `backend/` con Spring Initializr según plan §2 (Java 25, Spring Boot 4.1.x, Maven Wrapper, paquete `com.juanfran.radar`, dependencias de §2).
  - Comprobación: `cd backend` y `.\mvnw compile` termina con `BUILD SUCCESS`.
  - Commit: `feat(backend): genera proyecto Spring Boot`

- [x] **T2 [IA] Configurar datasource y Actuator**
  - Qué: `application.yml` según plan §2: datasource desde variables de entorno; Actuator con ruta base `/api`, **solo `health` expuesto**, componentes visibles sin detalles.
  - Comprobación: revisar el fichero y confirmar que ningún otro endpoint de Actuator queda expuesto y que no hay contraseñas escritas.
  - Commit: `feat(backend): configura datasource y endpoint de salud`
  - Cubre: CA-2, CA-3, CA-8

- [x] **T3 [IA] Test de integración del endpoint de salud**
  - Qué: test `@SpringBootTest` con Testcontainers (PostgreSQL) que comprueba que `GET /api/health` responde `200` con estado `UP` (plan §5).
  - Comprobación: con Docker Desktop abierto, `.\mvnw verify` pasa.
  - Commit: `test(backend): test de integración de /api/health con Testcontainers`
  - Cubre: CA-5

- [x] **T4 [IA] Dockerfile del backend**
  - Qué: Dockerfile multi-etapa según plan §4.1 (JDK 25 para compilar, JRE 25 para ejecutar, etiquetas de versión fijas).
  - Comprobación: desde la raíz, `docker build -t radar-backend ./backend` termina sin errores.
  - Commit: `build(backend): Dockerfile multi-etapa`

- [x] **T5 [IA] docker-compose con base de datos y backend**
  - Qué: `docker-compose.yml` con los servicios `db` y `backend` según plan §4.2 (healthchecks, dependencias, volumen) y `.env.example` según §4.3.
  - Comprobación:
    1. `copy .env.example .env` y cambiar la contraseña en `.env`.
    2. `docker compose up --build db backend`.
    3. Abrir `http://localhost:8080/api/health` → estado `UP`, con `db` en `UP`.
    4. En otra terminal, `docker compose stop db` → recargar: debe responder `503` / `DOWN` (CA-3).
    5. `git status` → `.env` **no** aparece.
  - Commit: `build: docker-compose con PostgreSQL y backend`
  - Cubre: CA-1 (parcial), CA-2, CA-3, CA-8

## Frontend

- [x] **T6 [IA] Generar el proyecto Angular**
  - Qué: crear el proyecto en `frontend/` con Angular CLI 22.x (standalone), según plan §3.
  - Comprobación: `cd frontend`, `npm start` y abrir `http://localhost:4200` → se ve la página por defecto de Angular.
  - Commit: `feat(frontend): genera proyecto Angular`

- [x] **T7 [IA] HealthService y su test**
  - Qué: servicio que consulta `GET /api/health` y devuelve `true`/`false`, con test de los dos casos (plan §3 y §5).
  - Comprobación: los tests del frontend pasan (comando de test del proyecto, en modo no interactivo).
  - Commit: `feat(frontend): servicio de estado del backend con tests`
  - Cubre: CA-6

- [ ] **T8 [IA] Página de estado y proxy de desarrollo**
  - Qué: componente principal con el título "Radar de Empleo IT" y "Conectado" / "Sin conexión"; `proxy.conf.json` para `npm start` (plan §3).
  - Comprobación:
    1. `docker compose up db backend` en una terminal.
    2. `npm start` en `frontend/` → `http://localhost:4200` muestra **"Conectado"**.
    3. `docker compose stop backend` → recargar → **"Sin conexión"**.
  - Commit: `feat(frontend): página de estado del backend`
  - Cubre: CA-4

- [ ] **T9 [IA] Dockerfile del frontend, nginx y servicio en compose**
  - Qué: Dockerfile multi-etapa + `nginx.conf` (SPA y proxy `/api/` → `backend:8080`) según plan §4.1, y servicio `frontend` en `docker-compose.yml` (§4.2).
  - Comprobación: `docker compose up --build` → `http://localhost:4200` muestra **"Conectado"** sin tener `npm start` arrancado.
  - Commit: `build(frontend): imagen nginx y servicio en docker-compose`
  - Cubre: CA-1, CA-4

## Integración continua y documentación

- [ ] **T10 [IA] Workflow de GitHub Actions**
  - Qué: `.github/workflows/ci.yml` con los jobs `backend`, `frontend` y `docker` según plan §6.
  - Comprobación: hacer push y, en la pestaña **Actions** de GitHub, los tres jobs terminan en verde.
  - Commit: `ci: workflow de build y tests`
  - Cubre: CA-7

- [ ] **T11 [IA] README**
  - Qué: actualizar `README.md` según plan §7.
  - Comprobación: seguir el README paso a paso como si fuera alguien nuevo; debe funcionar sin saber nada más.
  - Commit: `docs: README con instrucciones de arranque`
  - Cubre: CA-9

## Cierre

- [ ] **T12 [YO] Proteger la rama `main` en GitHub**
  - Qué: en GitHub → Settings → Branches (o Rules), exigir que el workflow de CI pase antes de hacer merge a `main`.
  - Comprobación: la regla aparece activa en la configuración del repo.
  - Commit: ninguno.

- [ ] **T13 [YO] Verificación final de la spec**
  - Qué: recorrer CA-1 a CA-9 de `spec.md` uno por uno y comprobar que se cumplen.
  - Comprobación: todos los criterios cumplidos.
  - Commit: cambiar el estado de `spec.md` a **Implementada** → `docs: spec 000 implementada`