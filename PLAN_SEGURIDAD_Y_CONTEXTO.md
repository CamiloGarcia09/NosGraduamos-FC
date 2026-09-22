# Plan de seguridad, autorizacion y contexto activo

## Proposito

Este documento registra la migracion del esquema de seguridad de MessageUcoLab. Debe actualizarse en cada sesion donde se tome una decision o se modifique codigo relacionado con autenticacion, autorizacion, tokens, organizaciones o contexto activo.

El objetivo final es separar claramente estas responsabilidades:

```text
Proveedor de identidad -> autentica: quien es el usuario o servicio
MessageUcoLab          -> autoriza: que puede hacer dentro del negocio
Contexto activo        -> determina: en que organizacion, aplicacion y ambiente trabaja
```

El token emitido por el proveedor se recibira y validara sin modificarlo, enriquecerlo ni reemplazarlo para transportar el contexto de MessageUcoLab.

## Estado general

| Campo | Valor |
|---|---|
| Ultima actualizacion | 2026-09-21 |
| Estado global | Implementacion gradual en curso |
| Fase actual | Fase 2 - Introducir identidad externa simulada (pendiente de inicio) |
| Proxima implementacion | Crear el modelo de identidad externa y su puerto de resolucion simulado |
| Bloqueo actual | Ninguno para iniciar la Fase 2; faltan datos del proveedor para la integracion real |

### Convenciones de estado

| Estado | Significado |
|---|---|
| `PENDIENTE` | No se ha iniciado |
| `EN CURSO` | Se esta trabajando actualmente |
| `PARCIAL` | Tiene avances, pero no cumple todos sus criterios de salida |
| `BLOQUEADA` | Requiere una decision o dependencia externa |
| `COMPLETADA` | Implementada, probada y documentada |

## Decisiones confirmadas

- Se incluira la jerarquia `Organizacion -> Aplicacion -> Ambiente`.
- El token propietario actual convivira temporalmente con el flujo nuevo.
- El token externo se usara para identificar al usuario o servicio, no para transportar el ambiente activo.
- MessageUcoLab sera responsable de los permisos de negocio.
- El identificador estable de una identidad externa sera la combinacion `issuer + subject`.
- El correo sera un atributo informativo y no la llave principal de la identidad.
- El token original no se modificara, refirmara ni envolvera dentro de otro token.
- No se almacenaran tokens completos en SurrealDB o Redis.
- El contexto activo se administrara en el backend.
- SurrealDB sera la fuente persistente del ultimo contexto y Redis permitira su consulta rapida.
- La migracion sera gradual para no interrumpir las consultas que dependen del token actual.
- Inicialmente se asumira un contexto activo por identidad. La estrategia para sesiones simultaneas se revisara antes de implementar la Fase 4.

## Estado actual del sistema

El flujo existente no usa realmente el `SecurityAdapter` simulado. Actualmente existe otro mecanismo propietario que:

1. Genera tokens con RSA.
2. Persiste informacion del token en SurrealDB.
3. Guarda claves privadas mediante Doppler.
4. Recibe el token en el header `Token`.
5. Obtiene el `environmentId` asociado al token.
6. Coloca el ambiente como atributo de la peticion HTTP.

Riesgos conocidos que deben atenderse antes o durante la migracion:

- La expiracion persistida del token no se verifica al autorizar una peticion.
- El listado de mensajes puede quedar accesible sin la proteccion esperada.
- Algunas consultas pueden aceptar un ambiente enviado por el cliente.
- La creacion de mensajes puede usar aplicacion y ambiente recibidos en el body sin compararlos correctamente con el contexto autorizado.
- No existe un modelo de organizaciones, usuarios, membresias, roles o permisos de negocio.
- Los logs que exponian tokens, cuerpos HTTP o material criptografico fueron saneados en la Fase 1.
- El adaptador simulado se habilita por defecto si falta configuracion y actualmente no participa en el flujo efectivo.

## Flujo objetivo

```text
Cliente
  -> envia Authorization: Bearer <token original>
API Gateway o backend
  -> valida autenticidad, emisor, audiencia y expiracion
MessageUcoLab
  -> obtiene issuer + subject
  -> resuelve el contexto activo en Redis/SurrealDB
  -> verifica permisos de negocio
Caso de uso
  -> opera solamente dentro del contexto autorizado
```

Respuestas esperadas:

- `401 Unauthorized`: token ausente, invalido o expirado.
- `403 Forbidden`: identidad valida sin permiso sobre la operacion o el contexto.
- `404 Not Found`: recurso inexistente dentro del contexto permitido.
- `409 Conflict`: seleccion de contexto inconsistente con la jerarquia de negocio.

## Resumen de fases

| Fase | Nombre | Estado | Resultado principal |
|---|---|---|---|
| 0 | Definir contrato de seguridad | `PARCIAL` | Responsabilidades y contrato con el proveedor definidos |
| 1 | Asegurar el flujo actual | `COMPLETADA` | El token existente no permite cruzar ambientes ni exponer secretos |
| 2 | Introducir identidad externa simulada | `PENDIENTE` | Un bearer token identifica al principal sin contener contexto |
| 3 | Modelar organizaciones y autorizacion | `PENDIENTE` | MessageUcoLab decide a que recursos accede cada identidad |
| 4 | Implementar contexto activo | `PENDIENTE` | El backend recuerda y valida el contexto seleccionado |
| 5 | Migrar operaciones al contexto | `PENDIENTE` | Los casos de uso dejan de confiar en IDs controlados por el cliente |
| 6 | Integrar el proveedor real | `PENDIENTE` | El adaptador real reemplaza al simulador |
| 7 | Retirar el mecanismo propietario | `PENDIENTE` | Se eliminan el token RSA y sus componentes exclusivos |

## Fase 0 - Definir el contrato de seguridad

**Estado:** `PARCIAL`

### Alcance

- [x] Separar autenticacion externa, autorizacion de negocio y contexto activo.
- [x] Elegir `issuer + subject` como identidad estable.
- [x] Decidir que el token no transportara el ambiente.
- [x] Decidir que se incluira la entidad Organizacion.
- [x] Elegir una migracion con convivencia temporal.
- [ ] Confirmar si el token del proveedor sera JWT u opaco.
- [ ] Obtener el `issuer` esperado.
- [ ] Obtener el `audience` esperado.
- [ ] Obtener la URL JWKS o el endpoint de introspeccion.
- [ ] Confirmar los claims disponibles.
- [ ] Definir como se autenticaran los servicios no humanos.
- [ ] Confirmar si el API Gateway validara el token.
- [ ] Definir como se protegera la comunicacion Gateway-backend.
- [ ] Definir si el navegador usara BFF y cookie `HttpOnly` o bearer directo.

### Criterio de salida

Existe un contrato documentado con el proveedor que permite validar tokens sin asumir detalles tecnicos ni incluir dependencias del proveedor en `core`.

## Fase 1 - Asegurar el flujo actual

**Estado:** `COMPLETADA`

### Alcance

- [x] Validar la fecha de expiracion del token actual.
- [x] Proteger el endpoint de listado de mensajes.
- [x] Impedir consultas globales cuando no exista un ambiente autorizado.
- [x] Usar el ambiente derivado del token en vez del enviado por query.
- [x] Impedir la creacion de mensajes en otro ambiente.
- [x] Validar la relacion entre aplicacion, funcionalidad y ambiente.
- [x] Eliminar tokens y claves privadas de los logs.
- [x] Homogeneizar las respuestas `401` del mecanismo actual y los rechazos de autorizacion como `403`.
- [x] Crear pruebas unitarias para token vigente, expirado y en el limite exacto de expiracion.
- [x] Crear o actualizar pruebas unitarias para estas reglas.
- [x] Ejecutar las pruebas de los modulos afectados.

### Criterios de salida

- [x] Un token del ambiente A no puede leer informacion del ambiente B.
- [x] Un token del ambiente A no puede escribir en el ambiente B.
- [x] Un token expirado recibe `401`.
- [x] Omitir el ambiente no produce una consulta global.
- [x] Los logs no contienen tokens ni claves privadas.
- [x] Las pruebas existentes y nuevas pasan correctamente.

### Fuera de alcance

- Redis para contexto activo.
- Modelo de organizaciones y permisos.
- Integracion con el proveedor real.
- Retiro del token propietario.

## Fase 2 - Introducir identidad externa simulada

**Estado:** `PENDIENTE`

### Alcance

- [ ] Crear en `core` un modelo de identidad sin dependencias web o de Spring.
- [ ] Crear un puerto para validar y resolver una identidad externa.
- [ ] Hacer que el resultado incluya `issuer`, `subject`, correo, tipo de principal y expiracion.
- [ ] Reemplazar conceptualmente la generacion del adaptador simulado por validacion de identidad.
- [ ] Activar el simulador solamente mediante perfil o propiedad explicita de desarrollo.
- [ ] Recibir el token externo mediante `Authorization: Bearer`.
- [ ] Evitar que el bearer token aparezca en logs o claves de cache.
- [ ] Mantener temporalmente el header `Token` para consumidores del flujo legado.
- [ ] Probar token simulado valido, invalido y expirado.

### Criterio de salida

Una peticion puede convertirse en una identidad estable mediante un token simulado, sin obtener organizacion, aplicacion o ambiente desde ese token.

## Fase 3 - Modelar organizaciones y autorizacion

**Estado:** `PENDIENTE`

### Modelo inicial esperado

```text
Organization
Application -> Organization
Environment -> Application
ExternalIdentity
Membership
Role
Permission
RoleAssignment con alcance
```

### Permisos iniciales propuestos

- `CONTEXT_SELECT`
- `MESSAGE_READ`
- `MESSAGE_CREATE`
- `MESSAGE_TRANSLATE`
- `APPLICATION_CREATE`
- `ENVIRONMENT_CREATE`
- `FUNCTIONALITY_CREATE`

### Alcance

- [ ] Crear la entidad y persistencia de Organizacion.
- [ ] Asociar cada Aplicacion con una Organizacion.
- [ ] Persistir identidades externas por `issuer + subject`.
- [ ] Modelar membresias, roles, permisos y asignaciones con alcance.
- [ ] Crear puertos de consulta de autorizaciones en `core`.
- [ ] Implementar politicas de autorizacion como reglas de negocio.
- [ ] Diferenciar consistentemente `401` y `403`.
- [ ] Filtrar catalogos segun los recursos autorizados.
- [ ] Probar acceso permitido y denegado entre organizaciones y aplicaciones.

### Criterio de salida

MessageUcoLab puede responder si una identidad tiene un permiso sobre una organizacion, aplicacion o ambiente sin depender de roles internos del proveedor.

## Fase 4 - Implementar contexto activo

**Estado:** `PENDIENTE`

### Modelo esperado

```text
ActiveContext
- organizationId
- applicationId
- environmentId
- updatedAt
```

### Alcance

- [ ] Decidir el comportamiento de varias pestanas o dispositivos para una misma identidad.
- [ ] Crear el modelo y los puertos de contexto en `core`.
- [ ] Persistir el ultimo contexto en SurrealDB.
- [ ] Implementar cache distribuida del contexto en Redis.
- [ ] No usar el token completo como clave de Redis.
- [ ] Crear `GET /messageucolab/v1/me/contexts`.
- [ ] Crear `GET /messageucolab/v1/me/context`.
- [ ] Crear `PUT /messageucolab/v1/me/context`.
- [ ] Verificar membresia, jerarquia y permiso `CONTEXT_SELECT` antes de cambiarlo.
- [ ] Definir el comportamiento cuando no exista contexto activo.
- [ ] Probar aislamiento, expiracion de cache, cambio y recuperacion desde persistencia.

### Criterio de salida

La identidad puede seleccionar un contexto autorizado, recuperarlo en peticiones posteriores y conservarlo aunque Redis pierda la entrada cacheada.

## Fase 5 - Migrar operaciones al contexto

**Estado:** `PENDIENTE`

### Orden de migracion

- [ ] Consulta y listado de mensajes.
- [ ] Traduccion de mensajes.
- [ ] Creacion de mensajes.
- [ ] Consulta de catalogos.
- [ ] Administracion de aplicaciones, ambientes y funcionalidades.

### Reglas

- [ ] Los casos de uso reciben identidad y contexto tipados.
- [ ] Los controllers no implementan reglas de autorizacion de negocio.
- [ ] `core` no depende de `HttpServletRequest`, Spring Security o Redis.
- [ ] Los IDs implicitos en el contexto se eliminan de queries y bodies cuando sea posible.
- [ ] Si un ID debe permanecer por compatibilidad, se exige coincidencia con el contexto.
- [ ] Cada operacion verifica el permiso correspondiente antes de acceder al repositorio.
- [ ] Se agregan pruebas de aislamiento entre organizaciones, aplicaciones y ambientes.

### Criterio de salida

Ninguna operacion migrada puede seleccionar su alcance de negocio mediante IDs arbitrarios enviados por el cliente.

## Fase 6 - Integrar el proveedor real

**Estado:** `PENDIENTE`

### Alcance

- [ ] Implementar el adaptador con el contrato confirmado en la Fase 0.
- [ ] Validar firma o introspeccion, `issuer`, `audience` y expiracion.
- [ ] Configurar secretos y endpoints externamente.
- [ ] Reemplazar el simulador sin modificar los casos de uso.
- [ ] Configurar la confianza entre Gateway y backend.
- [ ] Actualizar OpenAPI para `Authorization: Bearer`.
- [ ] Validar el flujo de navegador o BFF definido en la Fase 0.
- [ ] Ejecutar pruebas de integracion del flujo completo.

### Criterio de salida

El proveedor real autentica las peticiones y MessageUcoLab conserva de forma independiente la autorizacion y el contexto.

## Fase 7 - Retirar el mecanismo propietario

**Estado:** `PENDIENTE`

### Alcance

- [ ] Confirmar que no quedan consumidores del header `Token`.
- [ ] Retirar el endpoint de emision de tokens propietarios.
- [ ] Retirar la generacion y validacion RSA exclusiva de esos tokens.
- [ ] Retirar secretos Doppler exclusivos del mecanismo anterior.
- [ ] Retirar tabla, repositorios y casos de uso de tokens obsoletos.
- [ ] Eliminar configuraciones y documentacion del mecanismo anterior.
- [ ] Ejecutar pruebas de regresion y seguridad.

### Criterio de salida

Todas las peticiones protegidas usan la identidad del proveedor y no queda codigo activo del token propietario.

## Reglas para actualizar este documento

En cada sesion de trabajo relacionada con este plan se debe:

1. Actualizar la fecha de `Ultima actualizacion`.
2. Actualizar `Estado global`, `Fase actual`, `Proxima implementacion` y `Bloqueo actual`.
3. Cambiar el estado de la fase intervenida.
4. Marcar solamente las tareas realmente implementadas y verificadas.
5. Registrar archivos modificados y pruebas ejecutadas.
6. Registrar decisiones nuevas o cambios de alcance.
7. Registrar bloqueos y riesgos descubiertos.
8. Agregar una entrada al historial de cambios.

Una fase solo puede marcarse `COMPLETADA` cuando cumple sus criterios de salida y sus pruebas han sido ejecutadas exitosamente.

## Registro de la fase actual

### Archivos modificados

- `PLAN_SEGURIDAD_Y_CONTEXTO.md`: creacion del plan y mecanismo de seguimiento.
- `core/src/main/java/co/edu/uco/application/usecase/VerifyAccessUseCase.java`: validacion de expiracion con reloj UTC y traduccion de token inexistente, inactivo o vencido a `UnauthorizedException`.
- `core/src/test/java/co/edu/uco/application/usecase/VerifyAccessUseCaseTest.java`: cobertura de token vigente, inexistente, vencido y vencido exactamente en el instante actual.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/config/WebConfig.java`: registro explicito del listado de mensajes en el interceptor de token.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/primaryadapters/MessagesController.java`: retiro del parametro publico `environmentId` en el listado.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/primaryadapters/controller/MessagesControllerImpl.java`: uso del ambiente autenticado almacenado en la peticion.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/secondaryadapters/repository/surreal/impl/MessageSurrealRepositoryAdapterImpl.java`: eliminacion del filtro global `WHERE TRUE` cuando falta el ambiente.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/config/WebConfigTest.java`: verificacion del registro de la ruta protegida.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/primaryadapters/controller/MessagesControllerImplTest.java`: verificacion del ambiente autenticado en el listado.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/secondaryadapters/repository/surreal/impl/MessageSurrealRepositoryAdapterImplTest.java`: verificacion de que un ambiente ausente nunca genera una consulta global.
- `core/src/main/java/co/edu/uco/application/primaryports/facade/message/CreateMessageUseCaseFacade.java`: propagacion explicita del ambiente autenticado.
- `core/src/main/java/co/edu/uco/application/primaryports/facade/message/impl/CreateMessageUseCaseFacadeImpl.java`: delegacion del DTO junto con el ambiente autenticado.
- `core/src/main/java/co/edu/uco/application/usecase/handling/HandlingCreateMessagePort.java`: contrato de creacion extendido con el ambiente autenticado.
- `core/src/main/java/co/edu/uco/application/usecase/CreateMessageUseCase.java`: persistencia exclusiva con el ambiente autenticado.
- `core/src/main/java/co/edu/uco/application/usecase/validator/message/CreateMessageCompositeValidator.java`: integracion de la regla de contexto en la validacion compuesta.
- `core/src/main/java/co/edu/uco/application/usecase/validator/message/CreateMessageContextRule.java`: contrato de la regla de contexto de creacion.
- `core/src/main/java/co/edu/uco/application/usecase/validator/message/CreateMessageContextRuleImpl.java`: validacion de ambiente autenticado y jerarquia aplicacion-entorno-funcionalidad mediante identificadores UUID equivalentes y rechazos `403`.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/config/UseCaseRuleConfig.java`: composicion Spring de la regla de contexto para mantener `core` libre de frameworks.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/primaryadapters/controller/CreateMessageControllerImpl.java`: extraccion del ambiente autenticado desde la peticion.
- `utils/src/main/java/co/edu/uco/crosscutting/catalog/MessageCatalogCodeEnum.java`: codigos `FUN_145` y `FUN_146` para las nuevas reglas y correccion de la descripcion de `FUN_036`.
- `deployment/docker/scripts/redis/CatalogMessageInit.sh`: mensajes funcionales para contexto y funcionalidad no autorizados, y mensajes tecnicos saneados para no solicitar valores sensibles.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/secondaryadapters/security/SecurityAdapter.java`: validacion del token simulado sin registrar su valor.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/secondaryadapters/encryption/JavaSecurityEncryptTokenAdapter.java`: errores criptograficos sin claves privadas, firmas ni nombres de secreto como argumentos del logger.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/secondaryadapters/repository/surreal/impl/TokenSurrealRepositoryAdapterImpl.java`: logs genericos sin ID ni consulta SurrealQL del token.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/secondaryadapters/presenter/rest/HttpPresenterAdapter.java`: respuestas HTTP escritas al cliente sin registrar el cuerpo serializado y respeto del estado `403` tipado.
- `utils/src/main/java/co/edu/uco/crosscutting/exceptions/UnauthorizedException.java`: excepcion de autenticacion con estado HTTP `401`.
- `utils/src/main/java/co/edu/uco/crosscutting/exceptions/ForbiddenException.java`: excepcion de autorizacion con estado HTTP `403`.
- `infrastructure/src/main/java/co/edu/uco/infraestructure/primaryadapters/interceptors/TokenHeaderInterceptor.java`: respuesta `401` para token ausente, invalido, vencido o revocado durante la resolucion del ambiente.
- `ArquitecturaEjemplo.md`: documentacion actualizada de estados de seguridad y traduccion de excepciones HTTP.
- `core/src/test/java/co/edu/uco/application/usecase/CreateMessageUseCaseTest.java`: propagacion y persistencia del ambiente autenticado.
- `core/src/test/java/co/edu/uco/application/usecase/validator/message/CreateMessageCompositeValidatorTest.java`: integracion de la regla de contexto.
- `core/src/test/java/co/edu/uco/application/usecase/validator/message/CreateMessageContextRuleImplTest.java`: cobertura de la jerarquia, escenarios `403` y UUID equivalentes con distinta capitalizacion.
- `core/src/test/java/co/edu/uco/application/primaryports/facade/message/impl/CreateMessageUseCaseFacadeImplTest.java`: propagacion de facade a caso de uso.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/primaryadapters/controller/CreateMessageControllerImplTest.java`: uso del ambiente autenticado por el controller.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/secondaryadapters/security/SecurityAdapterTest.java`: verifica validacion valida e invalida sin pasar el token al logger.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/secondaryadapters/encryption/JavaSecurityEncryptTokenAdapterTest.java`: verifica errores sin material criptografico como argumentos del logger.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/secondaryadapters/repository/surreal/impl/TokenSurrealRepositoryAdapterImplTest.java`: verifica mensajes genericos al persistir y consultar tokens.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/secondaryadapters/presenter/rest/HttpPresenterAdapterTest.java`: verifica que los cuerpos de respuesta no se envian al logger y que `ForbiddenException` produce `403`.
- `utils/src/test/java/co/edu/uco/crosscutting/exceptions/UnauthorizedExceptionTest.java`: verifica mensaje, tipo, ubicacion y estado `401`.
- `utils/src/test/java/co/edu/uco/crosscutting/exceptions/ForbiddenExceptionTest.java`: verifica mensaje, tipo, ubicacion y estado `403`.
- `infrastructure/src/test/java/co/edu/uco/infraestructure/primaryadapters/interceptors/TokenHeaderInterceptorTest.java`: verifica `401` en todos los rechazos del token legado.

### Pruebas ejecutadas

- `VerifyAccessUseCaseTest`: 7 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Pruebas focalizadas del listado: 20 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Pruebas focalizadas de creacion: 32 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Pruebas focalizadas de saneamiento de logs: 39 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Pruebas focalizadas de normalizacion HTTP: 41 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Reactor completo con `clean verify`: `utils`, `core` e `infrastructure` finalizaron correctamente.
- Suite `utils`: 186 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Suite `core`: 446 pruebas, 0 fallos, 0 errores y 0 omitidas.
- Suite `infrastructure`: 357 pruebas, 0 fallos, 0 errores y 0 omitidas.
- JaCoCo: los controles de cobertura de linea y rama, configurados con minimo de 80 %, se cumplieron en todos los modulos.
- Auditoria de `VerifyAccessUseCaseTest`: prueba un resultado real, usa excepcion de dominio, no usa infraestructura concreta y evita acceder a secretos o criptografia cuando el token esta vencido.
- Auditoria del listado: las pruebas verifican la ruta protegida, el origen autenticado del ambiente y la ausencia del fallback global; no contienen asserts triviales ni pruebas deshabilitadas.
- Auditoria de creacion: las pruebas usan puertos simulados, excepciones de dominio y cubren completamente las lineas y ramas de la nueva regla de contexto.
- Auditoria de logs: las pruebas verifican argumentos concretos del `LoggingPort`, mockean SurrealDB y no dependen de infraestructura real; no contienen asserts triviales ni pruebas deshabilitadas.
- Auditoria HTTP: las pruebas verifican estados reales, excepciones especificas de la jerarquia y puertos simulados; no usan infraestructura concreta ni asserts triviales.

### Pendiente inmediato

Iniciar la Fase 2 con el modelo de identidad externa en `core` y un puerto de resolucion que no incluya organizacion, aplicacion ni ambiente en el token.

## Historial de cambios

| Fecha | Fase | Cambio | Estado resultante |
|---|---|---|---|
| 2026-09-21 | Planificacion | Creacion del documento; se registran decisiones, fases, riesgos y criterios de salida | Fase 0 `PARCIAL`; Fase 1 `PENDIENTE` |
| 2026-09-21 | Fase 1 | Se valida la expiracion antes de acceder a secretos o criptografia y se agregan pruebas de vigencia y limite temporal | Fase 1 `EN CURSO`; incremento de expiracion completado |
| 2026-09-21 | Fase 1 | Se protege el listado, se obtiene el ambiente desde el token y se elimina el fallback global de SurrealDB | Fase 1 `EN CURSO`; aislamiento de lectura completado |
| 2026-09-21 | Fase 1 | La creacion usa el ambiente autenticado y valida la jerarquia con aplicacion y funcionalidad | Fase 1 `EN CURSO`; aislamiento de escritura completado |
| 2026-09-21 | Fase 1 | Se comparan contextos mediante UUID, se mueve la composicion Spring a infraestructura y se completa la cobertura de la regla | Fase 1 `EN CURSO`; aislamiento de escritura auditado |
| 2026-09-21 | Fase 1 | Se eliminan tokens, material criptografico, consultas de token y cuerpos HTTP de los logs | Fase 1 `EN CURSO`; saneamiento de logs completado |
| 2026-09-21 | Fase 1 | Se tipan los rechazos de autenticacion como `401` y los de contexto como `403` | Fase 1 `COMPLETADA`; todos los criterios de salida verificados |
