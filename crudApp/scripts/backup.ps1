# ================================
# CrudApp Medical - Database Backup Script (PowerShell)
# ================================

function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Green
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

# Configuration
$BackupDir = ".\docker\mysql\backup"
$Timestamp = Get-Date -Format "yyyyMMdd_HHmmss"
$BackupFile = "crudapp_backup_$Timestamp.sql"

# Load environment variables
if (Test-Path .env) {
    Get-Content .env | ForEach-Object {
        if ($_ -match '^([^#][^=]+)=(.*)$') {
            [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
        }
    }
}

$MysqlContainer = if ($env:MYSQL_CONTAINER) { $env:MYSQL_CONTAINER } else { "crudapp-mysql" }
$MysqlDatabase = if ($env:MYSQL_DATABASE) { $env:MYSQL_DATABASE } else { "crudapp_db" }
$MysqlUser = if ($env:MYSQL_USER) { $env:MYSQL_USER } else { "crudapp_user" }
$MysqlPassword = if ($env:MYSQL_PASSWORD) { $env:MYSQL_PASSWORD } else { "crudapp_password" }

# Create backup directory
if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir | Out-Null
}

Write-Info "Starting database backup..."

# Perform backup
$BackupPath = Join-Path $BackupDir $BackupFile
docker exec $MysqlContainer mysqldump -u $MysqlUser -p$MysqlPassword $MysqlDatabase | Out-File -FilePath $BackupPath -Encoding UTF8

if ($LASTEXITCODE -eq 0) {
    Write-Info "Backup completed successfully: $BackupFile"

    # Compress backup
    Compress-Archive -Path $BackupPath -DestinationPath "$BackupPath.zip" -Force
    Remove-Item $BackupPath
    Write-Info "Backup compressed: $BackupFile.zip"

    # Keep only last 7 backups
    Write-Info "Cleaning old backups (keeping last 7)..."
    Get-ChildItem $BackupDir -Filter "crudapp_backup_*.zip" |
        Sort-Object LastWriteTime -Descending |
        Select-Object -Skip 7 |
        Remove-Item

    Write-Info "Backup process completed!"
} else {
    Write-Error "Backup failed!"
    exit 1
}
