# AGENTS.md

## TL;DR
- Monorepo Maven multi-módulo: `core` (dominio/casos de uso) → `utils` (helpers) → `infrastructure` (adaptadores + app ejecutable).
- Arquitectura hexagonal por feature. Dependencias solo hacia abajo: `infrastructure → core → utils`. Nunca al revés.
- Usar `./mvnw`, no `mvn`, para garantizar Maven 3.9.9.
- Antes de correr la app: levantar infraestructura con `docker compose up -d` en `deployment/docker`.
- No hay linter/formatter configurado — no asumas reglas de estilo automáticas.
- Todo cambio debe respetar Clean/Hexagonal Architecture y pasar el Quality Gate de SonarCloud (ver sección "Clean Architecture y SonarCloud").

## Qué es este proyecto
API de mensajes multientorno con catálogo jerárquico (Organización → Aplicación → Módulo → Funcionalidad → Parámetro), soporte de tokens, traducción asistida por IA, caché Redis y eventos SSE en memoria. Diseñada para desplegarse en Azure con secretos en Key Vault/Doppler y observabilidad vía Grafana/Prometheus/Loki.

## Estructura del monorepo
| Módulo | Contenido | Genera jar ejecutable |
|---|---|---|
| `core` | Casos de uso, puertos (interfaces), dominios, validadores, entidades de salida | No |
| `infrastructure` | Adaptadores primarios (controllers REST) y secundarios (SurrealDB, Redis, Pulsar, LangChain4j, Azure Key Vault, Doppler, Loki). Contiene `CrossWordApplication` | Sí |
| `utils` | Helpers transversales: UUID, texto, fechas, JSON, excepciones base, catálogos de mensajes | No |

Flujo de una petición: `Controller → Interactor → UseCase → Repository port → Repository adapter → SurrealDB`.

## Stack y dependencias clave (infrastructure)
Java 17, Spring Boot 3.2.5, SurrealDB 2.1.1 (BD principal, no SQL tradicional), Apache Pulsar 3.2.2, Spring Data Redis, Caffeine 3.1.8, LangChain4j 1.15.1 (Ollama local por defecto / OpenAI), loki-logback-appender 1.6.0, Micrometer Prometheus, SpringDoc OpenAPI, Azure Key Vault, Doppler client.

## Setup y build
Requiere JDK 17 y Maven 3.9.x. Usar el wrapper `./mvnw` en cada módulo en vez de `mvn` instalado globalmente.

```bash
git clone <repo>
cd MessageUcoLab

# Build completo + tests + cobertura JaCoCo
./mvnw clean verify

# Infraestructura de soporte (SurrealDB, Redis, etc.)
cd deployment/docker
docker compose up -d
cd ../..

# Ejecutar la app (requiere infraestructura corriendo)
./mvnw spring-boot:run -pl infrastructure
```

- Puerto por defecto: `8085`
- Swagger UI: `http://localhost:8085/swagger-ui.html`
- Actuator health: `http://localhost:8085/actuator/health`
- Prometheus: `http://localhost:8085/actuator/prometheus`

## Testing
```bash
./mvnw test            # Unitarios e integración (JUnit 5)
./mvnw clean verify    # Build + tests + reporte JaCoCo
```
Reporte de cobertura HTML en `infrastructure/target/site/jacoco/index.html`.

No hay linter/formatter configurado actualmente — no introducir reglas de estilo automáticas ni asumir que existen; seguir el estilo del código circundante.

## Manejo de errores
- Jerarquía base en `core`: `BusinessException`, `ValidationException`, `ConflictException`, `NotFoundException`, `TechnicalException`.
- Excepciones específicas por feature (ej. `MessageNotFoundException`) extienden la jerarquía base.
- `GlobalExceptionHandler` en `infrastructure` traduce excepciones de dominio a respuestas HTTP.
- Validaciones con patrón Composite: `CompositeValidator` agrupa reglas individuales (`*Rule` interfaz → `*RuleImpl` implementación).

## Clean Architecture y SonarCloud
Todo código nuevo o modificado debe respetar la separación de capas y pasar el Quality Gate de SonarCloud del pipeline. Reglas concretas:

- **Dirección de dependencias**: `infrastructure → core → utils`, nunca al revés. `core` no puede importar clases de `infrastructure` (frameworks, Spring, drivers de SurrealDB/Redis/Pulsar, etc.) ni de librerías de infraestructura.
- **`core` libre de frameworks**: casos de uso, dominios y validadores en `core` deben ser Java plano (POJOs), sin anotaciones de Spring ni dependencias de persistencia concretas — solo puertos (interfaces).
- **Sin lógica de negocio en adaptadores**: los controllers REST y los adaptadores de `infrastructure` solo traducen entrada/salida y delegan a los casos de uso; no deben contener reglas de negocio.
- **Cobertura de tests**: mantener o mejorar el porcentaje de cobertura JaCoCo que exige el Quality Gate; toda lógica nueva en `core` debe llevar tests unitarios (no depender solo de tests de integración en `infrastructure`).
- **Complejidad cognitiva**: evitar métodos largos o muy anidados; extraer métodos/objetos cuando Sonar marque "Cognitive Complexity" alta.
- **Sin duplicación**: evitar copiar/pegar lógica entre features; extraer a `utils` o a una clase compartida en `core` si aplica.
- **Manejo de excepciones consistente**: usar siempre la jerarquía existente (`BusinessException`, `ValidationException`, `ConflictException`, `NotFoundException`, `TechnicalException`) en vez de excepciones genéricas (`RuntimeException`, `Exception`) — Sonar marca esto como code smell.
- **Sin secretos ni credenciales hardcodeadas**: usar Doppler/Azure Key Vault; nunca literales de tokens, passwords o connection strings en el código (bloqueante en el Quality Gate de seguridad).
- **Nombres e imports limpios**: sin imports no usados, variables sin uso, código comentado muerto ni `TODO` sin ticket asociado — todo esto dispara issues en SonarCloud.
- **Validadores**: nuevas reglas de validación deben seguir el patrón Composite existente (`*Rule` → `*RuleImpl`), no lógica de validación inline en el caso de uso o el controller.

## Convenciones de commits/PRs
No hay convención formal documentada en el repo. CI se dispara en `develop` y en PRs contra `develop` vía Azure Pipelines. Usar mensajes de commit convencionales por defecto (`feat:`, `fix:`, `chore:`, etc.) salvo indicación distinta.

## Herramientas no obvias a tener en cuenta
- **Doppler**: gestión de secretos en runtime, además de Azure Key Vault.
- **SurrealDB**: única base de datos del proyecto, cubre tanto necesidades relacionales como no relacionales (reemplaza el esquema anterior con PostgreSQL/Kafka/MongoDB). No tratarla como una BD SQL tradicional ni asumir que hay un motor relacional separado.
- **LangChain4j + Ollama local**: motor por defecto para traducción de mensajes (fallback/alternativa: OpenAI).
- **Maven Wrapper (`mvnw`)**: siempre preferirlo sobre un `mvn` global.

## Documentación de referencia
- `ArquitecturaEjemplo.md` — documento maestro de arquitectura hexagonal.
- `docs/azure-deployment.md` — despliegue en Azure.