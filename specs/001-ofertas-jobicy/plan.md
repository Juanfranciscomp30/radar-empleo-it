# Plan 001: Ofertas reales de Jobicy

**Estado:** Aprobado · **Fecha:** 2026-11-11 · **Spec:** `specs/001-ofertas-jobicy/spec.md` · **ADRs:** 0003, 0010, 0011, 0012

Este documento describe **cómo** se construye la spec 001. No añade requisitos: todo sirve para cumplir algún criterio de aceptación (CA) de la spec.

## 1. Visión general

```
Jobicy API ──► JobicyConector ──► IngestaService ──► PostgreSQL (tabla ofertas)
                    ▲                   ▲                    │
         (implementa Conector)   @Scheduled / POST           ▼
                                                    OfertaController (GET /api/ofertas)
                                                             │
                                          nginx /api ──► Angular: listado de tarjetas
```

## 2. Backend

### 2.1 Paquetes (`com.juanfran.radar`)

| Paquete | Contenido |
|---|---|
| `ofertas` | Entidad `Oferta`, `OfertaRepository`, `OfertaService`, `OfertaController` y DTOs de la API |
| `conectores` | Interfaz `Conector` y el modelo intermedio `OfertaNormalizada` |
| `conectores.jobicy` | `JobicyConector`, DTOs de la respuesta de Jobicy y su configuración |
| `ingesta` | `IngestaService`, `IngestaScheduler` e `IngestaController` (manual) |

### 2.2 Arquitectura de conectores (constitución III, CA-1, CA-10)

- **`Conector`** (interfaz): `String fuente()` y `List<OfertaNormalizada> descargar()`.
- **`OfertaNormalizada`** (record): el modelo común de CA-1. Cada conector traduce sus datos a este formato; el resto de la app no sabe de qué fuente viene una oferta.
- Cada conector se registra como bean **solo si está habilitado**: `@ConditionalOnProperty(radar.conectores.jobicy.habilitado)` (CA-10).

### 2.3 Modelo de datos y migración (ADR-0010, ADR-0011)

`V1__crear_tabla_ofertas.sql`:

| Columna | Tipo | Notas |
|---|---|---|
| `id` | `bigserial` | Clave primaria |
| `fuente` | `varchar(50)` not null | p. ej. `jobicy` |
| `id_externo` | `varchar(100)` not null | Identificador en la fuente |
| `titulo` | `text` not null | |
| `empresa` | `text` | |
| `ubicacion` | `text` | Región o "Cualquier ubicación" |
| `fecha_publicacion` | `timestamptz` not null | |
| `url` | `text` not null | Oferta original |
| `tipo_jornada` | `text` | Opcional |
| `nivel` | `text` | Opcional (útil para filtrar junior más adelante) |
| `salario_min`, `salario_max` | `numeric` | Opcional |
| `moneda` | `varchar(10)` | Opcional |
| `descripcion` | `text` | Opcional; la usará la IA. No se muestra en esta spec |
| `creada_en` | `timestamptz` not null default `now()` | Cuándo la guardamos |

- **Restricción única** `(fuente, id_externo)` → idempotencia (CA-4).
- **Índice** en `fecha_publicacion desc` → listado ordenado (CA-6).

### 2.4 Persistencia (ADR-0010)

- Entidad `Oferta` sin relaciones.
- `OfertaRepository extends JpaRepository<Oferta, Long>` con una consulta nativa `insertarSiNoExiste(...)` (`INSERT … ON CONFLICT (fuente, id_externo) DO NOTHING`) que devuelve si se insertó.
- `spring.jpa.hibernate.ddl-auto=validate` y `spring.jpa.open-in-view=false`.

### 2.5 Conector de Jobicy (CA-2, CA-5, spec "Fuente")

- Cliente HTTP: **`RestClient`** de Spring (síncrono, sin dependencias nuevas), con timeouts (conexión 5 s, lectura 15 s).
- **Una sola llamada por ingesta**: `GET /api/v2/remote-jobs?count=100&industry=<slug de desarrollo>`.
- Filtro de región **en local**: se quedan las ofertas cuyo campo de región incluya España, Europa, EMEA o "cualquier ubicación" (CA-2). Así se hace 1 llamada por ingesta, no una por región.
- Los **slugs y nombres de campos exactos** se comprueban una vez contra la API real (taxonomía `?get=industries` y `?get=locations`) al implementar, y la respuesta real se guarda como *fixture* de tests.
- Mapeo de la respuesta de Jobicy a `OfertaNormalizada`; ofertas sin `id`, `url` o título se descartan y se registran en el log.
- Configuración (`application.yml`, con valores por defecto):
  - `radar.conectores.jobicy.habilitado=true`
  - `radar.conectores.jobicy.url=https://jobicy.com/api/v2/remote-jobs`
  - `radar.conectores.jobicy.cantidad=100`

### 2.6 Ingesta (ADR-0012, CA-3, CA-4, CA-5)

- **`IngestaService.ejecutar()`**: recorre todos los `Conector` registrados; para cada uno, `descargar()` dentro de `try/catch`; guarda con `insertarSiNoExiste`; devuelve un resumen por fuente (`nuevas`, `existentes`, `error`). El fallo de un conector se registra y **no** detiene a los demás ni la aplicación (CA-5).
- **`IngestaScheduler`**: `@Scheduled(cron = "${radar.ingesta.cron}")`, por defecto cada 6 horas; y ejecución al arrancar si `radar.ingesta.al-arrancar=true` (por defecto `true`). Requiere `@EnableScheduling`.
- **`IngestaController`**: `POST /api/ingestas` → `202` con el resumen. Solo existe si `radar.ingesta.manual.habilitada=true` (por defecto `false`); si no, devuelve `404`.

### 2.7 API de lectura (CA-6)

- `GET /api/ofertas?pagina=0&tamano=20` (tamaño máximo 50), ordenado por `fecha_publicacion` descendente.
- Respuesta con DTO propio, sin serializar `Page` de Spring ni la entidad:

```json
{
  "contenido": [
    { "id": 1, "titulo": "...", "empresa": "...", "ubicacion": "...",
      "fechaPublicacion": "2026-11-10T09:00:00Z", "url": "https://...",
      "fuente": "jobicy", "tipoJornada": "...", "nivel": "..." }
  ],
  "pagina": 0, "tamano": 20, "totalElementos": 57, "totalPaginas": 3
}
```

- La descripción **no** se incluye en el listado (no se muestra y reduce el tamaño de la respuesta).

## 3. Frontend (CA-7, CA-8, CA-9)

- **`OfertasService`**: `GET /api/ofertas` con `pagina` y `tamano`.
- **Componente de listado** con tres estados claros: cargando, **vacío** ("Todavía no hay ofertas…") y **error** ("No se ha podido conectar con el servidor") (CA-8).
- **Tarjetas** en una rejilla adaptable (1 columna en móvil, 2–3 en escritorio): título, empresa, ubicación, antigüedad y botón **"Ver oferta"** con `target="_blank" rel="noopener noreferrer"` (CA-7).
- **Antigüedad relativa** ("hace 2 días") con `Intl.RelativeTimeFormat('es')`, sin librerías, en una función pura testeable.
- **Paginación** sencilla: botones "Anterior" y "Siguiente".
- **Cabecera** con el título "Radar de Empleo IT" y el indicador de estado del backend de la spec 000 (CA-8).
- **Pie** con la atribución: "Ofertas remotas de [Jobicy](https://jobicy.com)" (CA-9).
- **Estilos:** CSS propio con variables (colores, espaciados, radios). **Sin librerías de componentes** (añadir una exigiría un ADR). Diseño sencillo, limpio y legible.
- **Seguridad:** nunca se inserta HTML de la fuente en la página (la descripción no se muestra; si algún día se muestra, se usará el saneado por defecto de Angular, nunca `bypassSecurityTrust…`).

## 4. Docker, configuración y CI

- Sin servicios nuevos. Flyway aplica la migración al arrancar el backend.
- `docker-compose.yml` pasa al backend `RADAR_INGESTA_MANUAL_HABILITADA` desde `.env` (por defecto `false`).
- `.env.example` documenta la nueva variable (CA-8 de la spec 000).
- CI: sin cambios; los tests nuevos se ejecutan en los jobs existentes.

## 5. Tests (CA-11)

**Backend:**
- `JobicyConector` con `MockRestServiceServer` y el *fixture* real: mapeo de campos, filtro de región y descarte de ofertas incompletas. **Sin llamadas a internet.**
- `JobicyConector` ante error HTTP, timeout y JSON inválido.
- `IngestaService` con Mockito: un conector que falla no impide guardar los de otro conector.
- `OfertaRepository` con Testcontainers: insertar la misma oferta dos veces deja **una** fila (CA-4).
- `OfertaController` (integración con Testcontainers): orden descendente y paginación (CA-6).
- `IngestaController`: `404` con el flag desactivado.

**Frontend:**
- `OfertasService` con `HttpTestingController`.
- Componente de listado: tarjetas, estado vacío, estado de error, enlace con `target` y `rel` correctos, atribución visible.
- Función de antigüedad relativa (minutos, horas, días).

## 6. Trazabilidad: criterios → plan

| CA | Dónde se cumple |
|---|---|
| CA-1 | §2.2 `OfertaNormalizada`, §2.3 tabla |
| CA-2 | §2.5 filtro de industria y región |
| CA-3 | §2.6 scheduler, al arrancar y endpoint manual |
| CA-4 | §2.3 restricción única, §2.4 `ON CONFLICT DO NOTHING` |
| CA-5 | §2.5 timeouts, §2.6 `try/catch` por conector |
| CA-6 | §2.7 API paginada |
| CA-7 | §3 tarjetas |
| CA-8 | §3 estados vacío y error, cabecera |
| CA-9 | §3 pie de atribución y enlace original |
| CA-10 | §2.2 `@ConditionalOnProperty` |
| CA-11 | §5 tests |

## 7. Riesgos

- **Cambios en la API de Jobicy** (campos o slugs): se detectan al comparar con el *fixture*; los slugs se leen de la taxonomía oficial.
- **Pocas ofertas para España/Europa** en algunos días: es una limitación de la fuente, no un fallo (ADR-0003 ya lo contempla).
- **HTML en la descripción:** se guarda tal cual pero no se muestra en esta spec (§3, seguridad).
- **Zonas horarias:** fechas en `timestamptz` (UTC) y conversión a hora local solo en el frontend.

## Historial

- **2026-11-11:** versión inicial.
- **2026-11-11:** T1 — dependencias de Spring Boot 4: `spring-boot-starter-data-jpa`, `spring-boot-starter-flyway`, `flyway-database-postgresql` (sustituye a `spring-boot-starter-jdbc`).
- **2026-11-11:** T2 — `insertarSiNoExiste` devuelve `int` (1 = insertada, 0 = ya existía), porque Spring Data solo admite `void`/`int` en consultas `@Modifying`. Los tests de repositorio usan `@DataJpaTest` con Testcontainers (`spring-boot-starter-data-jpa-test` sustituye a `spring-boot-starter-jdbc-test`).
