# Etapa 1: Build
FROM maven:3.8-openjdk-17-slim AS builder

WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY src ./src

# Construir la aplicación
RUN mvn clean package -DskipTests

# Etapa 2: Runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Instalar dependencias del sistema
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Crear usuario no-root para seguridad
RUN groupadd -r spring && useradd -r -g spring spring

# Copiar el JAR construido desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Cambiar propiedad del archivo JAR al usuario spring
RUN chown spring:spring app.jar

# Cambiar al usuario no-root
USER spring:spring

# Exponer el puerto de la aplicación
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV DB_HOST=mysql-db
ENV DB_PORT=3306
ENV DB_NAME=lugares_comunes
ENV DB_USERNAME=root
ENV DB_PASSWORD=root_password
ENV JWT_SECRET=mySecretKey12345678901234567890123456789012345678901234567890
ENV JWT_EXPIRATION=86400000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/auth/health || exit 1

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# Comando por defecto con opciones JVM optimizadas
CMD ["-Xmx512m", "-Xms256m", "-Djava.security.egd=file:/dev/./urandom"]