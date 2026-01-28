# Docker Diagnostics for CrudApp - PowerShell Script

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Docker Diagnostics for CrudApp" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "1. Checking running containers..." -ForegroundColor Yellow
docker ps -a
Write-Host ""

Write-Host "2. Checking Spring Boot container logs..." -ForegroundColor Yellow
docker logs crudapp-backend --tail 100
Write-Host ""

Write-Host "3. Checking MySQL container logs..." -ForegroundColor Yellow
docker logs crudapp-mysql --tail 50
Write-Host ""

Write-Host "4. Checking container health..." -ForegroundColor Yellow
$healthStatus = docker inspect --format='{{.State.Health.Status}}' crudapp-backend 2>$null
if ($healthStatus) {
    Write-Host "Health Status: $healthStatus" -ForegroundColor $(if ($healthStatus -eq "healthy") { "Green" } else { "Red" })
} else {
    Write-Host "Health Status: Unable to determine" -ForegroundColor Red
}
Write-Host ""

Write-Host "5. Checking if Spring Boot is responding..." -ForegroundColor Yellow
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5
    Write-Host "Spring Boot is responding! Status: $($response.StatusCode)" -ForegroundColor Green
} catch {
    Write-Host "Spring Boot not responding: $($_.Exception.Message)" -ForegroundColor Red
}
Write-Host ""

Write-Host "6. Checking MySQL connection..." -ForegroundColor Yellow
$mysqlCheck = docker exec crudapp-mysql mysql -u crudapp_user -pcrudapp_password -e "SELECT 1" crudapp_db 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "MySQL connection successful!" -ForegroundColor Green
} else {
    Write-Host "MySQL connection failed:" -ForegroundColor Red
    Write-Host $mysqlCheck
}
Write-Host ""

Write-Host "==========================================" -ForegroundColor Cyan
Write-Host "Diagnostics complete" -ForegroundColor Cyan
Write-Host "==========================================" -ForegroundColor Cyan
