# Script PowerShell pour tester l'intégration Prometheus
# Usage: .\scripts\test-prometheus.ps1

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Test de l'intégration Prometheus" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Fonction pour tester un endpoint
function Test-Endpoint {
    param (
        [string]$Url,
        [string]$Name
    )

    Write-Host "Testing $Name..." -NoNewline
    try {
        $response = Invoke-WebRequest -Uri $Url -UseBasicParsing -TimeoutSec 5
        if ($response.StatusCode -eq 200) {
            Write-Host " ✓ OK" -ForegroundColor Green
            return $true
        } else {
            Write-Host " ✗ FAILED (Status: $($response.StatusCode))" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host " ✗ FAILED ($($_.Exception.Message))" -ForegroundColor Red
        return $false
    }
}

Write-Host "1. Vérification de l'application Spring Boot..." -ForegroundColor Yellow
Write-Host ""

$appHealth = Test-Endpoint -Url "http://localhost:8080/actuator/health" -Name "Health Endpoint"
$appMetrics = Test-Endpoint -Url "http://localhost:8080/actuator/metrics" -Name "Metrics Endpoint"
$appPrometheus = Test-Endpoint -Url "http://localhost:8080/actuator/prometheus" -Name "Prometheus Endpoint"

Write-Host ""
Write-Host "2. Vérification de Prometheus..." -ForegroundColor Yellow
Write-Host ""

$prometheusUI = Test-Endpoint -Url "http://localhost:9090" -Name "Prometheus UI"
$prometheusTargets = Test-Endpoint -Url "http://localhost:9090/targets" -Name "Prometheus Targets"

Write-Host ""
Write-Host "3. Vérification de Grafana..." -ForegroundColor Yellow
Write-Host ""

$grafanaUI = Test-Endpoint -Url "http://localhost:3000" -Name "Grafana UI"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Résumé des tests" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

$totalTests = 6
$passedTests = 0
if ($appHealth) { $passedTests++ }
if ($appMetrics) { $passedTests++ }
if ($appPrometheus) { $passedTests++ }
if ($prometheusUI) { $passedTests++ }
if ($prometheusTargets) { $passedTests++ }
if ($grafanaUI) { $passedTests++ }

Write-Host "Tests réussis: $passedTests/$totalTests" -ForegroundColor $(if ($passedTests -eq $totalTests) { "Green" } else { "Yellow" })
Write-Host ""

if ($passedTests -eq $totalTests) {
    Write-Host "✓ Tous les services sont opérationnels!" -ForegroundColor Green
    Write-Host ""
    Write-Host "URLs d'accès:" -ForegroundColor Cyan
    Write-Host "  - Application:        http://localhost:8080" -ForegroundColor White
    Write-Host "  - Swagger UI:         http://localhost:8080/swagger-ui.html" -ForegroundColor White
    Write-Host "  - Actuator Health:    http://localhost:8080/actuator/health" -ForegroundColor White
    Write-Host "  - Prometheus Metrics: http://localhost:8080/actuator/prometheus" -ForegroundColor White
    Write-Host "  - Prometheus UI:      http://localhost:9090" -ForegroundColor White
    Write-Host "  - Grafana:            http://localhost:3000 (admin/admin)" -ForegroundColor White
    Write-Host "  - MailHog:            http://localhost:8025" -ForegroundColor White
} else {
    Write-Host "✗ Certains services ne sont pas disponibles." -ForegroundColor Red
    Write-Host ""
    Write-Host "Suggestions:" -ForegroundColor Yellow
    Write-Host "  1. Vérifiez que Docker Compose est démarré: docker-compose ps" -ForegroundColor White
    Write-Host "  2. Vérifiez les logs: docker-compose logs" -ForegroundColor White
    Write-Host "  3. Redémarrez les services: docker-compose restart" -ForegroundColor White
}

Write-Host ""
Write-Host "Pour voir les métriques en temps réel:" -ForegroundColor Cyan
Write-Host "  curl http://localhost:8080/actuator/prometheus" -ForegroundColor White
Write-Host ""
