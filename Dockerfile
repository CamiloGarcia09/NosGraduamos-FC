FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
COPY infrastructure/pom.xml infrastructure/
COPY core/pom.xml core/
COPY utils/pom.xml utils/

RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY infrastructure/src infrastructure/src
COPY core/src core/src
COPY utils/src utils/src

# Compilar
RUN mvn clean package -DskipTests -B

# =====================================================
# Runtime
# =====================================================
FROM ubuntu:24.04

# Instalar Java 21 y utilidades
RUN apt-get update && apt-get install -y \
    openjdk-21-jdk \
    curl \
    iputils-ping \
    net-tools \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

# Logs
RUN mkdir -p /app/logs && chmod 777 /app/logs

# Copiar JAR
COPY --from=build /app/infrastructure/target/infrastructure-0.0.1-SNAPSHOT.jar app.jar

# JVM
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

EXPOSE 8085

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]