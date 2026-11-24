# Decisiones de arquitectura (ADR)

Cada decisión de arquitectura o de proceso se registra aquí como un ADR (*Architecture Decision Record*): contexto, opciones consideradas, decisión y consecuencias (constitución I.3).

## Cómo se usan

- **Numeración:** `NNNN-titulo-corto.md`, correlativa; un número nunca se reutiliza.
- **Estados:** *Propuesto* → *Aceptado*. Un ADR aceptado no se borra: si otro lo cambia, su estado lo indica entre paréntesis (p. ej. "sustituido por ADR-NNNN") y el nuevo ADR lo enlaza.
- **Constitución:** cambiar un principio exige un ADR y subir su versión (ver "Gobierno" en [`../constitution.md`](../constitution.md)).
- **Este índice:** conviene actualizar este índice en el mismo commit que añade o cambia un ADR.

## Índice

| ADR | Decisión | Estado | Fecha |
|---|---|---|---|
| [0001](0001-sdd-manual.md) | SDD manual con evolución a comandos personalizados | Aceptado | 2026-11-09 |
| [0002](0002-politica-fuentes-legal.md) | Solo fuentes con acceso automatizado permitido | Aceptado | 2026-11-09 |
| [0003](0003-fuentes-mvp.md) | Fuentes de ofertas del MVP | Aceptado (calendario actualizado por ADR-0013) | 2026-11-09 |
| [0004](0004-versiones-stack.md) | Versiones del stack | Aceptado | 2026-11-09 |
| [0005](0005-herramienta-build-maven.md) | Maven como herramienta de build del backend | Aceptado | 2026-11-09 |
| [0006](0006-frontend-nginx.md) | Frontend servido con nginx en Docker | Aceptado | 2026-11-09 |
| [0007](0007-skill-tarea.md) | Skill `/tarea` para ejecutar tareas SDD | Aceptado | 2026-11-10 |
| [0008](0008-rama-por-spec.md) | Una rama y una pull request por spec, con `main` protegida | Aceptado (flujo de PR actualizado por ADR-0009) | 2026-11-11 |
| [0009](0009-skill-pr-revisor.md) | Skill `/pr` con subagente revisor independiente | Aceptado | 2026-11-11 |
| [0010](0010-persistencia-jpa.md) | Spring Data JPA como capa de persistencia | Aceptado | 2026-11-11 |
| [0011](0011-migraciones-flyway.md) | Flyway para las migraciones de base de datos | Aceptado | 2026-11-11 |
| [0012](0012-programacion-ingesta.md) | Ingesta programada con `@Scheduled` y disparo manual tras un flag | Aceptado | 2026-11-11 |
| [0013](0013-alcance-mvp.md) | Alcance del MVP recortado para mantener la fecha | Aceptado (alcance del MVP sustituido por ADR-0014) | 2026-11-16 |
| [0014](0014-cierre-mvp-v0.1.md) | Cierre del MVP como v0.1.0 y puntuación con IA en la v0.2.0 | Aceptado | 2026-11-24 |
