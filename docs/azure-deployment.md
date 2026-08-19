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

Crea un grupo de recursos en una región donde la VM seleccionada esté disponible:

```bash
az group create --name rg-messageucolab-dev --location eastus
```

Copia `infrastructure/azure/parameters.example.json` como `infrastructure/azure/parameters.local.json` y reemplaza los valores. El archivo local está ignorado por Git.

Valida antes de crear:

```bash
az deployment group what-if \
  --resource-group rg-messageucolab-dev \
  --template-file infrastructure/azure/main.bicep \
  --parameters infrastructure/azure/parameters.local.json
```

Despliega:

```bash
az deployment group create \
  --resource-group rg-messageucolab-dev \
  --template-file infrastructure/azure/main.bicep \
  --parameters infrastructure/azure/parameters.local.json
```

Conserva los outputs `acrNameOutput`, `vmNameOutput`, `publicIpAddress` y `nsgNameOutput`.

La VM instala Docker, Docker Compose, Azure CLI y Doppler mediante cloud-init. Revisa `/var/log/cloud-init-output.log` si el bootstrap no termina correctamente.

## 5. Configurar Azure DevOps

### Azure Resource Manager Service Connection

Crea una Service Connection de tipo Azure Resource Manager usando Workload Identity Federation. Limita su alcance al grupo `rg-messageucolab-dev`.

La identidad necesita:

- Permiso para iniciar y desasignar la VM.
- Permiso para crear y eliminar la regla temporal del NSG.
- Rol `AcrPush` sobre el ACR.

Para la primera demostración se puede usar `Contributor` en el grupo de recursos y `AcrPush` en ACR. Después se puede reducir a roles específicos.

La identidad del pipeline no necesita acceso a Key Vault.

### Secure File SSH

Carga la clave privada en **Pipelines > Library > Secure files** con el nombre `messageucolab-azure` y autoriza el pipeline para utilizarla.

El pipeline descarga la clave solo durante el job, obtiene la IP pública de la VM y ejecuta `scp`/`ssh` como `azureuser`.

El NSG no mantiene SSH abierto. El pipeline crea una regla `/32` para la IP temporal del agente hospedado, ejecuta SSH y el smoke test HTTP, y elimina la regla con `condition: always()`.

### Variable Group

Crea en Library un Variable Group llamado exactamente `messageucolab-cicd`:

| Variable | Ejemplo | Secreta |
|---|---|---|
| `AZURE_SERVICE_CONNECTION` | nombre de Azure RM Service Connection | No |
| `ACR_NAME` | output `acrNameOutput` | No |
| `AZURE_RESOURCE_GROUP` | `rg-messageucolab-dev` | No |
| `AZURE_VM_NAME` | output `vmNameOutput` | No |
| `AZURE_NSG_NAME` | output `nsgNameOutput` | No |
| `SSH_SECURE_FILE` | `messageucolab-azure` | No |
| `KEY_VAULT_NAME` | nombre del Key Vault existente | No |

Autoriza el pipeline para utilizar el Variable Group, la Azure Service Connection y el Secure File.

La primera versión usa un job normal de despliegue y no necesita crear un Azure DevOps Environment. Esto evita requerir permisos adicionales; los approvals se pueden incorporar en una fase posterior.

La organización no tiene habilitado el grant de paralelismo hospedado. El pipeline usa el agente Windows self-hosted del pool `Default`; Maven y Java 17 se ejecutan dentro del contenedor oficial de Maven para no depender de herramientas instaladas en la cuenta de servicio del agente. Se puede volver a `ubuntu-latest` cuando Microsoft apruebe el grant gratuito de 1.800 minutos mensuales.

## 6. Comportamiento del pipeline

El parámetro `operation` acepta:

| Operación | Resultado |
|---|---|
| `CI` | Compila, prueba, valida y publica en ACR si la rama es `develop` |
| `DEPLOY` | Ejecuta CI, publica la imagen de la rama seleccionada, inicia la VM y despliega |
| `STOP` | Desasigna la VM sin compilar el proyecto |

Los triggers automáticos utilizan `CI`. El despliegue y la detención se ejecutan manualmente desde **Run pipeline**.

Los pull requests nunca publican imágenes ni despliegan.

## 7. Inyección de variables

El script `deployment/azure/deploy.sh` realiza:

1. Inicio de sesión con Managed Identity.
2. Lectura de los dos tokens desde Key Vault.
3. Inicio de sesión de la VM en ACR mediante `AcrPull`.
4. Ejecución de `doppler run -- docker compose ...`.
5. Inicialización idempotente de Redis y SurrealDB.
6. Healthcheck de `/actuator/health`.
7. Rollback a la etiqueta anterior si la nueva aplicación no responde.

No se genera un archivo `.env`. Las variables quedan asociadas al contenedor de Docker y pueden ser inspeccionadas por un administrador root de la VM, que es una propiedad normal de la inyección por variables de entorno.

Nunca ejecutes `docker compose config` sin `--quiet` en CI o producción, porque puede imprimir valores interpolados.

## 8. Acceso y operación

La API se publica en:

```text
http://<publicIpAddress>:8085
```

El parámetro `allowedApiSourcePrefix` debe ser una IP `/32` siempre que sea posible. Para cambiar la IP permitida, vuelve a desplegar Bicep o actualiza la regla `AllowApplicationDemo`.

Después de una demostración ejecuta el pipeline manualmente con `operation=STOP` y confirma en Azure Portal que la VM aparece como `Stopped (deallocated)`.

Para consultar logs durante una sesión de mantenimiento:

```bash
cd /opt/messageucolab
docker compose -f deployment/azure/docker-compose.yml logs --tail 100 messageucolab
```

No utilices `docker compose down -v`, porque elimina los datos persistentes.

## 9. Rotación de secretos

Al cambiar variables en Doppler, ejecuta nuevamente `operation=DEPLOY` para recrear el contenedor con los nuevos valores.

Al rotar un Service Token:

1. Crea el token nuevo.
2. Actualiza su secreto en Key Vault o Azure DevOps.
3. Ejecuta y valida un despliegue.
4. Revoca el token anterior.

## 10. Eliminación

Antes de eliminar recursos, exporta cualquier dato necesario. Después elimina el grupo de recursos de demostración:

```bash
az group delete --name rg-messageucolab-dev --yes --no-wait
```

Esta operación no elimina el Key Vault si se encuentra en otro grupo de recursos, pero sí elimina ACR, VM, disco, IP y volúmenes almacenados en la VM.
