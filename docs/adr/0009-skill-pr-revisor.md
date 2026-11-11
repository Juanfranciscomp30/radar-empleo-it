# ADR-0009: Skill `/pr` con subagente revisor independiente

**Estado:** Aceptado · **Fecha:** 2026-11-11 · **Evoluciona:** ADR-0007, ADR-0008

## Contexto

El ADR-0008 establece una rama y una pull request por spec, con `main` protegida por CI. El cierre de cada rama repite siempre los mismos pasos: abrir la PR, esperar a CI, hacer merge y volver a `main`. Además, los tests comprueban que el código funciona, pero nadie comprueba de forma sistemática que la rama hace **lo que pide la spec** y respeta la constitución.

## Opciones consideradas

1. **Skill `/pr` sin subagente:** automatiza abrir la PR, esperar a CI y el merge. Simple, pero no añade revisión.
2. **Skill `/pr` + subagente `revisor-spec` de solo lectura:** antes de abrir la PR, un subagente con contexto limpio revisa el diff frente a la spec, la constitución y los ADRs, y emite un veredicto. Después, el flujo de la opción 1.
3. **Todo el flujo en un subagente en segundo plano:** no bloquea la conversación, pero un subagente en segundo plano no puede pedir confirmación antes del merge.

## Decisión

**Opción 2.**

- `.claude/agents/revisor-spec.md`: subagente revisor con herramientas de lectura y git en modo solo lectura. Devuelve un veredicto (APTA, APTA CON OBSERVACIONES, NO APTA) con evidencias por criterio de aceptación.
- `.claude/skills/pr/SKILL.md`: se ejecuta en la conversación principal para poder pedir confirmación. Delega la revisión en el subagente, abre la PR con `gh`, espera a CI y hace merge commit **solo con confirmación explícita**.
- Las ramas para cambios de proceso o documentación que no pertenecen a ninguna spec usan el prefijo `chore/` y siguen el mismo flujo; el revisor las compara solo con la constitución y los ADRs.
- La skill `/tarea` deja de abrir la PR por sí misma y, al terminar la última tarea, propone ejecutar `/pr`.

## Consecuencias

- **Positivas:** revisión independiente antes de cada merge (quien implementa no se corrige a sí mismo); descripción de PR coherente y trazable a la spec; menos pasos manuales.
- **Negativas:** requiere GitHub CLI (`gh`) instalado y autenticado; cada PR tarda algo más por la revisión.
- **Límites:** el revisor no sustituye a los tests ni a mi revisión; el merge sigue siendo decisión mía.