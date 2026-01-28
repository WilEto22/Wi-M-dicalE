# 🚀 Démarrage Rapide avec Docker

## ⚡ En 3 Minutes

### Prérequis
- ✅ Docker Desktop installé et démarré
- ✅ Git installé

### Étape 1: Cloner et Configurer

```powershell
# Cloner le projet
git clone https://github.com/votre-username/crudApp.git
cd crudApp

# Copier le fichier d'environnement
Copy-Item .env.example .env
```

### Étape 2: Démarrer l'Application

```powershell
# Option A: Avec le script (Recommandé)
.\scripts\deploy.ps1 -Environment dev

# Option B: Avec Docker Compose directement
docker-compose up -d
```

### Étape 3: Accéder à l'Application

Attendez 30-40 secondes que l'application démarre, puis ouvrez :

- 🌐 **Application:** http://localhost:8080
- 📚 **Swagger UI:** http://localhost:8080/swagger-ui.html
- 📧 **MailHog (Emails):** http://localhost:8025
- ❤️ **Health Check:** http://localhost:8080/actuator/health

---

## 🎯 Tester l'Application

### 1. Créer un Compte Médecin

**Via Swagger UI:**
1. Ouvrir http://localhost:8080/swagger-ui.html
2. Aller à **auth-controller** → **POST /api/auth/register**
3. Cliquer sur "Try it out"
4. Utiliser ce JSON:

```json
{
  "username": "dr.martin@hospital.com",
  "password": "Password123!",
  "userType": "DOCTOR",
  "specialty": "CARDIOLOGIE"
}
```

5. Cliquer sur "Execute"
6. **Copier le `accessToken`** de la réponse

### 2. Créer un Compte Patient

```json
{
  "username": "patient@email.com",
  "password": "Password123!",
  "userType": "PATIENT",
  "fullName": "Jean Dupont",
  "phoneNumber": "0612345678",
  "dateOfBirth": "1990-01-15"
}
```

### 3. Définir les Disponibilités du Médecin

1. Aller à **doctor-controller** → **POST /api/doctors/availability**
2. Cliquer sur le cadenas 🔒 et coller le token du médecin
3. Utiliser ce JSON:

```json
{
  "dayOfWeek": "MONDAY",
  "startTime": "09:00",
  "endTime": "17:00",
  "slotDuration": 30,
  "isActive": true
}
```

### 4. Prendre un Rendez-vous (Patient)

1. Se connecter avec le compte patient
2. Aller à **appointment-controller** → **POST /api/appointments**
3. Utiliser le token du patient
4. Créer un rendez-vous:

```json
{
  "doctorId": 1,
  "appointmentDateTime": "2024-12-30T10:00:00",
  "reason": "Consultation cardiologie"
}
```

### 5. Vérifier les Emails

Ouvrir http://localhost:8025 pour voir les emails envoyés !

---

## 📊 Commandes Utiles

### Voir les Logs

```powershell
# Tous les services
docker-compose logs -f

# Application uniquement
docker-compose logs -f app

# MySQL
docker-compose logs -f mysql
```

### Vérifier le Statut

```powershell
# Statut des containers
docker-compose ps

# Health check
curl http://localhost:8080/actuator/health
```

### Arrêter l'Application

```powershell
# Arrêter sans supprimer les données
docker-compose stop

# Arrêter et supprimer les containers
docker-compose down

# Arrêter et supprimer TOUT (données incluses)
docker-compose down -v
```

### Redémarrer

```powershell
# Redémarrer tous les services
docker-compose restart

# Redémarrer l'application uniquement
docker-compose restart app
```

---

## 🔧 Troubleshooting

### L'application ne démarre pas

```powershell
# Vérifier les logs
docker-compose logs app

# Vérifier que MySQL est prêt
docker-compose logs mysql

# Redémarrer
docker-compose restart app
```

### Port déjà utilisé

```powershell
# Trouver le processus sur le port 8080
netstat -ano | findstr :8080

# Tuer le processus
taskkill /PID <PID> /F

# Ou changer le port dans docker-compose.yml
# ports:
#   - "8081:8080"
```

### Erreur de connexion à la base de données

```powershell
# Vérifier que MySQL est démarré
docker-compose ps mysql

# Recréer les containers
docker-compose down
docker-compose up -d
```

### Nettoyer Docker

```powershell
# Supprimer les containers arrêtés
docker container prune

# Supprimer les images non utilisées
docker image prune

# Nettoyer tout
docker system prune -a
```

---

## 🎓 Aller Plus Loin

### Développement

Pour développer avec hot-reload:

```powershell
# Arrêter le container app
docker-compose stop app

# Lancer avec Maven
mvn spring-boot:run
```

### Production

Pour déployer en production:

```powershell
# Utiliser le fichier de production
docker-compose -f docker-compose.prod.yml up -d

# Ou avec le script
.\scripts\deploy.ps1 -Environment prod
```

### Backup

```powershell
# Créer un backup
.\scripts\backup.ps1

# Les backups sont dans: docker/mysql/backup/
```

---

## 📚 Documentation Complète

- 📘 [Guide DevOps](DEVOPS_GUIDE.md) - Guide complet
- 📗 [README](README.md) - Documentation principale
- 📙 [Règles Métier](BUSINESS_RULES.md) - Logique métier
- 📕 [API Documentation](APPOINTMENT_SYSTEM.md) - Endpoints

---

## 🆘 Besoin d'Aide ?

- 📖 Consultez le [Guide DevOps](DEVOPS_GUIDE.md)
- 🐛 Ouvrez une [Issue GitHub](https://github.com/votre-username/crudApp/issues)
- 💬 Contactez le support

---

**🎉 Bon développement !**
