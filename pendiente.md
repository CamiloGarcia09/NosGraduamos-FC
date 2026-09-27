# Pendientes de diseno de endpoints de mensajes

## Identificacion de mensajes

- Usar el UUID interno e inmutable como identificador canonico del mensaje.
- No usar el titulo o nombre del mensaje en el path porque puede cambiar, repetirse o contener caracteres especiales.
- Mantener `code` como identificador funcional para busquedas, no como identidad tecnica principal.
- Endpoint canonico propuesto:

```http
GET /messageucolab/v1/messages/{messageId}
```

- Busqueda por codigo propuesta:

```http
GET /messageucolab/v1/messages?code={messageCode}
```

## Traduccion de mensajes

- Cambiar la generacion dinamica de traducciones de `GET` a `POST`, porque usa IA, consume recursos y genera un resultado dinamico.
- Endpoint propuesto:

```http
POST /messageucolab/v1/messages/{messageId}/translations
```

- Body propuesto:

```json
{
  "sourceLanguage": "auto",
  "targetLanguage": "en"
}
```

- Si las traducciones se persisten, habilitar posteriormente su consulta:

```http
GET /messageucolab/v1/messages/{messageId}/translations?targetLanguage=en
```

## Creacion de mensajes

- Eliminar `application` del body. El servidor debe obtener el nombre a partir de `applicationId` y nunca confiar en ambos valores enviados por el cliente.
- Evaluar la eliminacion de `applicationId` y `environmentId` del body cuando se use Bearer, porque ambos ya pertenecen al contexto activo.
- Mantener `functionalityId`, porque la funcionalidad no forma parte del contexto activo, y validar que pertenezca a la aplicacion seleccionada.
- Evitar datos redundantes que puedan contradecir el contexto o los UUID enviados.
- Body objetivo con contexto activo:

```json
{
  "code": "UUID_SMOKE",
  "title": "UUID smoke test",
  "content": "Catalog UUID migration works",
  "typeId": "0c71601d-96b3-417e-b385-06b10ba126d9",
  "categoryId": "3b337f14-dde1-436e-829a-eebacb556eb4",
  "statusId": "4223b3dc-c991-4603-a746-d37ce5ecd976",
  "functionalityId": "5a8e2c74-1d39-4f6b-b7c2-0e9a3d5f8164",
  "messageEnvironmentStateId": "10ddbab2-352e-48de-b9eb-de0895ddb944"
}
```

## Estructura REST propuesta

Unificar creacion y consulta bajo la misma coleccion plural:

```http
POST /messageucolab/v1/messages
GET  /messageucolab/v1/messages
GET  /messageucolab/v1/messages/{messageId}
GET  /messageucolab/v1/messages?code={messageCode}
POST /messageucolab/v1/messages/{messageId}/translations
```

Esto reemplazaria la inconsistencia actual entre `POST /application/message` y `GET /application/messages`.

## Compatibilidad y migracion

- Definir si los endpoints actuales se mantendran temporalmente durante una fase de deprecacion.
- Actualizar controllers, casos de uso, DTO, validaciones, rutas de Kong y OpenAPI en una misma intervencion.
- Mantener compatibilidad con Bearer y Token legacy mientras sigan vigentes las fases de migracion de seguridad.
- Agregar pruebas unitarias y contractuales para UUID, busqueda por codigo, contexto activo y traduccion.
