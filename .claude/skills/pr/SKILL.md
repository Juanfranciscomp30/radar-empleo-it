---
name: pr
description: Abre la pull request de la rama actual hacia main tras una revisión independiente del subagente revisor-spec, espera a que CI termine y hace el merge solo con confirmación. Uso: /pr
argument-hint: "(sin argumentos: usa la rama actual)"
disable-model-invocation: true
---

# Skill: pull request con revisión independiente

Sigue estos pasos en orden. Si algo falla, **detente** y explica qué pasa.

## 1. Comprobaciones previas

- La rama actual **no** es `main`. Debe ser `spec/NNN-nombre` o `chore/tema` (ADR-0008 y ADR-0009).
- `git status` está limpio.
- La rama está subida y al día: `git fetch` y comprueba con `git status -sb` que no hay commits sin subir. Si los hay, propón `git push` y espera mi confirmación.
- `gh` está instalado y con sesión iniciada: `gh auth status`. Si no, dime cómo instalarlo (`winget install GitHub.cli` y `gh auth login`) y para.
- Si es una rama `spec/NNN-...`, localiza `specs/NNN-*/` y comprueba que todas las tareas de `tasks.md` están en `[x]`. Si falta alguna, dímelo y para.
- Si ya existe una PR abierta para la rama (`gh pr view`), no crees otra: repite el paso 2 (la revisión) para que los commits subidos después de abrirla también se revisen. Si el veredicto permite seguir, actualiza la descripción de la PR si ha cambiado (`gh pr edit <número> --body "..."`) y continúa desde el paso 4.

## 2. Revisión independiente

Delega la revisión en el subagente **`revisor-spec`**. Pásale el nombre de la rama y, si es de spec, la carpeta de la spec. No le pases tu opinión ni lo que crees que va a encontrar.

Enséñame su informe completo, sin resumirlo.

- **NO APTA**: para aquí. Propón cómo corregir los bloqueantes (en la rama, con sus commits) y no sigas.
- **APTA CON OBSERVACIONES**: pregúntame si sigo o si las corregimos antes.
- **APTA**: sigue.

## 3. Abrir la pull request (con mi confirmación)

Prepara:
- **Título:** `Spec NNN: nombre` para ramas de spec, o un título en formato Conventional Commits para ramas `chore/`.
- **Descripción:** el "Resumen para la descripción de la PR" del revisor, la tabla de criterios de aceptación (si hay spec) y una línea "Revisada por el subagente revisor-spec: <veredicto>".

Enséñamelos y, cuando confirme, ejecuta `gh pr create --base main --head <rama> --title "..." --body "..."`. Enséñame la URL de la PR.

## 4. Esperar a CI

Ejecuta `gh pr checks <número> --watch` y espera a que terminen `backend`, `frontend` y `docker`.

- Si alguno falla: enséñame el job y el paso que fallan (`gh run view --log-failed`), propón la corrección y **no** hagas merge.
- Si todos están en verde: sigue.

## 5. Merge (solo con mi confirmación explícita)

Pregúntame: "CI en verde. ¿Hago el merge?". **Nunca** hagas merge sin un sí explícito.

Cuando confirme: `gh pr merge <número> --merge --delete-branch` (merge commit, nunca squash; ADR-0008). `--delete-branch` borra la rama remota y la local, y cambia a `main`.

## 6. Volver a main

```
git switch main
git pull
git log --oneline -3
git branch --list <rama>
```

No hace falta `git branch -d`: `gh pr merge --delete-branch` ya ha borrado la rama local. Si `git branch --list` aún la muestra, bórrala con `git branch -d <rama>`.

Enséñame el log y confírmame que la rama está fusionada.
