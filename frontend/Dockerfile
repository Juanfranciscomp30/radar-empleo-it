# Imagen del frontend (plan 000 §4.1, ADR-0006): multi-etapa con etiquetas de versión fijas.

# --- Etapa 1: compilar Angular con Node LTS ---
FROM node:24.19.0-alpine AS build
WORKDIR /app

# Primero solo las dependencias: Docker reutiliza esta capa mientras no cambie package-lock.json.
COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npm run build

# --- Etapa 2: nginx sirve los estáticos y redirige /api/ al backend ---
FROM nginx:1.30.6-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/radar-frontend/browser /usr/share/nginx/html

EXPOSE 80
