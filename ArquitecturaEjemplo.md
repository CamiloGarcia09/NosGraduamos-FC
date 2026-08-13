# Esquema de arquitectura
 
Este proyecto esta organizado con una arquitectura hexagonal / clean architecture orientada por
features. La idea central es que la infraestructura depende de la aplicacion, pero la aplicacion
no depende de detalles externos como REST, SurrealDB, SSE, Docker, Azure Key Vault o Prometheus.
 
## Vista general
 
```text
Cliente HTTP / Frontend
        |
        v
infraestructure.primaryadapters.controller
        |
        v
application.features.<modulo>.<caso>.primaryports.interactor
        |
        v
application.features.<modulo>.<caso>.usecase
        |
        +--> domain + rules + rule validator
        |
        +--> application.secondaryports.repository
        |             |
        |             v
        |    infraestructure.secondaryadapters.repository
        |             |
        |             v
        |          SurrealDB
        |
        +--> application.features.<modulo>.<caso>.secondaryports.publisher
                      |
                      v
             infraestructure.secondaryadapters.publisher
                      |
                      v
                  SSE / eventos
```
 
## Capas principales
 
```text
src/main/java/.../
|-- init
|   `-- Clase Spring Boot principal
|
|-- crosscutting
|   |-- exceptions
|   `-- helpers
|
|-- application
|   |-- primaryports
|   |-- secondaryports
|   |   |-- entity
|   |   |-- repository
|   |   `-- publisher
|   |-- usecase
|   |   |-- domain
|   |   `-- validator
|   |-- common
|   `-- features
|
`-- infraestructure
    |-- primaryadapters
    |   |-- controller
    |   |-- response
    |   `-- exceptionhandler
    |-- secondaryadapters
    |   |-- repository
    |   |-- publisher
    |   `-- surrealdb
    `-- config
```
 
## Responsabilidad de cada capa
 
### init
 
Punto de entrada de Spring Boot. Solo arranca la aplicacion y define el paquete base de escaneo.
 
Ejemplo en este proyecto:
 
```text
CatalogoParametrosUcoLabApplication
```
 
### crosscutting
 
Codigo transversal que no pertenece a un caso de uso especifico:
 
- Excepciones de negocio, validacion, conflicto, no encontrado y tecnicas.
- Helpers para texto y UUID.
 
Esta capa puede ser usada por aplicacion e infraestructura.
 
### application
 
Contiene el nucleo de negocio. Es la parte mas importante para migrar.
 
Aqui viven:
 
- Puertos primarios: contratos que invocan los adaptadores de entrada.
- Puertos secundarios: contratos que necesita la aplicacion para salir a persistencia, eventos u otros servicios.
- Casos de uso.
- Dominios por operacion.
- Validadores y reglas de negocio.
- Entidades de salida hacia repositorios.
 
La aplicacion no deberia depender directamente de controladores, SurrealDB, REST, Docker ni frameworks externos
de infraestructura.
 
### infraestructure
 
Implementa los detalles tecnicos:
 
- Controladores REST/WebFlux.
- Respuestas HTTP.
- Manejador global de excepciones.
- Repositorios concretos contra SurrealDB.
- Cliente SurrealDB.
- Publicadores concretos basados en Reactor Sinks.
- Configuracion CORS.
 
Esta capa si depende de Spring, WebFlux, SurrealDB y cualquier tecnologia externa.
 
## Modelo de dominio principal
 
El catalogo se organiza jerarquicamente asi:
 
```text
Organizacion
    |
    v
Aplicacion
    |
    v
Modulo
    |
    v
Funcionalidad
    |
    v
Parametro
```
 
Entidades principales:
 
```text
OrganizacionEntity
- id
- nombre
 
AplicacionEntity
- id
- nombre
- idOrganizacion
- activa
- fechaInicio
- fechaFinal
 
ModuloEntity
- id
- nombre
- idAplicacion
- activo
- fechaInicio
- fechaFinal
 
FuncionalidadEntity
- id
- nombre
- idModulo
- activo
- fechaInicio
- fechaFinal
 
ParametroEntity
- id
- nombre
- idFuncionalidad
- idTipoParametro
- activo
```
 
## Patron por feature
 
Cada operacion se modela como una feature independiente. Ejemplo: crear parametro.
 
```text
application/features/parametro/crearparametro
|-- CrearParametro.java
|-- CrearParametroRuleValidator.java
|
|-- primaryports
|   |-- dto
|   |   |-- CrearParametroDtoRequest.java
|   |   `-- CrearParametroDtoInput.java
|   `-- interactor
|       |-- CrearParametroInteractor.java
|       |-- impl
|       |   `-- CrearParametroInteractorImpl.java
|       `-- mapper
|           `-- CrearParametroDtoMapper.java
|
|-- usecase
|   |-- crearparametroimpl
|   |   |-- CrearParametroImpl.java
|   |   `-- CrearParametroRuleValidatorImpl.java
|   `-- domain
|       |-- CrearParametroDomain.java
|       |-- exception
|       `-- rules
|           |-- ParametroNameIsNotNullRule.java
|           |-- ParametroNameIsNotEmptyRule.java
|           |-- ParametroNameDoesNotExistRule.java
|           `-- impl
|
`-- secondaryports
    |-- event
    |   `-- CrearParametroEvent.java
    `-- publisher
        `-- CrearParametroPublisher.java
```
 
Este mismo patron se repite para:
 
- crear
- actualizar
- eliminar
- consultar
 
Y para modulos funcionales como:
 
- organizacion
- aplicacion
- modulo
- funcionalidad
- parametro
 
## Flujo de una operacion de escritura
 
Ejemplo: crear parametro.
 
```text
1. ParametroController recibe POST /parametros
2. Controller invoca CrearParametroInteractor
3. Interactor convierte:
   CrearParametroDtoRequest -> CrearParametroDtoInput -> CrearParametroDomain
4. Interactor invoca CrearParametro
5. Caso de uso ejecuta CrearParametroRuleValidator
6. RuleValidator ejecuta reglas individuales
7. Caso de uso genera ID
8. Caso de uso convierte dominio a ParametroEntity
9. Caso de uso invoca ParametroRepository.save
10. Adaptador SurrealDbParametroRepository construye y ejecuta SurrealQL
11. Caso de uso publica CrearParametroEvent
12. Publisher emite evento a subscribers SSE
13. Controller retorna ResponseEntity con mensajes
```
 
## Flujo de una consulta
 
```text
1. Controller recibe GET
2. Controller invoca ConsultarXInteractor
3. Interactor llama repositorio o caso de consulta
4. Repositorio retorna entidades
5. Controller arma Response con lista de entidades y mensajes
```
 
En este proyecto las consultas son mas directas y tienen menos dominio/reglas que las operaciones de escritura.
 
## Puertos principales
 
### Puertos primarios
 
Contratos que usa la infraestructura de entrada para llamar la aplicacion.
 
```java
public interface InteractorWithOutReturn<T> {
    void execute(T data);
}
 
public interface InteractorWithReturn<T, R> {
    R execute(T data);
}
```
 
En cada feature se crean interfaces especificas, por ejemplo:
 
```text
CrearParametroInteractor
ActualizarParametroInteractor
EliminarParametroInteractor
ConsultarParametroInteractor
```
 
### Puertos secundarios
 
Contratos que la aplicacion necesita para hablar con el exterior.
 
Repositorios:
 
```text
OrganizacionRepository
AplicacionRepository
ModuloRepository
FuncionalidadRepository
ParametroRepository
```
 
Publicadores:
 
```text
Publisher<T>
CrearParametroPublisher
ActualizarParametroPublisher
EliminarParametroPublisher
...
```
 
## Adaptadores primarios
 
Son los controladores REST:
 
```text
OrganizacionController
AplicacionController
ModuloController
FuncionalidadController
ParametroController
```
 
Responsabilidades:
 
- Exponer endpoints HTTP.
- Recibir DTO request.
- Invocar interactors.
- Armar response HTTP.
- Exponer streams SSE de eventos.
- No implementar reglas de negocio.
 
Base de rutas:
 
```text
/catalogo-parametros/api/v1/organizaciones
/catalogo-parametros/api/v1/aplicaciones
/catalogo-parametros/api/v1/modulos
/catalogo-parametros/api/v1/funcionalidades
/catalogo-parametros/api/v1/parametros
```
 
Cada recurso suele tener:
 
```text
POST /
PUT /{id}
DELETE /{id}
GET /
GET /{id}
GET /events
```
 
## Adaptadores secundarios
 
### Persistencia
 
Cada repositorio de aplicacion tiene una implementacion concreta en infraestructura:
 
```text
ParametroRepository              -> SurrealDbParametroRepository
OrganizacionRepository           -> SurrealDbOrganizacionRepository
AplicacionRepository             -> SurrealDbAplicacionRepository
ModuloRepository                 -> SurrealDbModuloRepository
FuncionalidadRepository          -> SurrealDbFuncionalidadRepository
```
 
El cliente comun es:
 
```text
SurrealDbClient
SurrealDbProperties
```
 
Si se adapta a otro proyecto con PostgreSQL, MongoDB, JPA o una API externa, se mantiene el puerto y se reemplaza
solo el adaptador.
 
### Eventos
 
Los publishers concretos usan Reactor:
 
```text
Sinks.Many<Event>
Flux<Event>
```
 
Esto permite emitir eventos en memoria y exponerlos por SSE.
 
Si el nuevo proyecto requiere mensajeria real, esta es la pieza a reemplazar:
 
```text
CrearParametroPublisherImpl
ActualizarParametroPublisherImpl
...
```
 
## Reglas de negocio
 
Cada regla tiene una interfaz y una implementacion.
 
Ejemplo:
 
```text
ParametroNameDoesNotExistRule
ParametroNameDoesNotExistRuleImpl
```
 
Las reglas se agrupan en un validador por caso de uso:
 
```text
CrearParametroRuleValidator
CrearParametroRuleValidatorImpl
```
 
El caso de uso no conoce el detalle de cada validacion; solo llama:
 
```java
crearParametroRuleValidator.validate(data);
```
 
Tipos comunes de reglas:
 
- Campo no nulo.
- Campo no vacio.
- Longitud valida.
- Formato valido.
- Relacion existente.
- Nombre no duplicado.
- Entidad no usada por otra antes de eliminar.
 
## Manejo de errores
 
Hay excepciones transversales:
 
```text
BusinessException
ValidationException
ConflictException
NotFoundException
TechnicalException
```
 
Tambien existen excepciones por dominio/feature, por ejemplo:
 
```text
ParametroException
OrganizacionException
ModuloException
FuncionalidadException
AplicacionException
```
 
La infraestructura tiene un `GlobalExceptionHandler` que traduce excepciones a respuestas HTTP.
 
## Respuestas HTTP
 
Hay una respuesta base:
 
```text
Response
- mensajes: List<String>
```
 
Y respuestas especificas por recurso:
 
```text
ParametroResponse extends Response
- parametros: List<ParametroEntity>
 
OrganizacionResponse extends Response
- organizaciones: List<OrganizacionEntity>
```
 
La idea es devolver siempre:
 
- mensajes de resultado/error
- datos del recurso cuando aplica
 
## WebFlux y concurrencia
 
El proyecto usa:
 
```text
spring-boot-starter-webflux
Mono
Flux
ServerSentEvent
Schedulers.boundedElastic()
```
 
Aunque los endpoints retornan `Mono<ResponseEntity<...>>`, la logica interna es mayormente bloqueante.
Por eso el controller usa:
 
```java
Mono.fromCallable(() -> { ... }).subscribeOn(Schedulers.boundedElastic())
```
 
Si el nuevo proyecto no necesita WebFlux, se puede adaptar a Spring MVC sin cambiar la arquitectura interna.
 
## Observabilidad y despliegue
 
Componentes incluidos:
 
```text
Spring Boot Actuator
Micrometer
Prometheus
Grafana
Dockerfile multi-stage
docker-compose
Azure Key Vault para secretos
```
 
Configuracion relevante:
 
```text
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.show-details=always
management.metrics.export.prometheus.enabled=true
```
 
## Plantilla minima para adaptar a otro proyecto
 
Para crear un nuevo recurso llamado `producto`, con un caso de uso `crearproducto`, replicaria solo esto:
 
```text
application/features/producto/crearproducto
|-- CrearProducto.java
|-- CrearProductoRuleValidator.java
|-- primaryports
|   |-- dto
|   |   |-- CrearProductoDtoRequest.java
|   |   `-- CrearProductoDtoInput.java
|   `-- interactor
|       |-- CrearProductoInteractor.java
|       |-- impl/CrearProductoInteractorImpl.java
|       `-- mapper/CrearProductoDtoMapper.java
|-- usecase
|   |-- crearproductoimpl
|   |   |-- CrearProductoImpl.java
|   |   `-- CrearProductoRuleValidatorImpl.java
|   `-- domain
|       |-- CrearProductoDomain.java
|       |-- exception/ProductoException.java
|       `-- rules
|           |-- ProductoNombreIsNotNullRule.java
|           |-- ProductoNombreIsNotEmptyRule.java
|           |-- ProductoNombreDoesNotExistRule.java
|           `-- impl
`-- secondaryports
    |-- event/CrearProductoEvent.java
    `-- publisher/CrearProductoPublisher.java
 
application/secondaryports
|-- entity/ProductoEntity.java
`-- repository/ProductoRepository.java
 
infraestructure/primaryadapters
|-- controller/producto/ProductoController.java
`-- response/producto/ProductoResponse.java
 
infraestructure/secondaryadapters
|-- repository/producto/<Tecnologia>ProductoRepository.java
`-- publisher/producto/crearproducto/CrearProductoPublisherImpl.java
```
 
## Dependencias que deben respetarse
 
Regla principal:
 
```text
infraestructure --> application --> crosscutting
```
 
Evitar:
 
```text
application --> infraestructure
application --> controller
application --> SurrealDbClient
domain --> Spring Web
rules --> controller
```
 
Permitido:
 
```text
Controller -> Interactor
Interactor -> UseCase
UseCase -> Repository port
UseCase -> Publisher port
Repository adapter -> SurrealDbClient
Publisher adapter -> Reactor Sinks
```
 
## Que llevar al otro proyecto
 
Imprescindible:
 
- Separacion `application` / `infraestructure` / `crosscutting`.
- Puertos primarios para entrada.
- Puertos secundarios para salida.
- Caso de uso por operacion.
- Dominio por caso de uso.
- Reglas de negocio aisladas.
- Repositorios como interfaces en application.
- Adaptadores concretos fuera de application.
- Manejador global de excepciones.
 
Opcional segun necesidad:
 
- WebFlux.
- SSE.
- Reactor Sinks.
- SurrealDB.
- Azure Key Vault.
- Prometheus/Grafana.
- Docker Compose de monitoreo.
 
## Recomendacion practica
 
Para un proyecto nuevo, no copiaria todos los paquetes desde el inicio. Empezaria por una feature completa,
por ejemplo `crearproducto`, y despues replicaria el patron para `actualizar`, `eliminar` y `consultar`.
 
La unidad de migracion mas sana no es una clase suelta, sino una feature completa:
 
```text
DTO -> Mapper -> Interactor -> Domain -> RuleValidator -> Rules -> UseCase -> Ports -> Adapters
```
 
 
 