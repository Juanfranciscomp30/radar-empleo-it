# ADR-0012: Ingesta programada con `@Scheduled` y disparo manual tras un flag

**Estado:** Aceptado · **Fecha:** 2026-11-11 · **Relacionado:** spec 001 (CA-3, CA-4, CA-5)

## Contexto

La spec 001 exige que la ingesta de ofertas se ejecute automáticamente unas pocas veces al día, respetando el uso justo de Jobicy, y que pueda lanzarse a mano para pruebas (CA-3).

## Opciones consideradas

**Ejecución automática:**
1. **`@Scheduled` de Spring:** una anotación con expresión cron configurable; sin dependencias nuevas. Con varias instancias, cada una ejecutaría la ingesta.
2. **Quartz / ShedLock:** planificación persistente y segura con varias instancias; demasiado para una sola.
3. **Cron externo** (GitHub Actions, sistema operativo): exige que la app sea accesible desde fuera.

**Ejecución manual:**
1. **Endpoint siempre activo:** cómodo, pero cualquiera que llegue a la web podría lanzar ingestas sin límite y provocar un bloqueo de Jobicy por abuso.
2. **Endpoint tras un flag de configuración, desactivado por defecto.**

## Decisión

- **`@Scheduled`** con cron configurable; por defecto **cada 6 horas** (4 llamadas al día).
- **Una ejecución al arrancar**, configurable, para tener ofertas nada más levantar el sistema.
- **Endpoint manual `POST /api/ingestas`** que solo existe si `radar.ingesta.manual.habilitada=true` (por defecto `false`).
- La ingesta es **idempotente** (ADR-0010) y cada conector se ejecuta aislado: el fallo de uno se registra y no detiene a los demás (constitución III.3).

## Consecuencias

- **Positivas:** cero dependencias nuevas; ejecutar dos veces no tiene efectos (idempotencia); no se expone una operación que consume una API de terceros.
- **Negativas:** si algún día hay varias instancias, habrá que revisar esta decisión (p. ej. ShedLock).
- **Seguridad:** el flag manual nunca se activa en una demo pública.
