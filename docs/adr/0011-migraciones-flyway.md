# ADR-0011: Flyway para las migraciones de base de datos

**Estado:** Aceptado · **Fecha:** 2026-11-11 · **Relacionado:** spec 001, ADR-0010

## Contexto

La spec 001 crea la primera tabla de negocio (`ofertas`) y las specs siguientes la ampliarán (puntuación de IA, nuevas fuentes). Hay que decidir cómo se crea y evoluciona el esquema. La constitución (II) exige un ADR para cualquier tecnología nueva.

## Opciones consideradas

1. **Flyway:** migraciones en SQL puro, numeradas (`V1__…sql`) y aplicadas al arrancar. Simple y muy usado con Spring Boot. Los *rollbacks* se hacen con migraciones nuevas.
2. **Liquibase:** cambios descritos en XML/YAML/JSON, independiente de la base de datos y con *rollbacks*. Más complejo de lo necesario.
3. **Hibernate `ddl-auto=update`:** sin trabajo, pero sin historial, con riesgo de pérdida de datos y nada usado en producción.

## Decisión

**Opción 1: Flyway.**

- Migraciones en `backend/src/main/resources/db/migration/`, en SQL de PostgreSQL.
- Hibernate en modo **`ddl-auto=validate`**: Flyway crea el esquema y Hibernate solo comprueba que las entidades coinciden; si no, la aplicación no arranca (*fail fast*).
- Una migración ya fusionada en `main` **nunca se modifica**: cualquier cambio es una migración nueva.

## Consecuencias

- **Positivas:** el esquema queda versionado en git y se revisa en cada PR; restricciones (como la única de CA-4) explícitas en SQL; mismo esquema en local, en Docker, en Testcontainers y en CI.
- **Negativas:** una dependencia más; hay que escribir el SQL a mano.
- **Dependencias:** Flyway y su módulo de PostgreSQL (nombre exacto del starter según Spring Boot 4, a comprobar en Spring Initializr).
