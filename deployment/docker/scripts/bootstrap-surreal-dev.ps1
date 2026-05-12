$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$composeDir = Split-Path -Parent $scriptDir

Push-Location $composeDir
try {
    Get-Content ".env" | ForEach-Object {
        $line = $_.Trim()
        if ($line -eq "" -or $line.StartsWith("#") -or -not $line.Contains("=")) {
            return
        }

        $parts = $line.Split("=", 2)
        [Environment]::SetEnvironmentVariable($parts[0].Trim(), $parts[1].Trim(), "Process")
    }

    Write-Host "Starting SurrealDB..."
    docker compose up -d surrealdb

    Write-Host "Waiting for SurrealDB healthcheck..."
    $deadline = (Get-Date).AddSeconds(90)
    do {
        $status = docker inspect surrealdb-messageucolab --format "{{.State.Health.Status}}" 2>$null
        if ($status -eq "healthy") {
            break
        }

        if ((Get-Date) -gt $deadline) {
            throw "SurrealDB did not become healthy before timeout. Current status: $status"
        }

        Start-Sleep -Seconds 2
    } while ($true)

    Write-Host "Importing SurrealDB schema and catalogs..."
    docker compose run --rm surrealdb-init import `
        --endpoint http://surrealdb:8005 `
        --username $env:SURREALDBUSER `
        --password $env:SURREALDBPASSWORD `
        --namespace $env:SURREALDBNAMESPACE `
        --database $env:SURREALDBDATABASE `
        /init/surreal-init.surql

    Write-Host "Importing DEV seed data..."
    docker compose run --rm surrealdb-init import `
        --endpoint http://surrealdb:8005 `
        --username $env:SURREALDBUSER `
        --password $env:SURREALDBPASSWORD `
        --namespace $env:SURREALDBNAMESPACE `
        --database $env:SURREALDBDATABASE `
        /init/surreal-seed.dev.surql

    Write-Host "Latest generated DEV identifiers:"
    @"
SELECT * FROM bootstrap_metadata:latest_dev_seed;
"@ | docker compose exec -T surrealdb /surreal sql `
        --endpoint http://localhost:8005 `
        --username $env:SURREALDBUSER `
        --password $env:SURREALDBPASSWORD `
        --namespace $env:SURREALDBNAMESPACE `
        --database $env:SURREALDBDATABASE `
        --pretty
}
finally {
    Pop-Location
}
