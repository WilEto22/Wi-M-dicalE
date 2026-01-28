# 🐳 Guide DevOps - CrudApp Medical

## 📋 Table des Matières

1. [Vue d'ensemble](#vue-densemble)
2. [Prérequis](#prérequis)
3. [Configuration Locale](#configuration-locale)
4. [Déploiement Docker](#déploiement-docker)
5. [CI/CD avec GitHub Actions](#cicd-avec-github-actions)
6. [Monitoring et Logs](#monitoring-et-logs)
7. [Backup et Restore](#backup-et-restore)
8. [Déploiement en Production](#déploiement-en-production)
9. [Troubleshooting](#troubleshooting)

---

## 🎯 Vue d'ensemble

Cette application utilise une architecture containerisée avec Docker et un pipeline CI/CD automatisé via GitHub Actions.

### Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Docker Network                        │
│                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐         │
│  │  MySQL   │◄───│   App    │───►│ MailHog  │         │
│  │  :3306   │    │  :8080   │    │  :8025   │         │
│  └──────────┘    └──────────┘    └──────────┘         │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

### Environnements

- **dev** - Développement local avec H2/MySQL + MailHog
- **docker** - Environnement Docker avec MySQL + MailHog
- **prod** - Production avec MySQL + SMTP réel

---

## 🔧 Prérequis

### Logiciels Requis

- ✅ **Docker Desktop** (version 20.10+)
- ✅ **Docker Compose** (version 2.0+)
- ✅ **Java 17** (pour développement local)
- ✅ **Maven 3.9+** (pour développement local)
- ✅ **Git** (pour CI/CD)

### Vérification

```powershell
# Vérifier Docker
docker --version
docker-compose --version

# Vérifier Java
java -version

# Vérifier Maven
mvn -version
```

---

## ⚙️ Configuration Locale

### 1. Cloner le Projet

```bash
git clone https://github.com/votre-username/crudApp.git
cd crudApp
```

### 2. Configurer les Variables d'Environnement

```powershell
# Copier le fichier d'exemple
Copy-Item .env.example .env

# Éditer .env avec vos valeurs
notepad .env
```

**Variables importantes :**

```properties
# JWT Secret (générer avec: openssl rand -base64 32)
JWT_SECRET=your-secret-key-minimum-256-bits

# Database
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=crudapp_db
MYSQL_USER=crudapp_user
MYSQL_PASSWORD=crudapp_password

# Email (pour production)
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password
```

### 3. Démarrage Rapide (Développement)

```powershell
# Option 1: Avec Maven (H2 en mémoire)
mvn spring-boot:run

# Option 2: Avec Docker
.\scripts\deploy.ps1 -Environment dev
```

---

## 🐳 Déploiement Docker

### Structure des Fichiers

```
crudApp/
├── Dockerfile                    # Image multi-stage optimisée
├── docker-compose.yml            # Dev/Staging
├── docker-compose.prod.yml       # Production
├── .dockerignore                 # Fichiers exclus
├── .env.example                  # Template variables
└── docker/
    └── mysql/
        └── init.sql              # Script d'initialisation
```

### Commandes Docker

#### Développement

```powershell
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f app

# Arrêter les services
docker-compose down

# Reconstruire l'image
docker-compose up --build -d
```

#### Production

```powershell
# Démarrer en production
docker-compose -f docker-compose.prod.yml up -d

# Vérifier le statut
docker-compose -f docker-compose.prod.yml ps

# Voir les logs
docker-compose -f docker-compose.prod.yml logs -f
```

### Scripts de Déploiement

#### Windows (PowerShell)

```powershell
# Déployer en dev
.\scripts\deploy.ps1 -Environment dev

# Déployer en staging
.\scripts\deploy.ps1 -Environment staging

# Déployer en production
.\scripts\deploy.ps1 -Environment prod
```

#### Linux/Mac (Bash)

```bash
# Rendre les scripts exécutables
chmod +x scripts/*.sh

# Déployer
./scripts/deploy.sh dev
./scripts/deploy.sh staging
./scripts/deploy.sh prod
```

---

## 🔄 CI/CD avec GitHub Actions

### Workflow Automatisé

Le pipeline CI/CD s'exécute automatiquement sur :
- **Push** sur `main` ou `develop`
- **Pull Request** vers `main` ou `develop`

### Étapes du Pipeline

```
1. Build & Test
   ├── Checkout code
   ├── Setup JDK 17
   ├── Cache Maven dependencies
   ├── Build with Maven
   ├── Run tests (102 tests)
   └── Upload artifacts

2. Code Quality
   ├── SonarCloud analysis
   └── Security scan

3. Docker Build
   ├── Build Docker image
   ├── Tag image
   └── Push to Docker Hub

4. Deploy
   ├── Staging (develop branch)
   └── Production (main branch)
```

### Configuration GitHub Secrets

Allez dans **Settings → Secrets and variables → Actions** et ajoutez :

```
# Docker Hub
DOCKER_USERNAME=your-dockerhub-username
DOCKER_PASSWORD=your-dockerhub-token

# JWT
JWT_SECRET=your-secret-key-minimum-256-bits

# Database (Production)
DATABASE_URL=jdbc:mysql://host:3306/db
DATABASE_USERNAME=user
DATABASE_PASSWORD=password

# Email
EMAIL_HOST=smtp.gmail.com
EMAIL_PORT=587
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# Deployment (SSH)
STAGING_HOST=staging.example.com
STAGING_USERNAME=deploy
STAGING_SSH_KEY=-----BEGIN RSA PRIVATE KEY-----...

PRODUCTION_HOST=prod.example.com
PRODUCTION_USERNAME=deploy
PRODUCTION_SSH_KEY=-----BEGIN RSA PRIVATE KEY-----...

# SonarCloud (optionnel)
SONAR_TOKEN=your-sonarcloud-token
```

### Déclencher Manuellement

```bash
# Via GitHub UI
Actions → CI/CD Pipeline → Run workflow

# Via Git
git tag v2.0.0
git push origin v2.0.0
```

---

## 📊 Monitoring et Logs

### Spring Boot Actuator

L'application expose plusieurs endpoints de monitoring :

```
# Health Check
http://localhost:8080/actuator/health

# Informations
http://localhost:8080/actuator/info

# Métriques
http://localhost:8080/actuator/metrics

# Prometheus
http://localhost:8080/actuator/prometheus
```

### Endpoints Actuator Disponibles

| Endpoint | Description |
|----------|-------------|
| `/actuator/health` | État de santé de l'application |
| `/actuator/info` | Informations sur l'application |
| `/actuator/metrics` | Métriques de performance |
| `/actuator/prometheus` | Métriques au format Prometheus |

### Consulter les Logs

#### Docker Compose

```powershell
# Tous les services
docker-compose logs -f

# Service spécifique
docker-compose logs -f app
docker-compose logs -f mysql

# Dernières 100 lignes
docker-compose logs --tail=100 app

# Avec script
.\scripts\logs.sh app 100
```

#### Logs de l'Application

```powershell
# Logs dans le container
docker exec -it crudapp-backend cat /app/logs/crudapp.log

# Logs en temps réel
docker exec -it crudapp-backend tail -f /app/logs/crudapp.log
```

### Monitoring avec Prometheus + Grafana (Optionnel)

Ajoutez à `docker-compose.yml` :

```yaml
prometheus:
  image: prom/prometheus
  ports:
    - "9090:9090"
  volumes:
    - ./docker/prometheus/prometheus.yml:/etc/prometheus/prometheus.yml

grafana:
  image: grafana/grafana
  ports:
    - "3000:3000"
  environment:
    - GF_SECURITY_ADMIN_PASSWORD=admin
```

---

## 💾 Backup et Restore

### Backup Automatique

#### Windows

```powershell
# Backup manuel
.\scripts\backup.ps1

# Planifier avec Task Scheduler
# Créer une tâche quotidienne à 2h du matin
```

#### Linux

```bash
# Backup manuel
./scripts/backup.sh

# Planifier avec cron
crontab -e
# Ajouter: 0 2 * * * /path/to/scripts/backup.sh
```

### Restore

```powershell
# Lister les backups
Get-ChildItem .\docker\mysql\backup\

# Restaurer
.\scripts\restore.sh .\docker\mysql\backup\crudapp_backup_20241225_020000.sql.gz
```

### Backup Manuel

```powershell
# Exporter la base de données
docker exec crudapp-mysql mysqldump -u crudapp_user -pcrudapp_password crudapp_db > backup.sql

# Importer
docker exec -i crudapp-mysql mysql -u crudapp_user -pcrudapp_password crudapp_db < backup.sql
```

---

## 🚀 Déploiement en Production

### Checklist Pré-Déploiement

- [ ] Tests passent (102/102)
- [ ] Variables d'environnement configurées
- [ ] Secrets GitHub configurés
- [ ] Base de données de production prête
- [ ] Certificats SSL configurés
- [ ] DNS configuré
- [ ] Backup de la base de données actuelle

### Déploiement sur VPS/Cloud

#### 1. Préparer le Serveur

```bash
# Installer Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh

# Installer Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Créer utilisateur deploy
sudo useradd -m -s /bin/bash deploy
sudo usermod -aG docker deploy
```

#### 2. Déployer l'Application

```bash
# Se connecter au serveur
ssh deploy@your-server.com

# Cloner le projet
git clone https://github.com/votre-username/crudApp.git
cd crudApp

# Configurer .env
cp .env.example .env
nano .env

# Déployer
docker-compose -f docker-compose.prod.yml up -d
```

#### 3. Configurer Nginx (Reverse Proxy)

```nginx
server {
    listen 80;
    server_name crudapp-medical.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

#### 4. Configurer SSL avec Let's Encrypt

```bash
# Installer Certbot
sudo apt install certbot python3-certbot-nginx

# Obtenir certificat
sudo certbot --nginx -d crudapp-medical.com
```

### Déploiement sur Heroku

```bash
# Installer Heroku CLI
# https://devcenter.heroku.com/articles/heroku-cli

# Login
heroku login

# Créer app
heroku create crudapp-medical

# Ajouter MySQL
heroku addons:create jawsdb:kitefin

# Configurer variables
heroku config:set JWT_SECRET=your-secret
heroku config:set SPRING_PROFILES_ACTIVE=prod

# Déployer
git push heroku main
```

---

## 🔍 Troubleshooting

### Problèmes Courants

#### 1. Docker ne démarre pas

```powershell
# Vérifier Docker Desktop
Get-Process Docker*

# Redémarrer Docker Desktop
Restart-Service docker
```

#### 2. Port déjà utilisé

```powershell
# Trouver le processus utilisant le port 8080
netstat -ano | findstr :8080

# Tuer le processus
taskkill /PID <PID> /F
```

#### 3. Base de données non accessible

```powershell
# Vérifier que MySQL est démarré
docker-compose ps mysql

# Voir les logs MySQL
docker-compose logs mysql

# Se connecter à MySQL
docker exec -it crudapp-mysql mysql -u crudapp_user -p
```

#### 4. Application ne démarre pas

```powershell
# Vérifier les logs
docker-compose logs app

# Vérifier la santé
docker-compose ps

# Redémarrer
docker-compose restart app
```

#### 5. Tests échouent

```powershell
# Nettoyer et reconstruire
mvn clean install

# Exécuter tests spécifiques
mvn test -Dtest=BusinessDayCalculatorTest
```

### Commandes Utiles

```powershell
# Nettoyer Docker
docker system prune -a

# Voir l'utilisation des ressources
docker stats

# Inspecter un container
docker inspect crudapp-backend

# Accéder au shell du container
docker exec -it crudapp-backend sh

# Voir les variables d'environnement
docker exec crudapp-backend env
```

---

## 📚 Ressources

### Documentation

- [Docker Documentation](https://docs.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)
- [GitHub Actions](https://docs.github.com/en/actions)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)

### Outils

- [Docker Hub](https://hub.docker.com/)
- [MailHog](https://github.com/mailhog/MailHog)
- [Prometheus](https://prometheus.io/)
- [Grafana](https://grafana.com/)

---

## 🎯 Bonnes Pratiques

### Sécurité

- ✅ Ne jamais commiter `.env` dans Git
- ✅ Utiliser des secrets GitHub pour les credentials
- ✅ Changer les mots de passe par défaut
- ✅ Utiliser HTTPS en production
- ✅ Limiter les ressources Docker
- ✅ Scanner les images avec Trivy

### Performance

- ✅ Utiliser le cache Docker
- ✅ Multi-stage builds
- ✅ Optimiser les images (Alpine)
- ✅ Configurer les health checks
- ✅ Limiter les logs en production

### Maintenance

- ✅ Backups quotidiens automatiques
- ✅ Monitoring actif
- ✅ Rotation des logs
- ✅ Mises à jour régulières
- ✅ Documentation à jour

---

**🎉 Votre application est maintenant prête pour le DevOps professionnel !**
