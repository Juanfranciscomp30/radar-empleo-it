# ADR-0008: Una rama y una pull request por spec, con `main` protegida

**Estado:** Aceptado · **Fecha:** 2026-11-11 · **Relacionado:** constitución VI.4, ADR-0007

## Contexto

La constitución (VI.4) exige que GitHub Actions bloquee la entrada en `main` de código que no compila o no pasa los tests. Para que GitHub pueda exigirlo, el código debe llegar a `main` mediante pull requests (PR). Hasta la spec 000 se hacía push directo a `main`.

## Opciones consideradas

1. **Una rama por spec:** todos los commits de la spec (uno por tarea) en `spec/NNN-nombre`, y una PR al terminar la spec. CI obligatorio antes del merge.
2. **Una rama por tarea:** una PR por tarea. Máximo control, pero demasiados pasos para un proyecto en solitario.
3. **Regla con excepción para el administrador:** sin cambios en el flujo, pero la protección no protege nada en la práctica.

## Decisión

**Opción 1.**

- Ruleset de GitHub sobre `main`: PR obligatoria (0 aprobaciones, al ser un proyecto individual), checks `backend`, `frontend` y `docker` obligatorios, sin force push ni borrado, y **sin excepciones** (lista de bypass vacía).
- Cada spec se trabaja en la rama `spec/NNN-nombre`, que se crea al empezar su primera tarea y se sube a GitHub.
- La última tarea de cada spec abre la PR; el merge se hace cuando CI está en verde, con **merge commit** para conservar un commit por tarea en el historial.
- La skill `/tarea` se actualiza para comprobar la rama y para abrir la PR.

## Consecuencias

- **Positivas:** ningún código entra en `main` sin pasar CI; cada spec queda como una PR revisable (portfolio); flujo profesional (feature branch + PR + CI) sin pasos excesivos.
- **Negativas:** `main` solo se actualiza al cerrar cada spec; hay que acordarse de trabajar en la rama.
- **Transición:** la propia T13 de la spec 000 es la primera que sigue este flujo.
