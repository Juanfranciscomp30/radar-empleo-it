# ADR-0002: Solo fuentes con acceso automatizado permitido

**Estado:** Aceptado · **Fecha:** 2026-11-09 · **Sustituye a:** principio IV de la constitución v1.0.0

## Contexto

La constitución v1.0.0 permitía conectores "restringidos" (scraping contra las condiciones de uso, p. ej. LinkedIn) para uso personal, aislados y desactivados por defecto, con la obligación de eliminarlos (incluido el historial de git) antes de publicar.

## Opciones consideradas

1. **Mantener conectores restringidos para uso personal:** más cobertura, pero riesgo de baneo, incumplimiento de condiciones de uso, repo privado obligatorio y limpieza de historial antes de publicar.
2. **Solo fuentes que permiten el acceso automatizado:** menos cobertura, pero legal, estable, defendible en entrevista y publicable desde el primer día.

## Decisión

Opción 2: solo APIs oficiales, feeds públicos y portales cuyas condiciones de uso lo permitan. Cada conector documenta en su spec las condiciones revisadas.

## Consecuencias

- **Positivas:** el repositorio puede ser público desde el inicio (suma al portfolio); sin riesgo legal ni de baneo; conectores más estables al no depender del HTML.
- **Negativas:** menor cobertura de ofertas; algunos portales relevantes (p. ej. LinkedIn) quedan fuera.
- **Constitución:** sube a v2.0.0 (redefinición del principio IV).