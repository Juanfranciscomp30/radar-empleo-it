# ADR-0003: Fuentes de ofertas del MVP

**Estado:** Aceptado (revisado) · **Fecha:** 2026-11-09

## Contexto

La constitución (principio IV, ADR-0002) solo admite fuentes que permiten acceso automatizado. El MVP (principio VIII) limita a 2–3 conectores. Se evaluaron: Adzuna, InfoJobs, Jooble, Jobicy, Remotive y Arbeitnow.

Hallazgos tras intentar el registro (2026-11-09):
- **Adzuna** no es gratuita: la cuenta gratuita da 25 llamadas de prueba; el acceso real cuesta 8 £/mes (5.000 llamadas/mes, 500/día).
- **InfoJobs**: acceso al portal de desarrolladores conseguido; falta registrar la aplicación y obtener credenciales.

## Decisión

1. **Jobicy** (API/feed público sin key) es el **primer conector**: valida la arquitectura de conectores sin coste ni autenticación. Exige atribución con enlace y que "aplicar" lleve a la oferta original.
2. **InfoJobs** (API REST oficial): segundo conector, fuente principal de ofertas en España.
3. **Adzuna**: decisión de pago aplazada. Si InfoJobs no resulta suficiente, se valora pagarla. Las 25 llamadas de prueba se reservan para capturar respuestas reales como fixtures de tests.

**Plan B:** Remotive como fuente adicional gratuita.

## Descartadas

- **Jooble:** límites de uso muy restrictivos y descripciones truncadas (según fuentes de terceros, pendiente de verificar).
- **Arbeitnow:** cobertura centrada en Alemania, poco útil para el perfil.

## Consecuencias

- Coste cero de fuentes mientras no se decida lo contrario.
- Riesgo: si la API de InfoJobs tiene límites o restricciones no previstas, la cobertura de ofertas presenciales/híbridas en España será baja.
- Habrá duplicados entre fuentes → la deduplicación (principio III.4) es necesaria desde el segundo conector.
- Cada conector tendrá su spec con condiciones de uso revisadas, límites y atribución (principio IV.2).
- Las credenciales van en `.env`, nunca en el repo (principio VII).