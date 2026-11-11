# Spec 001: Ofertas reales de Jobicy, de la fuente a la pantalla

**Estado:** Borrador · **Fecha:** 2026-11-11 · **Relacionado:** constitución v2.0.0 (III, IV, VI, VIII), ADR-0002, ADR-0003

## Objetivo

Tener la primera funcionalidad completa del radar: recoger ofertas IT reales de Jobicy, guardarlas normalizadas y mostrarlas en la web en un listado claro, con enlace a la oferta original. Es el primer conector y valida la arquitectura de conectores (constitución III).

## Historias de usuario

- **HU-1:** Como usuario, quiero que la app recoja automáticamente ofertas IT de Jobicy, para no tener que buscarlas a mano.
- **HU-2:** Como usuario, quiero ver las ofertas en un listado ordenado de la más reciente a la más antigua, para revisar primero lo nuevo.
- **HU-3:** Como usuario, quiero abrir la oferta original desde el listado, para leerla completa y aplicar en su web.
- **HU-4:** Como usuario, quiero que la misma oferta no aparezca repetida aunque la app la descargue varias veces.
- **HU-5:** Como usuario, quiero que la app siga funcionando y mostrando lo ya guardado aunque Jobicy falle o no responda.

## Criterios de aceptación

| ID | Criterio | Historia |
|---|---|---|
| CA-1 | Cada oferta se guarda en la base de datos con un **modelo normalizado** común a todas las fuentes: fuente, identificador en la fuente, título, empresa, ubicación o región, fecha de publicación, URL original, y, si la fuente los da, tipo de jornada, nivel de experiencia, salario y descripción. | HU-1 |
| CA-2 | La ingesta de Jobicy solo trae ofertas de **desarrollo de software / IT** que estén **disponibles para candidatos en España** (por región o por estar abiertas a cualquier ubicación). | HU-1 |
| CA-3 | La ingesta se ejecuta **automáticamente** con una frecuencia que respeta el uso justo de Jobicy (como máximo unas pocas veces al día) y también puede lanzarse **manualmente** para pruebas. | HU-1 |
| CA-4 | Ejecutar la ingesta varias veces seguidas **no crea ofertas duplicadas**: una misma oferta de la misma fuente se guarda una sola vez. | HU-4 |
| CA-5 | Si Jobicy falla (error, timeout o respuesta inválida), el backend **sigue funcionando**, registra el error en los logs y las ofertas ya guardadas se siguen mostrando. | HU-5 |
| CA-6 | `GET /api/ofertas` devuelve las ofertas guardadas, ordenadas por fecha de publicación descendente y **paginadas**. | HU-2 |
| CA-7 | La web muestra en `http://localhost:4200` un **listado de ofertas** en forma de tarjetas, con: título, empresa, ubicación, antigüedad ("hace 2 días") y un botón **"Ver oferta"** que abre la oferta original en una pestaña nueva. Diseño sencillo, limpio y legible. | HU-2, HU-3 |
| CA-8 | La web muestra un **estado vacío** comprensible si no hay ofertas, y un **mensaje de error** si el backend no responde. El indicador de estado del backend de la spec 000 se mantiene. | HU-5 |
| CA-9 | Se cumple la **atribución** que exige Jobicy: las ofertas indican que vienen de Jobicy con un enlace visible, y "Ver oferta" lleva siempre a la oferta original. | HU-3 |
| CA-10 | El conector de Jobicy se puede **activar o desactivar por configuración**, sin tocar código (constitución III.2). | HU-1 |
| CA-11 | Hay tests de: el conector con respuestas simuladas de Jobicy (sin llamar a internet), la persistencia con PostgreSQL real (Testcontainers), el endpoint `/api/ofertas` y el servicio del frontend. CI pasa en verde. | Todas |

## Fuera de alcance

- Otras fuentes (InfoJobs, Adzuna) y la deduplicación **entre** fuentes.
- Puntuación de encaje con IA.
- Filtros y búsqueda en el listado (spec posterior).
- Identidad visual completa (logo, paleta propia, modo oscuro).
- Cuentas de usuario, notificaciones y despliegue en la nube.
- Guardar datos personales de reclutadores o empleados (constitución IV.4).

## Fuente: condiciones de uso (constitución IV.2)

| Aspecto | Detalle |
|---|---|
| Tipo de acceso | API pública JSON, sin autenticación: `GET https://jobicy.com/api/v2/remote-jobs` |
| Condiciones revisadas | **Pendiente:** enlace a las condiciones oficiales en jobicy.com/jobs-rss-feed, revisado por mí antes de aprobar esta spec |
| Límites de uso | Consultar unas pocas veces al día; el uso excesivo puede restringir el acceso |
| Atribución | Citar a Jobicy con enlace y que "aplicar" lleve a la oferta original |
| Restricciones conocidas | No redistribuir las ofertas en otros agregadores (uso personal: cumple) |

## Restricciones

- Cumple la constitución: principios III (conectores), IV (fuentes legales), V.6 no aplica todavía (sin IA), VI (tests) y VII (sin secretos).
- Las decisiones técnicas (capa de persistencia, migraciones, cómo se programa la ingesta, filtros exactos de Jobicy) se toman en el plan y, si procede, en ADRs.

## Pasos manuales (los hago yo, no la IA)

- Leer las condiciones oficiales de Jobicy y completar la fila "Condiciones revisadas" antes de aprobar la spec.
- Hacer las capturas de pantalla del listado para el README y el post de LinkedIn.

## Preguntas abiertas

- Ninguna.