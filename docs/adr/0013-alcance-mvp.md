# ADR-0013: Alcance del MVP recortado para mantener la fecha

**Estado:** Aceptado · **Fecha:** 2026-11-16 · **Modifica:** constitución VIII (v3.0.0), ADR-0003

## Contexto

La constitución (VIII) fija el MVP para el **2026-11-23** e incluía: 2–3 conectores, puntuación con IA y listado con filtros y orden por encaje. A 2026-11-16 está terminada la spec 000 y la spec 001 va por la T2 de 13 (T0–T12).

Estimación del trabajo pendiente (basada en el ritmo real de las specs 000 y 001):

| Bloque | Esfuerzo estimado |
|---|---|
| Spec 001 (T3–T12) | 2–3 sesiones |
| Spec de puntuación con IA | 3–4 sesiones (spec, decisiones y ADR de proveedor, implementación) |
| Conector InfoJobs + deduplicación entre fuentes | 2–3 sesiones |
| Filtros del listado | 1–2 sesiones |

Todo el alcance no cabe en una semana. La propia constitución establece: "ante la duda, se recorta alcance antes que mover la fecha".

## Opciones consideradas

1. **Recortar el alcance:** MVP = spec 001 (Jobicy + listado) + puntuación con IA. InfoJobs, deduplicación entre fuentes y filtros pasan a una fase 2.
2. **Mover la fecha:** mantener todo el alcance; contradice el principio VIII.
3. **Mantener alcance y fecha:** poco realista y deja menos margen para entender lo que se construye.

## Decisión

**Opción 1.** El MVP del 2026-11-23 incluye:

- **Spec 001:** ofertas reales de Jobicy, guardadas y mostradas en un listado.
- **Puntuación de encaje con IA** de cada oferta, con explicación, y orden del listado por encaje.

Pasan a **fase 2** (después del MVP): conector de InfoJobs, deduplicación entre fuentes (constitución III.4), filtros y búsqueda en el listado, y Adzuna/Remotive si hicieran falta.

**Plan B:** si la puntuación con IA no llega a tiempo, el MVP se cierra con la spec 001 y la IA se entrega justo después, sin mover la fecha de cierre del MVP.

## Consecuencias

- **Positivas:** fecha realista sin renunciar a la funcionalidad que da sentido al proyecto (la puntuación con IA); el MVP se puede enseñar en el portfolio con ofertas reales.
- **Negativas:** en el MVP solo hay ofertas remotas de Jobicy; la cobertura de ofertas presenciales o híbridas en España llega en la fase 2 con InfoJobs.
- **Constitución:** sube a **v3.0.0** (se redefine el principio VIII).
- **ADR-0003:** el calendario de conectores queda actualizado: InfoJobs pasa a la fase 2.
