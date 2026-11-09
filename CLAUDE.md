# CLAUDE.md

Reglas de trabajo para Claude en este repositorio.

## Spec-Driven Development

- Este proyecto sigue **Spec-Driven Development**. Antes de cualquier tarea, lee `docs/constitution.md` y los ADRs de `docs/adr/`.
- No escribas código de producción sin una spec aprobada en `specs/NNN-nombre/`. Si se pide algo sin spec, avisa antes de continuar.
- Si una tarea contradice la constitución o una spec, detente y pregunta.

## Secretos

- Nunca versiones secretos. Usa `.env` (no versionado) y mantén `.env.example` actualizado con todas las variables necesarias, sin valores reales.

## Commits

- Commits pequeños siguiendo **Conventional Commits**: `feat:`, `fix:`, `docs:`, `chore:`, `test:`.
