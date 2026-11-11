# Tareas 001: Ofertas reales de Jobicy

**Estado:** Aprobado · **Fecha:** 2026-11-11 · **Spec:** `spec.md` · **Plan:** `plan.md` · **Rama:** `spec/001-ofertas-jobicy`

## Cómo se trabaja con este fichero

1. Una tarea cada vez, en orden, con la skill **`/tarea 001 TX`** (ADR-0007). Las tareas **[YO]** las hago yo; la skill me explica los pasos.
2. Después de cada tarea: reviso, ejecuto la **comprobación**, y si todo está bien confirmo el **commit** con el mensaje indicado.
3. Si una comprobación falla, no paso a la siguiente tarea hasta resolverlo.
4. Al terminar todas las tareas, cierro la spec con **`/pr`** (ADR-0009).

---

## Preparación

- [x] **T0 [YO] Comprobar el entorno**
  - Qué: estar en la rama `spec/001-ofertas-jobicy`, con `main` actualizado y Docker Desktop arrancado.
  - Comprobación: `git branch --show-current` muestra la rama; `docker run hello-world` funciona.
  - Commit: ninguno.

## Backend: datos

- [x] **T1 [IA] Dependencias, configuración y migración de la tabla `ofertas`**
  - Qué: añadir JPA y Flyway (ADR-0010, ADR-0011), configurar `ddl-auto=validate` y `open-in-view=false`, y crear `V1__crear_tabla_ofertas.sql` según plan §2.3 (restricción única e índice incluidos).
  - Comprobación:
    1. `.\mvnw verify` pasa (el test de salud sigue en verde con Flyway aplicando la migración en Testcontainers).
    2. `docker compose up --build -d db backend` y `docker compose exec db psql -U radar -d radar -c "\d ofertas"` muestra la tabla con su restricción única.
  - Commit: `feat(backend): tabla de ofertas con Flyway y JPA`
  - Cubre: CA-1, CA-4

- [x] **T2 [IA] Entidad `Oferta` y repositorio idempotente**
  - Qué: entidad `Oferta` sin relaciones y `OfertaRepository` con `insertarSiNoExiste` (`ON CONFLICT DO NOTHING`), según plan §2.4. Test con Testcontainers: insertar la misma oferta dos veces deja una sola fila.
  - Comprobación: `.\mvnw verify` pasa. Ver el test fallar quitando la restricción única de una copia temporal de la migración (y deshacerlo).
  - Commit: `feat(backend): entidad Oferta y repositorio idempotente`
  - Cubre: CA-1, CA-4, CA-11

## Backend: conectores e ingesta

- [ ] **T3 [IA] Arquitectura de conectores e `IngestaService`**
  - Qué: interfaz `Conector`, record `OfertaNormalizada` e `IngestaService` con `try/catch` por conector y resumen por fuente, según plan §2.2 y §2.6. Test con Mockito: un conector que falla no impide guardar las ofertas de otro.
  - Comprobación: `.\mvnw verify` pasa.
  - Commit: `feat(backend): arquitectura de conectores e IngestaService`
  - Cubre: CA-1, CA-5, CA-11

- [ ] **T4 [IA] Capturar la respuesta real de Jobicy como fixture**
  - Qué: consultar **una vez** la taxonomía (`?get=industries` y `?get=locations`) y el listado de ofertas de la API real, guardar las respuestas en `backend/src/test/resources/fixtures/jobicy/` y anotar en el plan (§2.5 e Historial) el slug de industria y los valores de región que se usarán. Son las únicas llamadas reales a Jobicy durante el desarrollo.
  - Comprobación: los ficheros de fixture existen y el plan indica los slugs elegidos.
  - Commit: `test(backend): fixtures reales de la API de Jobicy`
  - Cubre: CA-2, CA-11

- [ ] **T5 [IA] Conector de Jobicy**
  - Qué: `JobicyConector` con `RestClient`, timeouts, una llamada por ingesta, filtro de región en local, mapeo a `OfertaNormalizada`, descarte de ofertas incompletas y activación por configuración, según plan §2.5. Tests con `MockRestServiceServer` y el fixture: mapeo, filtro de región, descarte, error HTTP, timeout y JSON inválido.
  - Comprobación: `.\mvnw verify` pasa sin conexión a internet.
  - Commit: `feat(backend): conector de Jobicy`
  - Cubre: CA-1, CA-2, CA-5, CA-10, CA-11

- [ ] **T6 [IA] Ingesta programada, al arrancar y manual**
  - Qué: `IngestaScheduler` (cron configurable y ejecución al arrancar) e `IngestaController` tras el flag, según plan §2.6 y ADR-0012. Variable `RADAR_INGESTA_MANUAL_HABILITADA` en `docker-compose.yml` y `.env.example`. Test: `404` con el flag desactivado.
  - Comprobación:
    1. `docker compose up --build -d db backend` → en `docker compose logs backend` aparece el resumen de la ingesta al arrancar.
    2. `docker compose exec db psql -U radar -d radar -c "select count(*) from ofertas"` → más de 0.
    3. `POST http://localhost:8080/api/ingestas` → `404`.
    4. Con `RADAR_INGESTA_MANUAL_HABILITADA=true` en `.env` y reiniciando el backend → `202` y el resumen indica `nuevas: 0` (idempotencia en vivo). Volver a ponerlo a `false`.
  - Commit: `feat(backend): ingesta programada y disparo manual tras flag`
  - Cubre: CA-3, CA-4, CA-11

## Backend: API

- [ ] **T7 [IA] `GET /api/ofertas` paginado**
  - Qué: `OfertaService`, `OfertaController` y DTOs de respuesta según plan §2.7. Test de integración con Testcontainers: orden descendente y paginación.
  - Comprobación: `.\mvnw verify` pasa; con el sistema levantado, `http://localhost:8080/api/ofertas?pagina=0&tamano=5` devuelve 5 ofertas, de la más reciente a la más antigua.
  - Commit: `feat(backend): API paginada de ofertas`
  - Cubre: CA-6, CA-11

## Frontend

- [ ] **T8 [IA] `OfertasService` y antigüedad relativa**
  - Qué: servicio que consume `/api/ofertas` y función pura de antigüedad relativa con `Intl.RelativeTimeFormat('es')`, según plan §3. Tests de ambos.
  - Comprobación: `npx ng test --watch=false` pasa. Ver fallar el test de antigüedad cambiando un resultado esperado (y deshacerlo).
  - Commit: `feat(frontend): servicio de ofertas y antigüedad relativa`
  - Cubre: CA-7, CA-11

- [ ] **T9 [IA] Listado de ofertas en tarjetas**
  - Qué: componente de listado con estados de carga, vacío y error; tarjetas con "Ver oferta" (`target="_blank" rel="noopener noreferrer"`); paginación Anterior/Siguiente; cabecera con el indicador de estado; pie con la atribución a Jobicy; estilos propios según plan §3. Tests del componente.
  - Comprobación:
    1. `npx ng test --watch=false` pasa.
    2. `docker compose up -d db backend` + `npm start` → `http://localhost:4200` muestra tarjetas de ofertas reales y el pie "Ofertas remotas de Jobicy".
    3. "Ver oferta" abre la oferta original en otra pestaña.
    4. `docker compose stop backend` → recargar → mensaje de error comprensible.
  - Commit: `feat(frontend): listado de ofertas en tarjetas`
  - Cubre: CA-7, CA-8, CA-9, CA-11

## Integración y documentación

- [ ] **T10 [IA] Comprobación con Docker completo y README**
  - Qué: verificar el sistema completo con `docker compose up --build` y actualizar el README: descripción del listado, nueva variable de entorno, decisiones (ADR-0010 a 0012) y roadmap.
  - Comprobación: con `docker compose up --build` (sin `npm start`), `http://localhost:4200` muestra ofertas reales; el README se sigue sin conocimientos previos.
  - Commit: `docs: README con el listado de ofertas`
  - Cubre: CA-7

## Cierre

- [ ] **T11 [YO] Capturas de pantalla**
  - Qué: hacer 1–2 capturas del listado (escritorio y, si quiero, móvil), guardarlas en `docs/img/` y enlazarlas en el README.
  - Comprobación: las imágenes se ven en el README en GitHub (después del merge).
  - Commit: `docs: capturas del listado de ofertas`

- [ ] **T12 [YO] Verificación final de la spec**
  - Qué: recorrer CA-1 a CA-11 de `spec.md` y comprobar que se cumplen; cambiar el estado de `spec.md` a **Implementada**. Después, cerrar la spec con **`/pr`**.
  - Comprobación: todos los criterios cumplidos; el revisor da APTA (o APTA CON OBSERVACIONES aceptadas), CI en verde y merge hecho.
  - Commit: `docs: spec 001 implementada`
