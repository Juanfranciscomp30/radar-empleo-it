# ADR-0010: Spring Data JPA como capa de persistencia

**Estado:** Aceptado · **Fecha:** 2026-11-11 · **Relacionado:** spec 001, ADR-0011

## Contexto

La spec 000 solo comprobaba la conexión a PostgreSQL (`spring-boot-starter-jdbc`) y dejó para la spec 001 la elección de la capa de persistencia. La spec 001 necesita guardar ofertas normalizadas, evitar duplicados (CA-4) y leerlas paginadas (CA-6).

## Opciones consideradas

1. **Spring Data JPA (Hibernate):** entidades `@Entity` y repositorios. Estándar del mercado Java en España, mucha documentación y paginación integrada. Tiene "magia" que da problemas si no se entiende (*lazy loading*, N+1, *dirty checking*).
2. **Spring Data JDBC:** repositorios sin sesión ni *lazy loading*; simple y predecible, pero menos extendido en empresas.
3. **`JdbcClient` con SQL a mano:** control total y la inserción idempotente es trivial, pero más código de mapeo y sin repositorios ni paginación automática.

## Decisión

**Opción 1: Spring Data JPA**, con estas reglas para evitar sus trampas:

- Entidades **sin relaciones** entre tablas mientras no hagan falta (sin *lazy loading*).
- La idempotencia (CA-4) la garantiza una **restricción única en la base de datos** (`fuente` + `id_externo`), no la lógica de Hibernate. La inserción usa una consulta nativa `INSERT … ON CONFLICT DO NOTHING`.
- La API no expone entidades: usa DTOs propios.
- Hibernate **nunca** modifica el esquema (`ddl-auto=validate`; ver ADR-0011).

## Consecuencias

- **Positivas:** tecnología más demandada en el mercado; repositorios y paginación (`Pageable`) sin código propio; idempotencia robusta incluso ante ejecuciones concurrentes.
- **Negativas:** más dependencias y arranque algo más lento; hay que conocer las trampas de JPA para no caer en ellas.
- **Dependencias:** se añade el starter de Spring Data JPA (nombre exacto según Spring Boot 4, a comprobar en Spring Initializr).
