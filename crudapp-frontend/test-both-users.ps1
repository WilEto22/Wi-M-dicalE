# Script de test pour les 2 types d'utilisateurs
$baseUrl = "http://localhost:8080/api"

Write-Host "=== Test d'inscription PATIENT ===" -ForegroundColor Cyan

# Générer un timestamp unique pour éviter les doublons
$timestamp = Get-Date -Format "yyyyMMddHHmmss"

# Test PATIENT
$patientData = @{
    username = "patient_$timestamp"
    email = "patient_$timestamp@test.com"
    password = "password123"
    userType = "PATIENT"
    fullName = "Patient Test"
    phoneNumber = "06$timestamp"
    dateOfBirth = "1990-01-01"
} | ConvertTo-Json

Write-Host "Données envoyées pour PATIENT:" -ForegroundColor Yellow
$patientData

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $patientData -ContentType "application/json"
    Write-Host "✅ PATIENT inscrit avec succès!" -ForegroundColor Green
    Write-Host "Token: $($response.accessToken.Substring(0, 50))..." -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur PATIENT:" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    $errorDetails = $_.ErrorDetails.Message | ConvertFrom-Json
    Write-Host "Message: $($errorDetails.message)" -ForegroundColor Red
    if ($errorDetails.errors) {
        Write-Host "Détails des erreurs:" -ForegroundColor Red
        $errorDetails.errors | ConvertTo-Json
    }
}

Write-Host ""
Write-Host "=== Test d'inscription DOCTOR ===" -ForegroundColor Cyan

# Test DOCTOR
$doctorData = @{
    username = "doctor_$timestamp"
    email = "doctor_$timestamp@test.com"
    password = "password123"
    userType = "DOCTOR"
    medicalSpecialty = "CARDIOLOGY"
} | ConvertTo-Json

Write-Host "Données envoyées pour DOCTOR:" -ForegroundColor Yellow
$doctorData

try {
    $response = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $doctorData -ContentType "application/json"
    Write-Host "✅ DOCTOR inscrit avec succès!" -ForegroundColor Green
    Write-Host "Token: $($response.accessToken.Substring(0, 50))..." -ForegroundColor Green
} catch {
    Write-Host "❌ Erreur DOCTOR:" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    $errorDetails = $_.ErrorDetails.Message | ConvertFrom-Json
    Write-Host "Message: $($errorDetails.message)" -ForegroundColor Red
    if ($errorDetails.errors) {
        Write-Host "Détails des erreurs:" -ForegroundColor Red
        $errorDetails.errors | ConvertTo-Json
    }
}

Write-Host ""
Write-Host "=== Tests terminés ===" -ForegroundColor Cyan
