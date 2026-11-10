# ADR-0007: Skill `/tarea` para ejecutar tareas SDD

**Estado:** Aceptado · **Fecha:** 2026-11-10 · **Evoluciona:** ADR-0001

## Contexto

El ADR-0001 eligió SDD manual con evolución a comandos propios cuando el flujo estuviera interiorizado. Tras T1–T3 de la spec 000, cada tarea repite el mismo ritual: pegar un mensaje a Claude Code, implementar solo esa tarea, informar, comprobar, marcar `[x]` y hacer commit con el mensaje de `tasks.md`. Repetirlo a mano es lento y propenso a olvidos (p. ej. no marcar la casilla o desviarse del plan sin anotarlo).

## Opciones consideradas

1. **Seguir pegando el mensaje a mano:** sin trabajo extra, pero repetitivo y sin comprobaciones previas.
2. **Skill de proyecto en `.claude/skills/tarea/SKILL.md`**, invocable solo por mí (`/tarea <spec> <tarea>`): codifica el flujo completo, incluidas comprobaciones previas y formato de informe. Se versiona con el repo.
3. **Skill que además hace commit y push automáticamente:** más rápido, pero elimina mi revisión, que es la base del SDD ("yo decido").

## Decisión

**Opción 2**, con estas reglas:
- `disable-model-invocation: true`: Claude no la lanza por su cuenta (tiene efectos: crea ficheros y commits).
- Comprobaciones previas: documentos aprobados, tareas anteriores hechas y árbol de git limpio.
- Las desviaciones del plan se anotan y se proponen como corrección del documento; si cambian el alcance, se detiene.
- **El commit solo se hace tras mi confirmación explícita**, y el push se pregunta aparte.

## Consecuencias

- **Positivas:** flujo consistente en todas las tareas; menos olvidos; el proceso queda documentado y visible en el repo (portfolio).
- **Negativas:** hay que mantener la skill si cambia el flujo; dependencia del formato de `tasks.md`.
- **Revisión:** se valida con la T4 de la spec 000 y se ajusta si hace falta.