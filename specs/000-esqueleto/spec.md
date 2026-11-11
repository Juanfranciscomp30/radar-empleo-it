# Spec 000: Esqueleto del proyecto

**Estado:** Implementada · **Fecha:** 2026-11-09 · **Relacionado:** constitución v2.0.0, ADR-0004, ADR-0005

## Objetivo

Tener la base técnica del proyecto funcionando de punta a punta (frontend → backend → base de datos) y verificada automáticamente en GitHub, para poder construir funcionalidades encima sin preocuparse de la infraestructura.

## Historias de usuario

- **HU-1:** Como desarrollador, quiero levantar todo el sistema con un solo comando, para no tener que arrancar cada pieza a mano.
- **HU-2:** Como desarrollador, quiero comprobar que el backend está vivo y conectado a la base de datos, para detectar problemas de configuración desde el principio.
- **HU-3:** Como desarrollador, quiero que la web muestre si el backend responde, para comprobar que frontend y backend se comunican.
- **HU-4:** Como desarrollador, quiero que GitHub compile y pase los tests en cada cambio, para no subir nunca código roto a `main`.

## Criterios de aceptación

| ID | Criterio | Historia |
|---|---|---|
| CA-1 | Con Docker instalado, tras copiar `.env.example` a `.env`, ejecutar `docker compose up` en la raíz del repo levanta los 3 servicios (frontend, backend, PostgreSQL) sin errores. | HU-1 |
| CA-2 | `GET http://localhost:8080/api/health` responde `200` e indica que la aplicación está en marcha y que la base de datos está accesible. | HU-2 |
| CA-3 | Si PostgreSQL no está disponible, el endpoint de salud lo refleja (no responde como si todo estuviera bien). | HU-2 |
| CA-4 | `http://localhost:4200` muestra una página con el título "Radar de Empleo IT" y el estado del backend: "Conectado" si responde, "Sin conexión" si no. | HU-3 |
| CA-5 | El backend tiene al menos un test de integración que arranca un PostgreSQL real (Testcontainers) y verifica el endpoint de salud. `./mvnw test` pasa. | HU-2, HU-4 |
| CA-6 | El frontend tiene al menos un test del servicio que consulta el estado del backend. Los tests del frontend pasan. | HU-3, HU-4 |
| CA-7 | Un workflow de GitHub Actions se ejecuta en cada push a `main` y en cada pull request: compila y testea backend y frontend, y falla si cualquiera de los dos falla. | HU-4 |
| CA-8 | Ningún secreto está en el repo: `.env` está en `.gitignore` y `.env.example` documenta todas las variables necesarias con valores de ejemplo. | — |
| CA-9 | El `README.md` explica en pocos pasos cómo arrancar el proyecto en local. | HU-1 |

## Fuera de alcance

- Ofertas de empleo, conectores, puntuación con IA (specs posteriores).
- Autenticación y cuentas de usuario.
- Despliegue en la nube.
- Diseño visual: la página de la web solo tiene que ser funcional.
- Esquema de base de datos de negocio (solo la conexión).

## Restricciones

- Versiones según ADR-0004 (Java 25, Spring Boot 4.1.x, Angular 22.x) y build con Maven Wrapper según ADR-0005.
- Cumple la constitución: principios II, VI y VII.

## Pasos manuales (los hago yo, no la IA)

- Activar en GitHub la protección de la rama `main` para que exija que el workflow de CA-7 pase antes de hacer merge (constitución VI.4).

## Preguntas abiertas

- Ninguna.