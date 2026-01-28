# ================================
# CrudApp Medical - Deployment Script (PowerShell)
# ================================

param(
    [Parameter(Mandatory=$true)]
    [ValidateSet('dev','staging','prod')]
    [string]$Environment
)

# Colors
function Write-Info {
    param([string]$Message)
    Write-Host "[INFO] $Message" -ForegroundColor Green
}

function Write-Warning {
    param([string]$Message)
    Write-Host "[WARNING] $Message" -ForegroundColor Yellow
}

function Write-Error {
    param([string]$Message)
    Write-Host "[ERROR] $Message" -ForegroundColor Red
}

Write-Info "Starting deployment for environment: $Environment"

# Determine compose file
$ComposeFile = switch ($Environment) {
    'dev' { 'docker-compose.yml' }
    'staging' { 'docker-compose.yml'; $env:SPRING_PROFILES_ACTIVE = 'docker' }
    'prod' { 'docker-compose.prod.yml'; $env:SPRING_PROFILES_ACTIVE = 'prod' }
}

# Check if Docker is running
try {
    docker info | Out-Null
} catch {
    Write-Error "Docker is not running. Please start Docker Desktop."
    exit 1
}

# Load environment variables from .env
if (Test-Path .env) {
    Write-Info "Loading environment variables from .env"
    Get-Content .env | ForEach-Object {
        if ($_ -match '^([^#][^=]+)=(.*)$') {
            [Environment]::SetEnvironmentVariable($matches[1], $matches[2], 'Process')
        }
    }
} else {
    Write-Warning ".env file not found. Using default values."
}

# Pull latest images (for production)
if ($Environment -eq 'prod') {
    Write-Info "Pulling latest Docker images..."
    docker-compose -f $ComposeFile pull
}

# Stop existing containers
Write-Info "Stopping existing containers..."
docker-compose -f $ComposeFile down

# Build and start containers
Write-Info "Building and starting containers..."
if ($Environment -eq 'dev') {
    docker-compose -f $ComposeFile up --build -d
} else {
    docker-compose -f $ComposeFile up -d
}

# Wait for services to be healthy
Write-Info "Waiting for services to be healthy..."
Start-Sleep -Seconds 10

# Check health
Write-Info "Checking service health..."
docker-compose -f $ComposeFile ps

# Show logs
Write-Info "Showing recent logs..."
docker-compose -f $ComposeFile logs --tail=50

Write-Info "Deployment completed successfully!"
Write-Info "Application URL: http://localhost:8080"
Write-Info "Swagger UI: http://localhost:8080/swagger-ui.html"
if ($Environment -eq 'dev') {
    Write-Info "MailHog UI: http://localhost:8025"
}
Write-Info "Actuator Health: http://localhost:8080/actuator/health"
