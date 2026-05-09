FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app

COPY pom.xml .
COPY infrastructure/pom.xml infrastructure/
COPY core/pom.xml core/
COPY utils/pom.xml utils/

RUN mvn dependency:go-offline -B

# Copiar el código fuente de todos los módulos
COPY infrastructure/src infrastructure/src
COPY core/src core/src
COPY utils/src utils/src

# Construir todos los módulos
RUN mvn clean package -DskipTests -B

# Etapa de ejecución
FROM eclipse-temurin:17-jdk-jammy

# Instalar herramientas de diagnóstico
RUN apt-get update && apt-get install -y \
    curl \
    iputils-ping \
    net-tools \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Crear directorio de logs y dar permisos
RUN mkdir -p /app/logs && \
    chmod 777 /app/logs

# Copiar el JAR desde la etapa de construcción
COPY --from=build /app/infrastructure/target/infrastructure-0.0.1-SNAPSHOT.jar app.jar

# Configuración de variables de entorno
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]