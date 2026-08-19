FROM maven:3.9.9-eclipse-temurin-17-noble AS build

WORKDIR /app

COPY pom.xml .
COPY infrastructure/pom.xml infrastructure/
COPY core/pom.xml core/
COPY utils/pom.xml utils/

RUN mvn dependency:go-offline -B

COPY infrastructure/src infrastructure/src
COPY core/src core/src
COPY utils/src utils/src

RUN mvn clean package -DskipTests -B

FROM eclipse-temurin:17-jre-noble

RUN apt-get update \
    && apt-get install --no-install-recommends -y curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system app \
    && useradd --system --gid app --home-dir /app app

WORKDIR /app

RUN mkdir -p /app/logs && chown -R app:app /app

COPY --from=build --chown=app:app /app/infrastructure/target/infrastructure-*.jar app.jar

ENV JAVA_OPTS="-Xms256m -Xmx768m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

EXPOSE 8085

USER app

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl --fail --silent http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
