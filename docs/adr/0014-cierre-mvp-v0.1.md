# ADR-0014: Cierre del MVP como v0.1.0 y puntuación con IA en la v0.2.0

**Estado:** Aceptado · **Fecha:** 2026-11-24 · **Modifica:** constitución VIII (v4.0.0), ADR-0013

## Contexto

El ADR-0013 fijó el MVP para el **2026-11-23** con dos bloques: la spec 001 (ofertas reales de Jobicy y listado) y la puntuación de encaje con IA. También previó un **plan B**: si la IA no llegaba a tiempo, el MVP se cerraría con la spec 001 y la IA se entregaría justo después.

A 2026-11-24:

- La **spec 001 está implementada y fusionada en `main`** (PR #5): conector de Jobicy, ingesta programada e idempotente, API paginada y listado en tarjetas, con CI en verde. Se cerró **un día después** de la fecha prevista.
- La **puntuación con IA no está empezada**: falta su spec, la elección de proveedor y modelo (constitución V.2, que exige un ADR) y la implementación.

Hasta ahora el proyecto no tenía versiones: no había forma de señalar qué estado del repositorio corresponde al MVP.

## Opciones consideradas

1. **Aplicar el plan B del ADR-0013:** cerrar el MVP ya como **v0.1.0** con la spec 001 y llevar la puntuación con IA a una **v0.2.0** con fecha objetivo propia.
2. **Mantener el MVP abierto hasta terminar la IA:** el MVP seguiría sin cerrarse y la fecha original ya ha pasado; no habría ningún estado "terminado" que enseñar.
3. **Implementar la IA deprisa para cerrar el MVP completo:** contradice el flujo SDD (spec, ADR de proveedor, plan) y el principio "se recorta alcance antes que mover la fecha".

## Decisión

**Opción 1.**

- **v0.1.0 — MVP**, cerrado el 2026-11-24: un conector (Jobicy) y listado de ofertas (spec 001).
- **v0.2.0**, fecha objetivo **2026-12-01**: puntuación de encaje con IA, con explicación, y orden del listado por encaje.
- **Fase 2**, después de la v0.2.0: conector de InfoJobs y otros, deduplicación entre fuentes (III.4), filtros y búsqueda.
- **Versionado:** cada versión se marca con un tag `vX.Y.Z` en `main` y una release en GitHub que resume lo que incluye.

## Consecuencias

- **Positivas:** hay un estado del proyecto cerrado, funcional y enseñable (v0.1.0). La IA conserva una fecha concreta y se hace con el flujo completo (spec y ADR de proveedor). Los tags y releases dan un historial claro para el portfolio.
- **Negativas:** el MVP llega sin la funcionalidad que da sentido al proyecto (la puntuación con IA), y se cerró un día tarde respecto a la fecha del ADR-0013.
- **Constitución:** sube a **v4.0.0** (se redefine el principio VIII).
- **ADR-0013:** su alcance del MVP queda sustituido por este ADR; el reparto de la fase 2 se mantiene.
- **Pendiente:** crear el tag `v0.1.0` y su release en GitHub tras fusionar este cambio.
