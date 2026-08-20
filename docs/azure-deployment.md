# Despliegue de MessageUcoLab en Azure

Esta primera arquitectura está destinada a demostraciones académicas. Ejecuta la aplicación, Redis, SurrealDB y Pulsar en una VM temporal con Docker Compose. No es una arquitectura de alta disponibilidad.

## Recursos y costo

La solución crea:

- Un Azure Container Registry Standard.
- Una VM Linux `Standard_B2s`.
- Un disco Standard SSD de 64 GB.
- Una IP pública Standard.
- Una red virtual, subnet y NSG.
- Una Managed Identity asociada a la VM.
- Un horario de apagado automático a las 23:00, hora de Colombia.

El Key Vault indicado se reutiliza y no se crea ni elimina.

Azure for Students incluye ACR Standard durante 12 meses para cuentas elegibles. La VM B2s, el disco y la IP pueden consumir el crédito de estudiante. Desasignar la VM detiene el cobro del cómputo, pero no necesariamente el del disco o la IP.

No se debe cambiar la suscripción a Pay-As-You-Go. Configura alertas de presupuesto en Cost Management al 50 %, 75 % y 90 % del presupuesto elegido.

## 1. Preparar Doppler

En el proyecto Doppler `messageucolab`, crea la configuración `dev_azure`.

Usa `deployment/azure/doppler.dev_azure.example` como inventario para `dev_azure`. Los valores reales se crean directamente en Doppler.

Crea estos Service Tokens:

| Token | Configuración | Permiso |
|---|---|---|
| `azure-dev-deploy` | `dev_azure` | Solo lectura |
| `azure-dev-app` | `dev_azure` | Escritura |

La aplicación necesita el token con escritura porque actualmente crea y consulta secretos de token mediante la API de Doppler. El permiso debe limitarse exclusivamente a `dev_azure`.

## 2. Preparar Key Vault

Guarda los tokens de Azure en el Key Vault existente con estos nombres:

- `doppler-deploy-token`: valor del token `azure-dev-deploy`.
- `doppler-app-token`: valor del token `azure-dev-app`.

Es preferible introducir los valores desde Azure Portal para evitar que aparezcan en el historial de la terminal.

El Bicep asigna a la Managed Identity de la VM el rol `Key Vault Secrets User` cuando `assignKeyVaultRbacRole=true`.

Si el Key Vault utiliza access policies, establece `assignKeyVaultRbacRole=false` y concede manualmente permisos `Get` para secretos a la identidad mostrada en el output `vmPrincipalId`.

Verifica también la configuración de red del Key Vault. Managed Identity resuelve autenticación, pero no omite reglas de firewall.

## 3. Crear una clave SSH

Genera una clave exclusiva para despliegue. No guardes la clave privada en Git:

```bash
ssh-keygen -t ed25519 -f ~/.ssh/messageucolab-azure -C messageucolab-azure-devops
```

La clave pública se entrega a Bicep. La privada se sube posteriormente a Azure DevOps como Secure File y nunca se almacena en Git.

## 4. Desplegar infraestructura

La infraestructura actual reutiliza el grupo `TrabajoGrado`. Verifica que exista antes de desplegar:

```bash
az group show --name TrabajoGrado
```

Copia `infrastructure/azure/parameters.example.json` como `infrastructure/azure/parameters.local.json` y reemplaza los valores. El archivo local está ignorado por Git.

Valida antes de crear:

```bash
az deployment group what-if \
  --resource-group TrabajoGrado \
  --template-file infrastructure/azure/main.bicep \
  --parameters infrastructure/azure/parameters.local.json
```

Despliega:

```bash
az deployment group create \
  --resource-group TrabajoGrado \
  --template-file infrastructure/azure/main.bicep \
  --parameters infrastructure/azure/parameters.local.json
```

Conserva los outputs `acrNameOutput`, `vmNameOutput`, `publicIpAddress` y `nsgNameOutput`.

La VM instala Docker, Docker Compose, Azure CLI y Doppler mediante cloud-init. Revisa `/var/log/cloud-init-output.log` si el bootstrap no termina correctamente.

## 5. Configurar Azure DevOps

La organización activa es `NosGraduamosFC` y el proyecto es [`MessageUCO`](https://dev.azure.com/NosGraduamosFC/MessageUCO). La integración usa dos pipelines sobre agentes Microsoft `ubuntu-24.04`:

| Pipeline | Archivo YAML | Ejecución |
|---|---|---|
| `MessageUCO-CI` | `azure-pipelines.yml` | Automática para PR y cambios en `develop` |
| `MessageUCO-CD` | `azure-deploy-pipeline.yml` | Manual para `DEPLOY` y `STOP` |

Conecta ambos pipelines al repositorio GitHub `CamiloGarcia09/NosGraduamos-FC` mediante la Azure Pipelines GitHub App. El CI no utiliza Service Connections, Secure Files ni Variable Groups de despliegue.

### Azure Resource Manager Service Connection

Crea una Service Connection de tipo Azure Resource Manager usando Workload Identity Federation. Usa el nombre `messageucolab-azure-wif`; la identidad recibe roles solamente sobre el ACR `messageucolab1c6062b9`, la VM `messageucolab-dev-vm` y el NSG `messageucolab-dev-nsg` existentes en `TrabajoGrado`.

La identidad necesita estos roles sobre recursos concretos:

- `Virtual Machine Contributor` sobre `messageucolab-dev-vm`.
- `Network Contributor` sobre `messageucolab-dev-nsg`.
- Rol `AcrPush` sobre el ACR.

La identidad del pipeline no necesita acceso a Key Vault. Autoriza esta conexión solamente para `MessageUCO-CD`; no habilites acceso para todos los pipelines.

### Secure File SSH

Genera una clave Ed25519 exclusiva para esta organización, instala su clave pública para `azureuser` en la VM y carga la privada en **Pipelines > Library > Secure files** con el nombre `messageucolab-azure-v2`. Autoriza solamente `MessageUCO-CD`.

El pipeline descarga la clave solo durante el job, obtiene la IP pública de la VM y ejecuta `scp`/`ssh` como `azureuser`.

El NSG no mantiene SSH abierto. El pipeline crea una regla `/32` para la IP temporal del agente hospedado, ejecuta SSH y el smoke test HTTP, y elimina la regla con `condition: always()`.

### Variable Group

Crea en Library un Variable Group llamado exactamente `messageucolab-cicd`:

| Variable | Ejemplo | Secreta |
|---|---|---|
| `ACR_NAME` | output `acrNameOutput` | No |
| `AZURE_RESOURCE_GROUP` | `TrabajoGrado` | No |
| `AZURE_VM_NAME` | output `vmNameOutput` | No |
| `AZURE_NSG_NAME` | output `nsgNameOutput` | No |
| `SSH_SECURE_FILE` | `messageucolab-azure-v2` | No |
| `KEY_VAULT_NAME` | nombre del Key Vault existente | No |

El nombre `messageucolab-azure-wif` queda declarado literalmente en el YAML de CD para que Azure DevOps pueda resolver y proteger la Service Connection antes de iniciar el stage. Autoriza solamente `MessageUCO-CD` para utilizar el Variable Group, la Azure Service Connection y el Secure File. No agregues tokens Doppler, contraseñas de base de datos ni otros secretos al Variable Group.

### Environment protegido

Crea el Environment `messageucolab-dev` y autoriza solamente `MessageUCO-CD`. Configura una aprobación manual, control de rama para `refs/heads/develop` y un bloqueo exclusivo. El bloqueo evita que dos operaciones modifiquen simultáneamente las reglas NSG o los contenedores de la VM.

La organización dispone del grant gratuito de 1.800 minutos mensuales. Ambos pipelines usan agentes Microsoft `ubuntu-24.04`; no requieren el agente local Windows ni Docker Desktop.

## 6. Comportamiento de los pipelines

### MessageUCO-CI

Se ejecuta para todo PR hacia `develop` y para cada cambio integrado en `develop`. Compila, prueba, publica resultados JUnit y valida Docker Compose, Bicep y la construcción de la imagen. No inicia sesión en Azure ni publica imágenes.

El primer run crea una caché Maven Linux. Los agentes son efímeros, por lo que las capas Docker locales no persisten; `Cache@2` restaura únicamente el repositorio Maven del job de CI.

### MessageUCO-CD

No tiene triggers automáticos. Debe ejecutarse manualmente desde `develop` y su parámetro `operation` acepta:

| Operación | Resultado |
|---|---|
| `DEPLOY` | Construye y publica la imagen del commit, inicia la VM y despliega |
| `STOP` | Desasigna la VM sin compilar el proyecto |

El YAML rechaza ambas operaciones fuera de `refs/heads/develop`. El Environment solicita aprobación y serializa las ejecuciones. Los pull requests nunca reciben credenciales, publican imágenes ni despliegan.

## 7. Inyección de variables

El script `deployment/azure/deploy.sh` realiza:

1. Inicio de sesión con Managed Identity.
2. Lectura de los dos tokens desde Key Vault.
3. Inicio de sesión de la VM en ACR mediante `AcrPull`.
4. Ejecución de `doppler run -- docker compose ...`.
5. Inicialización idempotente de Redis y SurrealDB.
6. Inicio de Kong como único punto de entrada público.
7. Healthcheck de `/actuator/health` a través de Kong.
8. Rollback a la etiqueta anterior si la nueva aplicación no responde.

No se genera un archivo `.env`. Las variables quedan asociadas al contenedor de Docker y pueden ser inspeccionadas por un administrador root de la VM, que es una propiedad normal de la inyección por variables de entorno.

Nunca ejecutes `docker compose config` sin `--quiet` en CI o producción, porque puede imprimir valores interpolados.

## 8. Acceso y operación

La API se publica automáticamente a través de Kong después de un `DEPLOY` exitoso:

```text
http://<publicIpAddress>:8000
```

El pipeline crea la regla NSG `AllowDemoGateway` para permitir TCP `8000` desde Internet. No es necesario editar manualmente la red de la VM. Spring Boot permanece accesible solo dentro de la red Docker por `8085` y el puerto administrativo `8001` de Kong está deshabilitado.

Endpoints de operación:

```text
http://<publicIpAddress>:8000/actuator/health
http://<publicIpAddress>:8000/swagger-ui/index.html
http://<publicIpAddress>:8000/messageucolab/v1/application/messages
```

La API utiliza HTTP y queda disponible públicamente mientras la VM está encendida. No mantengas la demostración desplegada más tiempo del necesario.

Después de una demostración ejecuta `MessageUCO-CD` manualmente con `operation=STOP`. El pipeline elimina `AllowDemoGateway`, desasigna la VM y evita que los endpoints continúen accesibles.

Para consultar logs durante una sesión de mantenimiento:

```bash
cd /opt/messageucolab
docker compose -f deployment/azure/docker-compose.yml logs --tail 100 messageucolab kong
```

No utilices `docker compose down -v`, porque elimina los datos persistentes.

## 9. Rotación de secretos

Al cambiar variables en Doppler, ejecuta nuevamente `MessageUCO-CD` con `operation=DEPLOY` para recrear el contenedor con los nuevos valores.

Al rotar un Service Token:

1. Crea el token nuevo.
2. Actualiza su secreto en Key Vault.
3. Ejecuta y valida un despliegue.
4. Revoca el token anterior.

## 10. Eliminación

Antes de eliminar recursos, exporta cualquier dato necesario. Después elimina el grupo de recursos de demostración:

```bash
az group delete --name rg-messageucolab-dev --yes --no-wait
```

Esta operación no elimina el Key Vault si se encuentra en otro grupo de recursos, pero sí elimina ACR, VM, disco, IP y volúmenes almacenados en la VM.
