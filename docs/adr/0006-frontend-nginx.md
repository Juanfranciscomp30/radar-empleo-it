# ADR-0006: Frontend servido con nginx en Docker

**Estado:** Aceptado · **Fecha:** 2026-11-09 · **Relacionado:** spec 000

## Contexto

La spec 000 exige levantar frontend, backend y PostgreSQL con `docker compose up`. Hay que decidir cómo se ejecuta Angular dentro de Docker y cómo llama al backend. La constitución (principio II) exige ADR para tecnologías fuera de su lista: nginx no está en ella.

## Opciones consideradas

1. **Servidor de desarrollo (`ng serve`) en el contenedor:** recarga instantánea, pero poco parecido a producción, imagen grande y obliga a configurar CORS en el backend.
2. **Build de producción servido con nginx:** Angular se compila a estáticos y nginx los sirve; nginx actúa además como *reverse proxy* de `/api/` hacia el backend. Parecido a producción, imagen pequeña, sin CORS. No hay recarga instantánea dentro de Docker.

## Decisión

**Opción 2.** Dockerfile multi-etapa: una etapa con Node compila Angular y otra con nginx sirve los estáticos y redirige `/api/` al backend.

Flujo de trabajo:
- `docker compose up`: sistema completo como en producción (demo y CA-1).
- Desarrollo diario del frontend: `npm start` fuera de Docker, con el proxy de desarrollo de Angular apuntando al backend en Docker.

## Consecuencias

- **Positivas:** entorno cercano a producción; imagen ligera; sin CORS porque navegador, frontend y API comparten origen.
- **Negativas:** dos formas de ejecutar el frontend (Docker y local) que hay que mantener coherentes; cambiar el frontend en Docker requiere reconstruir la imagen.
- Se añade nginx al stack.