# Script de test des erreurs d'inscription
# Ce script teste différents scénarios d'erreur pour l'inscription

$baseUrl = "http://localhost:8080/api"

Write-Host "=== Test des erreurs d'inscription ===" -ForegroundColor Cyan
Write-Host ""

# Test 1: Nom d'utilisateur déjà existant
Write-Host "Test 1: Inscription avec un nom d'utilisateur existant" -ForegroundColor Yellow
$body1 = @{
    username = "testuser"
    email = "duplicate@test.com"
    password = "password123"
    userType = "PATIENT"
    fullName = "Test User"
    phoneNumber = "0123456789"
    dateOfBirth = "1990-01-01"
} | ConvertTo-Json

try {
    $response1 = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $body1 -ContentType "application/json"
    Write-Host "✅ Inscription réussie (première fois)" -ForegroundColor Green

    # Essayer de s'inscrire à nouveau avec le même nom d'utilisateur
    Write-Host "Tentative de réinscription avec le même nom d'utilisateur..." -ForegroundColor Yellow
    try {
        $response2 = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $body1 -ContentType "application/json"
        Write-Host "❌ ERREUR: L'inscription a réussi alors qu'elle devrait échouer" -ForegroundColor Red
    } catch {
        $errorDetails = $_.ErrorDetails.Message | ConvertFrom-Json
        Write-Host "✅ Erreur attendue reçue:" -ForegroundColor Green
        Write-Host "   Status: $($errorDetails.status)" -ForegroundColor Cyan
        Write-Host "   Message: $($errorDetails.message)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "Note: Le premier utilisateur existe peut-être déjà" -ForegroundColor Yellow
    $errorDetails = $_.ErrorDetails.Message | ConvertFrom-Json
    Write-Host "   Message: $($errorDetails.message)" -ForegroundColor Cyan
}

Write-Host ""

# Test 2: Champs manquants pour PATIENT
Write-Host "Test 2: Inscription PATIENT sans champs requis" -ForegroundColor Yellow
$body2 = @{
    username = "patient_incomplete"
    email = "incomplete@test.com"
    password = "password123"
    userType = "PATIENT"
} | ConvertTo-Json

try {
    $response2 = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $body2 -ContentType "application/json"
    Write-Host "❌ ERREUR: L'inscription a réussi sans les champs requis" -ForegroundColor Red
} catch {
    $errorDetails = $_.ErrorDetails.Message | ConvertFrom-Json
    Write-Host "✅ Erreur attendue reçue:" -ForegroundColor Green
    Write-Host "   Status: $($errorDetails.status)" -ForegroundColor Cyan
    Write-Host "   Message: $($errorDetails.message)" -ForegroundColor Cyan
    if ($errorDetails.errors) {
        Write-Host "   Erreurs de validation:" -ForegroundColor Cyan
        $errorDetails.errors.PSObject.Properties | ForEach-Object {
            Write-Host "      - $($_.Name): $($_.Value)" -ForegroundColor Yellow
        }
    }
}

Write-Host ""

# Test 3: Champs manquants pour DOCTOR
Write-Host "Test 3: Inscription DOCTOR sans spécialité" -ForegroundColor Yellow
$body3 = @{
    username = "doctor_incomplete"
    email = "doctor_incomplete@test.com"
    password = "password123"
    userType = "DOCTOR"
} | ConvertTo-Json

try {
    $response3 = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $body3 -ContentType "application/json"
    Write-Host "❌ ERREUR: L'inscription a réussi sans spécialité" -ForegroundColor Red
} catch {
    $errorDetails = $_.ErrorDetails.Message | ConvertFrom-Json
    Write-Host "✅ Erreur attendue reçue:" -ForegroundColor Green
    Write-Host "   Status: $($errorDetails.status)" -ForegroundColor Cyan
    Write-Host "   Message: $($errorDetails.message)" -ForegroundColor Cyan
}

Write-Host ""

# Test 4: Email invalide
Write-Host "Test 4: Inscription avec email invalide" -ForegroundColor Yellow
$body4 = @{
    username = "testuser_invalidemail"
    email = "invalid-email"
    password = "password123"
    userType = "PATIENT"
    fullName = "Test User"
    phoneNumber = "0123456789"
    dateOfBirth = "1990-01-01"
} | ConvertTo-Json

try {
    $response4 = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $body4 -ContentType "application/json"
    Write-Host "❌ ERREUR: L'inscription a réussi avec un email invalide" -ForegroundColor Red
} catch {
    $errorDetails = $_.ErrorDetails.Message | ConvertFrom-Json
    Write-Host "✅ Erreur attendue reçue:" -ForegroundColor Green
    Write-Host "   Status: $($errorDetails.status)" -ForegroundColor Cyan
    Write-Host "   Message: $($errorDetails.message)" -ForegroundColor Cyan
    if ($errorDetails.errors) {
        Write-Host "   Erreurs de validation:" -ForegroundColor Cyan
        $errorDetails.errors.PSObject.Properties | ForEach-Object {
            Write-Host "      - $($_.Name): $($_.Value)" -ForegroundColor Yellow
        }
    }
}

Write-Host ""
Write-Host "=== Tests terminés ===" -ForegroundColor Cyan
