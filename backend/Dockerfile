# Imagen del backend (plan 000 §4.1): multi-etapa con etiquetas de versión fijas.

# --- Etapa 1: compilar con JDK 25 y Maven Wrapper ---
FROM eclipse-temurin:25.0.5_8-jdk-noble AS build
WORKDIR /app

# Primero solo lo necesario para descargar dependencias: así Docker reutiliza
# esta capa mientras no cambie el pom.xml.
COPY mvnw pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B -q dependency:go-offline

COPY src src
# Los tests no se ejecutan aquí: el de integración necesita Docker (Testcontainers)
# y se ejecutan con ./mvnw verify en local y en CI.
RUN ./mvnw -B -q package -DskipTests \
    && cp target/*.jar app.jar

# --- Etapa 2: solo JRE 25 con el .jar ---
FROM eclipse-temurin:25.0.5_8-jre-noble
WORKDIR /app

# curl: lo usa el healthcheck de docker-compose para llamar a /api/health.
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

# No ejecutar la aplicación como root.
RUN groupadd --system radar && useradd --system --gid radar radar
COPY --from=build /app/app.jar app.jar
USER radar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
