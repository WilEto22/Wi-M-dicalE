# 🚀 Guide de Démarrage Rapide - CrudApp Medical

Ce guide vous permet de démarrer rapidement avec l'API CrudApp Medical.

---

## ⚡ Démarrage en 5 Minutes

### 1. Prérequis

- ✅ Java 17 installé
- ✅ MySQL 8.0 en cours d'exécution
- ✅ Maven installé

### 2. Configuration de la Base de Données

```sql
CREATE DATABASE crudapp_db;
```

### 3. Démarrer l'Application

```bash
cd D:/crudApp/crudApp
mvn spring-boot:run
```

### 4. Accéder à Swagger UI

Ouvrez votre navigateur : **http://localhost:8080/swagger-ui.html**

---

## 🎯 Premier Test avec Swagger UI

### Étape 1 : Créer un Compte

1. Dans Swagger UI, trouvez la section **🔐 Authentification**
2. Cliquez sur **POST /api/auth/register**
3. Cliquez sur **Try it out**
4. Entrez :
   ```json
   {
     "username": "docteur_test",
     "password": "Test123!"
   }
   ```
5. Cliquez sur **Execute**
6. **Copiez** le `accessToken` de la réponse

### Étape 2 : S'Authentifier dans Swagger

1. Cliquez sur le bouton **Authorize** 🔒 en haut de la page
2. Collez votre `accessToken` (sans "Bearer")
3. Cliquez sur **Authorize**
4. Fermez la fenêtre

### Étape 3 : Créer un Patient

1. Trouvez la section **🏥 Patients**
2. Cliquez sur **POST /api/patients**
3. Cliquez sur **Try it out**
4. Utilisez cet exemple :
   ```json
   {
     "name": "Jean Dupont",
     "email": "jean.dupont@example.com",
     "age": 45,
     "address": "123 Rue de la Santé, Paris",
     "bloodType": "A+",
     "allergies": "Pénicilline, Pollen",
     "medicalHistory": "Diabète de type 2",
     "phoneNumber": "+33612345678",
     "emergencyContact": "Marie Dupont",
     "emergencyPhone": "+33687654321",
     "insuranceNumber": "1234567890123",
     "lastVisit": "2024-12-15"
   }
   ```
5. Cliquez sur **Execute**

### Étape 4 : Rechercher des Patients

1. Cliquez sur **GET /api/patients/search**
2. Essayez différents filtres :
   - `bloodType`: A+
   - `allergy`: Pénicilline
   - `minAge`: 40
   - `maxAge`: 60

### Étape 5 : Exporter en PDF

1. Cliquez sur **GET /api/patients/export/pdf**
2. Cliquez sur **Execute**
3. Cliquez sur **Download file**

---

## 📝 Exemples avec cURL

### Inscription

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"docteur_test","password":"Test123!"}'
```

### Connexion

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"docteur_test","password":"Test123!"}'
```

### Créer un Patient

```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Authorization: Bearer VOTRE_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jean Dupont",
    "email": "jean.dupont@example.com",
    "age": 45,
    "address": "123 Rue de la Santé, Paris",
    "bloodType": "A+",
    "allergies": "Pénicilline",
    "phoneNumber": "+33612345678",
    "emergencyContact": "Marie Dupont",
    "emergencyPhone": "+33687654321",
    "insuranceNumber": "1234567890123"
  }'
```

### Lister les Patients

```bash
curl -X GET "http://localhost:8080/api/patients?page=0&size=10" \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

### Rechercher par Groupe Sanguin

```bash
curl -X GET "http://localhost:8080/api/patients/search?bloodType=A%2B" \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

---

## 🔑 Endpoints Essentiels

| Endpoint | Méthode | Description |
|----------|---------|-------------|
| `/api/auth/register` | POST | Créer un compte |
| `/api/auth/login` | POST | Se connecter |
| `/api/patients` | POST | Créer un patient |
| `/api/patients` | GET | Liste des patients |
| `/api/patients/{id}` | GET | Détails d'un patient |
| `/api/patients/search` | GET | Recherche avancée |
| `/api/patients/export/pdf` | GET | Export PDF |

---

## 🎨 Groupes Sanguins Valides

- `A+`, `A-`
- `B+`, `B-`
- `AB+`, `AB-`
- `O+`, `O-`

---

## 🔍 Critères de Recherche Disponibles

- **name** : Nom du patient (recherche partielle)
- **email** : Email (recherche partielle)
- **minAge** / **maxAge** : Plage d'âge
- **bloodType** : Groupe sanguin exact
- **allergy** : Recherche dans les allergies
- **lastVisitAfter** / **lastVisitBefore** : Dates de visite (format: YYYY-MM-DD)
- **isActive** : true (actif) ou false (archivé)
- **insuranceNumber** : Numéro d'assurance
- **phoneNumber** : Téléphone
- **emergencyContact** : Contact d'urgence

---

## 🛠️ Dépannage

### L'application ne démarre pas

```bash
# Vérifier que MySQL est démarré
mysql -u root -p

# Vérifier la configuration
cat src/main/resources/application.properties
```

### Erreur 401 Unauthorized

- Vérifiez que vous avez cliqué sur **Authorize** dans Swagger UI
- Vérifiez que votre token n'a pas expiré (5 heures)
- Utilisez `/api/auth/refresh` pour obtenir un nouveau token

### Erreur 403 Forbidden

- Vérifiez que vous avez le bon rôle (USER ou ADMIN)
- Les endpoints `/api/admin/**` nécessitent le rôle ADMIN

---

## 📚 Documentation Complète

- **Swagger UI** : http://localhost:8080/swagger-ui.html
- **OpenAPI JSON** : http://localhost:8080/v3/api-docs
- **README** : Voir README.md pour la documentation complète

---

## 🎓 Cas d'Usage Typiques

### Scénario 1 : Enregistrer un Nouveau Patient

1. Connexion → Obtenir le token
2. POST `/api/patients` → Créer le patient
3. GET `/api/patients/{id}` → Vérifier les informations

### Scénario 2 : Suivi Médical

1. GET `/api/patients/follow-up?daysAgo=30` → Patients sans visite depuis 30 jours
2. PUT `/api/patients/{id}` → Mettre à jour la date de visite

### Scénario 3 : Recherche par Allergie

1. GET `/api/patients/search?allergy=Pénicilline` → Trouver tous les patients allergiques
2. GET `/api/patients/export/pdf` → Exporter la liste

### Scénario 4 : Archivage

1. PUT `/api/patients/{id}/archive` → Archiver un patient inactif
2. GET `/api/patients/search?isActive=false` → Voir les patients archivés
3. PUT `/api/patients/{id}/reactivate` → Réactiver si nécessaire

---

## 💡 Conseils

- ✅ Utilisez Swagger UI pour tester rapidement
- ✅ Sauvegardez vos tokens pour éviter de vous reconnecter
- ✅ Utilisez la recherche avancée pour filtrer efficacement
- ✅ Exportez régulièrement vos données en PDF/Excel
- ✅ Archivez les patients inactifs au lieu de les supprimer

---

## 🚀 Prochaines Étapes

1. Explorez tous les endpoints dans Swagger UI
2. Testez les différents formats d'export (CSV, Excel, PDF)
3. Créez plusieurs patients pour tester la pagination
4. Essayez les recherches combinées
5. Testez le rafraîchissement des tokens

---

**Besoin d'aide ?** Consultez le README.md complet ou la documentation Swagger UI !
