# Script de test d'inscription

# Test inscription PATIENT
$patientBody = @{
    username = "patient_test"
    email = "patient@test.com"
    password = "Password123!"
    userType = "PATIENT"
    fullName = "Jean Dupont"
    phoneNumber = "+33612345678"
    dateOfBirth = "1990-05-15"
} | ConvertTo-Json

Write-Host "=== Test inscription PATIENT ===" -ForegroundColor Cyan
Write-Host "Body: $patientBody" -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $patientBody -ContentType "application/json"
    Write-Host "✅ Succès:" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Erreur:" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)"
    Write-Host "Message: $($_.Exception.Message)"
    if ($_.ErrorDetails.Message) {
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Yellow
    }
}

Write-Host "`n"

# Test inscription DOCTOR
$doctorBody = @{
    username = "doctor_test"
    email = "doctor@test.com"
    password = "Password123!"
    userType = "DOCTOR"
    specialty = "CARDIOLOGIE"
} | ConvertTo-Json

Write-Host "=== Test inscription DOCTOR ===" -ForegroundColor Cyan
Write-Host "Body: $doctorBody" -ForegroundColor Yellow

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/register" -Method POST -Body $doctorBody -ContentType "application/json"
    Write-Host "✅ Succès:" -ForegroundColor Green
    $response | ConvertTo-Json
} catch {
    Write-Host "❌ Erreur:" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode.value__)"
    Write-Host "Message: $($_.Exception.Message)"
    if ($_.ErrorDetails.Message) {
        Write-Host "Details: $($_.ErrorDetails.Message)" -ForegroundColor Yellow
    }
}
