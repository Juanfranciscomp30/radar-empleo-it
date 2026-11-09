# ADR-0001: SDD manual con evolución a comandos personalizados

**Estado:** Aceptado · **Fecha:** 2026-11-09

## Contexto

El proyecto se desarrolla con Spec-Driven Development: yo decido y Claude Code implementa. Hay que elegir cómo organizar los artefactos (constitución, specs, planes, tareas, ADRs).

## Opciones consideradas

1. **GitHub Spec Kit:** flujo predefinido y conocido, pero añade una herramienta con convenciones propias que cambian entre versiones y puede ser excesiva para un proyecto personal.
2. **SDD manual:** estructura propia en `docs/` y `specs/`. Control total, cero dependencias, pero exige disciplina y crear las plantillas.
3. **Híbrido:** estructura manual más comandos propios en `.claude/commands/` que automatizan el flujo.

## Decisión

Empezar con **SDD manual** (opción 2) y evolucionar a la **opción híbrida** (3) cuando el flujo esté interiorizado.

## Consecuencias

- **Positivas:** entiendo y puedo justificar cada artefacto; sin dependencia de una herramienta externa; la migración a comandos es incremental.
- **Negativas:** más trabajo inicial en plantillas; el flujo depende de mi disciplina hasta automatizarlo.
- **Estructura:** `docs/constitution.md`, `docs/adr/NNNN-titulo.md`, `specs/NNN-nombre/{spec,plan,tasks}.md`.