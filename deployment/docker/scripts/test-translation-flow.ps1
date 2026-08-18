param(
    [Parameter(Mandatory = $true)]
    [string]$Token,

    [string]$MessageCode = "MSG_TEST_001",
    [string]$SourceLanguage = "auto",
    [string]$TargetLanguage = "en",
    [switch]$ViaKong
)

$ErrorActionPreference = "Stop"

$composeDir = Resolve-Path (Join-Path $PSScriptRoot "..")
$envFile = Join-Path $composeDir ".env"
$baseUrl = if ($ViaKong) { "http://localhost:8000" } else { "http://localhost:8085" }

function Get-DockerEnvValue {
    param([string]$Name)

    $currentValue = [Environment]::GetEnvironmentVariable($Name)
    if (-not [string]::IsNullOrWhiteSpace($currentValue)) {
        return $currentValue
    }

    if (Test-Path $envFile) {
        $line = Get-Content $envFile | Where-Object { $_ -match "^\s*$Name\s*=" } | Select-Object -First 1
        if ($line) {
            return ($line -replace "^\s*$Name\s*=", "").Trim().Trim('"')
        }
    }

    throw "Missing $Name in environment or deployment/docker/.env"
}

function Invoke-SurrealSql {
    param([string]$Sql)

    $username = Get-DockerEnvValue "SURREALDBUSER"
    $password = Get-DockerEnvValue "SURREALDBPASSWORD"
    $namespace = Get-DockerEnvValue "SURREALDBNAMESPACE"
    $database = Get-DockerEnvValue "SURREALDBDATABASE"

    $Sql | docker compose exec -T surrealdb /surreal sql `
        --endpoint http://localhost:8005 `
        --username $username `
        --password $password `
        --namespace $namespace `
        --database $database
}

$escapedMessageCode = $MessageCode.Replace("'", "\'")
$selectMessageSql = "SELECT code, title, content FROM message WHERE code = '$escapedMessageCode';"
$encodedMessageCode = [System.Uri]::EscapeDataString($MessageCode)
$encodedSourceLanguage = [System.Uri]::EscapeDataString($SourceLanguage)
$encodedTargetLanguage = [System.Uri]::EscapeDataString($TargetLanguage)
$uri = "$baseUrl/messageucolab/v1/application/messages/$encodedMessageCode/translation?sourceLanguage=$encodedSourceLanguage&targetLanguage=$encodedTargetLanguage"

Push-Location $composeDir
try {
    Write-Host "1. Original message in SurrealDB before translation"
    Invoke-SurrealSql $selectMessageSql

    Write-Host "2. Calling dynamic translation endpoint"
    $script:translationResponse = $null
    $elapsed = Measure-Command {
        $script:translationResponse = Invoke-RestMethod `
            -Method GET `
            -Uri $uri `
            -Headers @{ Token = $Token; Accept = "application/json" }
    }

    $script:translationResponse | ConvertTo-Json -Depth 20
    Write-Host ("HTTP elapsedMs: {0}" -f [Math]::Round($elapsed.TotalMilliseconds, 2))

    Write-Host "3. Original message in SurrealDB after translation"
    Invoke-SurrealSql $selectMessageSql

    Write-Host "4. Translation logs"
    docker compose logs --tail 50 messageucolab | Select-String "Dynamic translation"
}
finally {
    Pop-Location
}
