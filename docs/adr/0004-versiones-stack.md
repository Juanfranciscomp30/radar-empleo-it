# ADR-0004: Versiones del stack

**Estado:** Aceptado · **Fecha:** 2026-11-09

## Contexto

La constitución (principio II) fija el stack (Java + Spring Boot, Angular, PostgreSQL, Docker) pero deja las versiones concretas para un ADR. Es un proyecto nuevo, sin código previo que migrar, con un MVP en 2 semanas.

## Opciones consideradas

1. **Java 25 + Spring Boot 4.1.x + Angular 22:** todo en la última versión estable. Java 25 es la LTS más reciente. Menos tutoriales de Spring Boot 4 que de la 3.
2. **Java 21 + Spring Boot 4.1.x + Angular 22:** Java más extendido en empresas y con más material, pero se empieza un proyecto nuevo con una LTS superada sin beneficio concreto.
3. **Esperar a Spring Boot 4.2:** última versión, pero retrasa el arranque del MVP.

## Decisión

Opción 1:

- **Java 25 (LTS)**
- **Spring Boot 4.1.x** (último parche disponible)
- **Angular 22.x**
- Node.js: la versión LTS compatible con Angular 22 (comprobar en angular.dev/reference/versions).

## Consecuencias

- **Positivas:** soporte de seguridad a largo plazo; sin migraciones pendientes desde el primer día; demuestra stack actualizado.
- **Negativas:** algo menos de documentación y ejemplos de comunidad para Spring Boot 4.
- **Seguimiento:** cuando salga Spring Boot 4.2, se valora actualizar (cambio menor) con su propio commit.
- Las imágenes Docker y GitHub Actions usarán estas mismas versiones.