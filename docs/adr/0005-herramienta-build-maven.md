# ADR-0005: Maven como herramienta de build del backend

**Estado:** Aceptado · **Fecha:** 2026-11-09

## Contexto

El backend (Java 25 + Spring Boot 4.1, ADR-0004) necesita una herramienta de build para gestionar dependencias, compilar, ejecutar tests y empaquetar la aplicación. La constitución (principio II) exige un ADR para cualquier tecnología fuera de su lista.

## Opciones consideradas

1. **Maven** (`pom.xml`): declarativo y convencional; todos los proyectos siguen la misma estructura. Fácil de leer y revisar. Muy extendido en empresas Java en España. Menos flexible y algo más lento en proyectos grandes.
2. **Gradle** (`build.gradle.kts`): programable y más rápido gracias a la caché y a las builds incrementales. Más flexible, pero con más formas de hacer lo mismo y una curva de aprendizaje mayor.

## Decisión

**Maven**, usando el **Maven Wrapper** (`mvnw`), incluido en el repositorio.

## Consecuencias

- **Positivas:** configuración fácil de revisar cuando la IA añade dependencias; menos complejidad mientras aprendo SDD y Claude Code; conocimiento directamente aplicable en ofertas Java.
- **Negativas:** menos flexibilidad para personalizar la build; la ventaja de velocidad de Gradle no se aprovecha (irrelevante para el tamaño de este proyecto).
- **Wrapper:** no hace falta instalar Maven en el PC ni en CI; todos (yo, Claude Code y GitHub Actions) usan la misma versión con `./mvnw`.