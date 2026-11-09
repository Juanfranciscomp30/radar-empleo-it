# Constitución — Radar de Empleo IT

**Versión:** 2.0.0 · **Fecha:** 2026-11-09 · **Estado:** vigente

Este documento recoge los principios no negociables del proyecto. Toda spec, plan y línea de código debe respetarlos. Cambiarlos requiere un ADR y subir la versión.

## Propósito

Agregador personal de ofertas IT junior que recoge ofertas de varias fuentes y les asigna una puntuación de encaje con mi perfil mediante IA. Uso personal; debe poder mostrarse como demo de portfolio.

## I. Spec-Driven Development

1. No se escribe código sin una spec aprobada en `specs/NNN-nombre/spec.md`.
2. Flujo obligatorio: constitución → spec → plan → tareas → implementación → revisión.
3. Yo decido; la IA (Claude Code) implementa. Las decisiones de arquitectura se registran como ADR en `docs/adr/`.
4. Si una implementación contradice la constitución o una spec, se detiene y se consulta.

## II. Stack tecnológico

- **Backend:** Java + Spring Boot (versiones LTS/estables concretas fijadas en ADR).
- **Frontend:** Angular (versión estable concreta fijada en ADR).
- **Base de datos:** PostgreSQL.
- **Infraestructura:** Docker y Docker Compose; todo el sistema arranca con un único `docker compose up`.
- **CI:** GitHub Actions.

Añadir una tecnología fuera de esta lista requiere ADR.

## III. Arquitectura de conectores

1. Cada fuente de ofertas es un **conector** que implementa una interfaz común y devuelve ofertas en un modelo normalizado.
2. Cada conector se activa o desactiva por configuración, sin tocar código del núcleo.
3. El fallo de un conector nunca detiene la ingesta de los demás.
4. Las ofertas se deduplican entre fuentes.

## IV. Política de fuentes: 100 % legal

1. Solo se integran fuentes que **permiten expresamente el acceso automatizado**: APIs oficiales, feeds públicos (RSS/JSON) o portales cuyas condiciones de uso y `robots.txt` lo autoricen.
2. Antes de implementar un conector, su spec documenta: tipo de acceso, enlace a las condiciones de uso revisadas, límites de uso (rate limits) y requisitos de atribución.
3. Se respetan los límites de frecuencia y la atribución que exija cada fuente.
4. Solo se almacenan datos de la oferta; nunca datos personales de reclutadores o empleados.
5. Queda excluido el scraping de portales que lo prohíben (p. ej. LinkedIn).
6. Como consecuencia, el repositorio puede ser **público** desde el inicio.

## V. Puntuación con IA

1. El proveedor de IA está detrás de una interfaz; cambiar de proveedor o modelo no afecta al resto del sistema.
2. Se usa un modelo pequeño y barato en la nube, elegido tras comparar precios vigentes (ADR).
3. Solo se puntúan ofertas nuevas; las puntuaciones se guardan y no se recalculan salvo cambio de perfil o de modelo.
4. Existe un tope de gasto mensual configurado en el proveedor.
5. Cada puntuación incluye una explicación breve (motivos de encaje y de no encaje).
6. Al modelo solo se envían los datos del perfil necesarios para puntuar.

## VI. Calidad y tests

1. **Backend:** JUnit 5 + Mockito para lógica; Testcontainers para integración con PostgreSQL real.
2. **Frontend:** tests de servicios y componentes con lógica, con el runner por defecto de la versión de Angular usada.
3. **Cobertura mínima del 70 % en la capa de dominio y servicios.** No se persigue cobertura global.
4. GitHub Actions bloquea el merge a `main` si no compila o fallan los tests.

## VII. Seguridad y configuración

1. Ningún secreto (API keys, contraseñas) se versiona; se usan variables de entorno y un `.env.example` documentado.
2. `.env` está en `.gitignore` desde el primer commit.

## VIII. Alcance y plazos

- **MVP en 2 semanas como máximo** (fecha límite: 2026-11-23).
- **Dentro del MVP:** 2–3 conectores, puntuación con IA, listado de ofertas con filtros y orden por encaje.
- **Fuera del MVP:** cuentas de usuario, despliegue en la nube, notificaciones, conectores adicionales.
- Ante la duda, se recorta alcance antes que mover la fecha.

## IX. Demo y portfolio

1. Durante el MVP la app se ejecuta en local con Docker Compose.
2. La demo de portfolio se hace con vídeo y capturas.
3. El despliegue público en la nube es una fase posterior.

## Gobierno

- Las modificaciones se hacen vía ADR y suben la versión (MAJOR: se elimina o redefine un principio; MINOR: se añade uno; PATCH: aclaraciones).

## Historial

- **2.0.0** (2026-11-09): el principio IV se redefine; se eliminan los conectores restringidos y solo se admiten fuentes con acceso automatizado permitido (ADR-0002).
- **1.0.0** (2026-11-09): versión inicial.