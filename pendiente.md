# Diseno de endpoints de mensajes

## Identificacion de mensajes

- Usar el UUID interno e inmutable como identificador canonico del mensaje.
- No usar el titulo o nombre del mensaje en el path porque puede cambiar, repetirse o contener caracteres especiales.
- Mantener `code` como identificador funcional para busquedas, no como identidad tecnica principal.
- Endpoint canonico pendiente:

```http
GET /messageucolab/v1/messages/{messageId}
```

- Busqueda por codigo implementada:

```http
GET /messageucolab/v1/messages?code={messageCode}
```

## Traduccion de mensajes

- La generacion dinamica de traducciones usa `POST`, porque consume IA y genera un resultado dinamico.
- Mientras se implementa la consulta canonica por UUID, la traduccion conserva el codigo funcional:

```http
POST /messageucolab/v1/messages/{messageCode}/translations
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

- `application`, `applicationId`, `environmentId` y `messageEnvironmentStateId` fueron eliminados del body.
- El servidor resuelve el ambiente autenticado y deriva la aplicacion asociada sin confiar en datos redundantes del cliente.
- La relacion del mensaje con el ambiente se crea siempre en estado `Active`, resuelto por nombre desde el catalogo.
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
  "functionalityId": "5a8e2c74-1d39-4f6b-b7c2-0e9a3d5f8164"
}
```

## Estructura REST actual

Unificar creacion y consulta bajo la misma coleccion plural:

```http
POST /messageucolab/v1/messages
GET  /messageucolab/v1/messages
GET  /messageucolab/v1/messages?code={messageCode}
POST /messageucolab/v1/messages/{messageCode}/translations
```

Esta estructura reemplaza la inconsistencia anterior entre `POST /application/message` y `GET /application/messages`.

## Compatibilidad y migracion

- Las rutas anteriores se retiraron mediante corte inmediato; no existe una fase de compatibilidad o deprecacion.
- La creacion de una aplicacion aprovisiona atomicamente ambientes `Develop`, `Testing` y `Production` activos; no existe un endpoint de creacion manual de ambientes.
- Mantener compatibilidad con Bearer y Token legacy mientras sigan vigentes las fases de migracion de seguridad.
- El esquema de persistencia final solo se soporta para instalaciones nuevas; no se migran volumenes existentes.
- Agregar pruebas unitarias y contractuales para UUID, busqueda por codigo, contexto activo y traduccion.
