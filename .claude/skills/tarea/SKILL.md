---
name: tarea
description: Ejecuta UNA tarea del tasks.md de una spec siguiendo el flujo SDD del proyecto (comprobaciones previas, implementación acotada, informe y commit solo tras confirmación). Uso: /tarea seguido del número de spec y de la tarea, p. ej. /tarea 000 T4
argument-hint: "[spec] [tarea]  (ej.: 000 T4)"
disable-model-invocation: true
---

# Skill: ejecutar una tarea SDD

Argumentos recibidos: `$ARGUMENTS`

El primer argumento es el número de spec (p. ej. `000`) y el segundo el identificador de la tarea (p. ej. `T4`). Si falta alguno o no tiene ese formato, **detente** y pide los dos.

## 1. Localizar los documentos

1. Busca la carpeta `specs/<spec>-*/` (p. ej. `specs/000-esqueleto/`). Si no existe o hay más de una, detente y pregunta.
2. Lee, en este orden: `CLAUDE.md`, `docs/constitution.md`, los ADRs de `docs/adr/` que cite el plan, y de la carpeta de la spec: `spec.md`, `plan.md` y `tasks.md`.

## 2. Comprobaciones previas (si alguna falla, detente y explica cuál)

- `spec.md` tiene **Estado: Aprobada**, y `plan.md` y `tasks.md` tienen **Estado: Aprobado**.
- La tarea existe en `tasks.md` y **no** está marcada con `[x]`.
- Todas las tareas anteriores están marcadas con `[x]`. Si no, avisa de cuáles faltan y pregunta si continuar.
- `git status` está limpio. Si hay cambios sin commit, enséñalos y pregunta antes de seguir.

## 3. Si la tarea es [YO]

No la implementes. Explica paso a paso qué tengo que hacer yo, cómo comprobarlo y termina ahí.

## 4. Si la tarea es [IA]: implementar

- Haz **solo** lo que dice el apartado "Qué" de la tarea, siguiendo el plan. No adelantes trabajo de otras tareas.
- Si el plan está desactualizado o hay que desviarse de él (nombres que han cambiado, versiones, etc.), **no lo decidas en silencio**: hazlo solo si el alcance no cambia y apúntalo para el informe. Si cambiaría el alcance o contradice la constitución o un ADR, **detente y pregunta**.
- No escribas secretos en ningún fichero.
- Ejecuta tú mismo la parte de la **Comprobación** que se pueda automatizar (compilar, tests, builds). Si algo falla, intenta corregirlo dentro del alcance de la tarea; si no puedes, detente y explica el error.

## 5. Informe final (siempre con esta estructura)

1. **Qué he hecho**, en pocas frases.
2. **Ficheros** creados, cambiados o borrados (tabla).
3. **Desviaciones del plan**, con la corrección que propongo para `plan.md` (o "ninguna").
4. **Cómo lo compruebas tú**: los pasos exactos de la Comprobación de `tasks.md`. Si la tarea incluye tests, añade cómo verlos fallar a propósito.
5. **Avisos para tareas siguientes**, si los hay.

## 6. Commit: solo con mi confirmación

- **No hagas commit ni marques la casilla por tu cuenta.**
- Termina el informe preguntando: "¿Lo has comprobado? Si me confirmas, marco la tarea con [x] y hago el commit."
- Solo cuando yo confirme explícitamente:
  1. Marca la tarea con `[x]` en `tasks.md`.
  2. Si acordamos correcciones del plan, aplícalas y añade una línea en su apartado "Historial".
  3. Haz `git add` de los ficheros de la tarea y de los documentos tocados, y un único commit con **exactamente** el mensaje indicado en el campo "Commit" de la tarea.
  4. Enséñame `git log --oneline -1` y pregúntame si hago `git push`.
