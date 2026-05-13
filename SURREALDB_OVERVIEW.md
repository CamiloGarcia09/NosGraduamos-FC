Resumen de SurrealDB en este proyecto

Este documento explica, en lenguaje sencillo y actualizado, cómo se usa SurrealDB en este proyecto y qué archivos lo controlan.

1) ¿Qué es SurrealDB aquí?
- SurrealDB se usa como una de las opciones de almacenamiento. En este proyecto guarda datos relacionados con mensajes y tokens: tablas como "token", "token_state", "message", "message_environment" y modelos de lectura.

2) Dónde está la configuración
- deployment/docker/.env: variables de entorno que definen host, puerto, usuario, contraseña, namespace y database (SURREALDBHOST, SURREALDBPORT, SURREALDBUSER, SURREALDBPASSWORD, SURREALDBNAMESPACE, SURREALDBDATABASE).
- deployment/docker/docker-compose.yml: define los servicios surrealdb y surrealdb-init (importa esquema y datos de ejemplo). Expone puertos, habilita autenticación y monta volúmenes.
- infrastructure/src/main/resources/application.properties: propiedades de Spring para conectarse desde la aplicación (prefijo surreal.*). La propiedad persistence.primary puede ser "surreal" para usar SurrealDB como almacenamiento principal.

3) Cómo se inicializa
- deployment/docker/scripts/bootstrap-surreal-dev.ps1: script que levanta SurrealDB, espera a que esté listo y ejecuta la importación del esquema (surreal-init.surql) y los datos semilla de desarrollo (surreal-seed.dev.surql).
- deployment/docker/scripts/surreal-init.surql: definición de tablas, campos e índices que la aplicación usa.
- deployment/docker/scripts/surreal-seed.dev.surql: crea datos de ejemplo (aplicación, entorno, mensajes, read models) y guarda identificadores en bootstrap_metadata para facilitar pruebas.

4) Cómo se conecta la aplicación Java
- infrastructure/src/main/java/.../SurrealDBProperties.java: clase que carga las propiedades (host, port, username, password, namespace, database, maxConnections).
- infrastructure/src/main/java/.../SurrealDBConfig.java: crea un bean Surreal (cliente) usando la librería Java de SurrealDB. Conecta vía WebSocket, hace signin con usuario, y selecciona namespace y database.

5) Adaptadores, mappers y modelos (novedades importantes)
La implementación de acceso a Surreal fue ampliada y ahora hay dos estilos coexistentes. A continuación se detalla qué hace cada clase y su responsabilidad dentro de la capa de infraestructura:

- Estilo "directo" (adaptadores que implementan los puertos del dominio):
  - infrastructure/src/main/java/.../surreal/impl/TokenSurrealAdapter.java
    - Responsabilidad: implementar los puertos del dominio y ejecutar consultas SurrealQL directamente usando el cliente Surreal. Construye las consultas (SELECT, INSERT, UPDATE, DELETE) y transforma las respuestas JSON en objetos de negocio (por ejemplo TokenData).
    - Comportamiento: realiza parsing manual de resultados, maneja errores de conexión y está pensado para acceso inmediato sin modelos intermedios.
  - infrastructure/src/main/java/.../surreal/impl/TokenStateSurrealAdapter.java
    - Responsabilidad: equivalente al anterior pero para el estado de tokens (Status/TokenState). Traduce filas de SurrealDB a objetos StatusTokenData usados por la aplicación.

- Estilo "repositorio + modelo + mapper" (más desacoplado):
  - Interfaces (adapter/repository):
    - infrastructure/.../surreal/TokenSurrealRepositoryAdapter.java
      - Responsabilidad: definir el contrato de persistencia (métodos para guardar, buscar por id/secret, listar por entorno, etc.) sin exponer detalles de SurrealDB.
    - infrastructure/.../surreal/TokenStateSurrealRepositoryAdapter.java
      - Responsabilidad: contrato para operaciones relacionadas con estados de token.
  - Implementaciones:
    - infrastructure/.../surreal/impl/TokenSurrealRepositoryAdapterImpl.java
      - Responsabilidad: implementación del contrato. Usa los modelos (TokenSurrealModel) y los mappers para convertir entre representación persistente y objetos de dominio. Encapsula la lógica de construcción de queries y el uso del cliente Surreal; también centraliza manejo de errores/transformaciones comunes.
      - Comportamiento típico: recibir TokenData del dominio, mapear a TokenSurrealModel, serializar campos necesarios, ejecutar upsert/insert en Surreal, mapear respuesta de vuelta a TokenData.
    - infrastructure/.../surreal/impl/TokenStateSurrealRepositoryAdapterImpl.java
      - Responsabilidad: análoga a la anterior para estados de token; maneja lookup por nombre/id y sincronización con tablas de estado.
  - Modelos simples usados internamente:
    - infrastructure/.../model/TokenSurrealModel.java
      - Campos y propósito: representación directa de la fila en SurrealDB. Campos comunes: id (record id), secretName, creationDate, expirationDate, environmentId, stateId. Diseñado para serialización/ deserialización JSON compatible con el cliente Surreal.
    - infrastructure/.../model/StatusTokenSurrealModel.java
      - Campos y propósito: representación de la tabla de estados (id, name). Facilita consultas y mapeos a objetos de dominio.
  - Mappers (conversión entre modelo y dominio):
    - infrastructure/.../data/TokenSurrealMapper.java
      - Responsabilidad: convertir TokenSurrealModel <-> TokenData (objeto de dominio). Maneja formatos de fecha, nombres de campos distintos y valores nulos. Centraliza las reglas de conversión para evitar duplicación.
    - infrastructure/.../data/TokenStateSurrealMapper.java
      - Responsabilidad: convertir StatusTokenSurrealModel <-> StatusTokenData.

Por qué importa: esta separación aporta pruebas más sencillas (se pueden mockear mappers o repositorios), reduce el acoplamiento entre dominio e infraestructura y hace explícita la forma en que se modelan los datos en Surreal. Las implementaciones del repositorio actúan como fachada: construyen queries, utilizan SurrealQLUtil cuando es necesario, y delegan la transformación a los mappers.

Notas sobre coexistencia y migración:
- El adaptador "directo" sigue presente como solución rápida/legacy; su uso puede permanecer para operaciones puntuales o scripts. Las nuevas funcionalidades deberían preferir el patrón repositorio+modelo+mapper.
- Al migrar, verificar las pruebas de integración contra SurrealDB para asegurar que los mappers conservan la semántica (por ejemplo formatos de fecha y nombres de columnas).

6) Utilidad para construir consultas
- infrastructure/.../surreal/impl/SurrealQLUtil.java: pequeño utilitario para escapar cadenas, crear literales de record id y formatear fechas para usar en consultas SurrealQL. Se sigue usando porque la versión del SDK no soporta binding nativo.

7) Constantes y nombres
- infrastructure/.../InfrastructureConstant.java: contiene nombres de tablas (SURREAL_TABLE_TOKEN, SURREAL_TABLE_TOKEN_STATE), nombres de beans y la propiedad SURREAL_CONFIG_PREFIX = "surreal".

8) Consideraciones y riesgos
- Credenciales: se leen de .env en Docker; en producción usar un gestor de secretos.
- Construcción de queries: se generan concatenando literales; es fundamental usar SurrealQLUtil.quote para evitar inyección.
- Coexistencia de adaptadores: hay dos estilos (directo y repositorio). Asegúrate cuál está activo por la propiedad persistence.primary y por condiciones de Spring (@ConditionalOnProperty). Podría haber duplicidad si no se controla.

9) Cómo probar localmente (rápido)
- Ejecutar desde deployment\docker: docker compose up -d surrealdb surrealdb-init
- O usar el script PowerShell: deployment\docker\scripts\bootstrap-surreal-dev.ps1 (levanta, espera e importa esquema/seed)
- Revisar que el contenedor surrealdb esté healthy y que bootstrap_metadata tenga datos (consulta en scripts o con /surreal sql).

10) Archivos clave (rutas relativas, actualizadas)
- deployment/docker/.env
- deployment/docker/docker-compose.yml
- deployment/docker/scripts/bootstrap-surreal-dev.ps1
- deployment/docker/scripts/surreal-init.surql
- deployment/docker/scripts/surreal-seed.dev.surql
- infrastructure/src/main/resources/application.properties
- infrastructure/src/main/java/co/edu/uco/infrastructure/configuration/SurrealDBProperties.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/configuration/SurrealDBConfig.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/surreal/TokenSurrealRepositoryAdapter.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/surreal/TokenStateSurrealRepositoryAdapter.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/surreal/model/TokenSurrealModel.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/surreal/model/StatusTokenSurrealModel.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/data/TokenSurrealMapper.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/data/TokenStateSurrealMapper.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/surreal/impl/TokenSurrealRepositoryAdapterImpl.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/surreal/impl/TokenStateSurrealRepositoryAdapterImpl.java
- infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/surreal/impl/SurrealQLUtil.java
- (Aún presente) infrastructure/src/main/java/co/edu/uco/infrastructure/adapter/secondary/repository/surreal/impl/TokenSurrealAdapter.java

11) Recomendaciones prácticas
- Mantener y usar los mappers: facilitan pruebas unitarias y desacoplan la capa de dominio de Surreal.
- Evaluar eliminar duplicidad: elegir un patrón (repositorio+model o adaptador directo) y migrar todo a ese patrón para evitar confusiones.
- Actualizar SDK de SurrealDB si es posible para usar binding en lugar de concatenar literales.
- No subir credenciales a repositorio; usar secretos o variables de entorno en CI/CD.

Si quieres, puedo:
- Actualizar este archivo para incluir ejemplos de uso (ej. código breve de cómo llamar upsert y find).
- Generar pruebas unitarias para TokenSurrealRepositoryAdapterImpl y TokenSurrealMapper.
- Detectar posibles duplicados activos (qué adaptador está realmente instanciado en tiempo de ejecución) y sugerir limpieza.

Fin del resumen actualizado.