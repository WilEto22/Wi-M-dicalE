# 🚀 Guide Complet - CrudApp Medical Full Stack

## 📋 Vue d'ensemble

Application complète de gestion médicale avec :
- **Backend** : Spring Boot 3.4.12 + MySQL + JWT
- **Frontend** : React 18 + Vite + Redux Toolkit + Material-UI
- **Monitoring** : Prometheus + Grafana
- **Email** : MailHog (développement)
- **Déploiement** : Docker + Docker Compose

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                     Docker Compose                          │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │   Frontend   │  │   Backend    │  │    MySQL     │    │
│  │  React:3001  │→ │ Spring:8080  │→ │   :3307      │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
│         ↓                  ↓                               │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐    │
│  │  Prometheus  │  │   Grafana    │  │   MailHog    │    │
│  │    :9090     │  │    :3000     │  │  :8025       │    │
│  └──────────────┘  └──────────────┘  └──────────────┘    │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Démarrage rapide

### Prérequis

- **Docker** 20.10+
- **Docker Compose** 2.0+
- **Node.js** 18+ (pour développement local)
- **Java** 17+ (pour développement local)
- **Maven** 3.8+ (pour développement local)

### 1. Cloner le projet

```bash
git clone <votre-repo>
cd crudApp
```

### 2. Démarrer avec Docker Compose

```bash
# Depuis le répertoire crudApp/
cd crudApp
docker-compose up -d
```

### 3. Vérifier que tous les services sont démarrés

```bash
docker-compose ps
```

Vous devriez voir :
- ✅ crudapp-mysql (healthy)
- ✅ crudapp-backend (healthy)
- ✅ crudapp-frontend (up)
- ✅ crudapp-prometheus (up)
- ✅ crudapp-grafana (up)
- ✅ crudapp-mailhog (up)

---

## 🌐 URLs d'accès

| Service | URL | Credentials | Description |
|---------|-----|-------------|-------------|
| **Frontend** | http://localhost:3001 | - | Application React |
| **Backend API** | http://localhost:8080/api | - | API REST Spring Boot |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - | Documentation API |
| **Actuator** | http://localhost:8080/actuator | - | Métriques Spring Boot |
| **Prometheus** | http://localhost:9090 | - | Métriques système |
| **Grafana** | http://localhost:3000 | admin/admin | Dashboards |
| **MailHog** | http://localhost:8025 | - | Emails de test |
| **MySQL** | localhost:3307 | crudapp_user/crudapp_password | Base de données |

---

## 👤 Premiers pas

### 1. Créer un compte

1. Accédez à http://localhost:3001
2. Cliquez sur **S'inscrire**
3. Remplissez le formulaire :
   - Nom d'utilisateur
   - Email
   - Nom complet
   - Téléphone
   - Type : Patient ou Médecin
   - Spécialité (si médecin)
   - Mot de passe
4. Cliquez sur **S'inscrire**

### 2. Se connecter

1. Utilisez vos identifiants
2. Vous serez redirigé vers le Dashboard

### 3. Explorer l'application

- **Dashboard** : Vue d'ensemble
- **Patients** : Gestion des patients (Médecins/Admin)
- **Rendez-vous** : Gestion des rendez-vous
- **Profil** : Informations personnelles

---

## 🛠️ Développement local

### Backend (Spring Boot)

```bash
cd crudApp

# Démarrer MySQL avec Docker
docker-compose up -d mysql mailhog

# Démarrer l'application
mvn spring-boot:run

# Ou avec un profil spécifique
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

### Frontend (React)

```bash
cd crudapp-frontend

# Installer les dépendances
npm install

# Démarrer en mode développement
npm run dev
```

L'application sera accessible sur http://localhost:5173

---

## 🧪 Tests

### Backend

```bash
cd crudApp

# Tous les tests
mvn clean test

# Avec profil test
mvn clean test "-Dspring.profiles.active=test"

# Tests spécifiques
mvn test -Dtest=AuthControllerTest
```

**Résultat attendu** : 62/62 tests passent ✅

### Frontend

```bash
cd crudapp-frontend

# Tests unitaires
npm run test

# Avec couverture
npm run test:coverage
```

---

## 📊 Monitoring

### Prometheus

1. Accédez à http://localhost:9090
2. Exemples de requêtes :

```promql
# Taux de requêtes HTTP
rate(http_server_requests_seconds_count{application="crudApp"}[5m])

# Utilisation mémoire
jvm_memory_used_bytes{application="crudApp", area="heap"}

# Connexions DB actives
hikaricp_connections_active{application="crudApp"}
```

### Grafana

1. Accédez à http://localhost:3000
2. Connectez-vous : admin/admin
3. Ajoutez Prometheus comme source de données :
   - URL : `http://prometheus:9090`
4. Importez le dashboard : `grafana-dashboard.json`

---

## 📧 Emails de test

Tous les emails envoyés par l'application sont capturés par MailHog :

1. Accédez à http://localhost:8025
2. Vous verrez tous les emails :
   - Emails de bienvenue
   - Rappels de rendez-vous
   - Notifications

---

## 🐳 Commandes Docker utiles

```bash
# Démarrer tous les services
docker-compose up -d

# Voir les logs
docker-compose logs -f

# Logs d'un service spécifique
docker logs crudapp-backend -f
docker logs crudapp-frontend -f

# Arrêter tous les services
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v

# Reconstruire les images
docker-compose build --no-cache

# Redémarrer un service
docker-compose restart backend
docker-compose restart frontend

# Voir l'état des services
docker-compose ps

# Exécuter une commande dans un conteneur
docker exec -it crudapp-backend bash
docker exec -it crudapp-mysql mysql -u root -p
```

---

## 🔧 Configuration

### Variables d'environnement Backend

Créez un fichier `.env` dans `crudApp/` :

```env
# JWT
JWT_SECRET=votre-secret-jwt-tres-long-et-securise-minimum-256-bits
JWT_EXPIRATION=86400000
JWT_REFRESH_EXPIRATION=604800000

# MySQL
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=crudapp_db
MYSQL_USER=crudapp_user
MYSQL_PASSWORD=crudapp_password
```

### Variables d'environnement Frontend

Créez un fichier `.env` dans `crudapp-frontend/` :

```env
VITE_API_URL=http://localhost:8080/api
VITE_APP_NAME=CrudApp Medical
VITE_APP_VERSION=1.0.0
```

---

## 🚢 Déploiement en production

### 1. Build des images

```bash
# Backend
cd crudApp
docker build -t crudapp-backend:latest .

# Frontend
cd ../crudapp-frontend
docker build -t crudapp-frontend:latest .
```

### 2. Push vers Docker Hub

```bash
docker tag crudapp-backend:latest votre-username/crudapp-backend:latest
docker push votre-username/crudapp-backend:latest

docker tag crudapp-frontend:latest votre-username/crudapp-frontend:latest
docker push votre-username/crudapp-frontend:latest
```

### 3. Déployer sur le serveur

```bash
# Sur le serveur de production
cd /opt/crudapp
docker compose pull
docker compose up -d --remove-orphans
```

---

## 🔒 Sécurité en production

### Backend

1. **Changer le JWT_SECRET** :
   ```env
   JWT_SECRET=<générer-un-secret-fort-256-bits>
   ```

2. **Utiliser HTTPS** :
   - Configurer un reverse proxy (Nginx)
   - Obtenir un certificat SSL (Let's Encrypt)

3. **Sécuriser les endpoints Actuator** :
   ```java
   // Dans SecurityConfig.java
   http.authorizeHttpRequests(auth -> auth
       .requestMatchers("/actuator/**").hasRole("ADMIN")
       .anyRequest().authenticated()
   );
   ```

4. **Configurer CORS** correctement

### Frontend

1. **Changer les credentials Grafana** :
   ```yaml
   environment:
     - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_ADMIN_PASSWORD}
   ```

2. **Utiliser des variables d'environnement** pour les URLs

---

## 📝 Structure du projet

```
crudApp/
├── crudApp/                    # Backend Spring Boot
│   ├── src/
│   ├── docker-compose.yml
│   ├── Dockerfile
│   ├── pom.xml
│   └── README.md
│
└── crudapp-frontend/           # Frontend React
    ├── src/
    ├── Dockerfile
    ├── nginx.conf
    ├── package.json
    └── README.md
```

---

## 🐛 Troubleshooting

### Le backend ne démarre pas

```bash
# Vérifier les logs
docker logs crudapp-backend --tail=100

# Vérifier que MySQL est prêt
docker logs crudapp-mysql

# Redémarrer le backend
docker-compose restart app
```

### Le frontend ne se connecte pas au backend

1. Vérifiez que le backend est accessible : http://localhost:8080/actuator/health
2. Vérifiez la variable `VITE_API_URL` dans `.env`
3. Vérifiez les logs du frontend : `docker logs crudapp-frontend`

### Erreur de connexion MySQL

```bash
# Se connecter à MySQL
docker exec -it crudapp-mysql mysql -u crudapp_user -p

# Vérifier la base de données
SHOW DATABASES;
USE crudapp_db;
SHOW TABLES;
```

### Prometheus ne collecte pas les métriques

1. Vérifiez http://localhost:9090/targets
2. Le target `crudapp-backend` doit être "UP"
3. Vérifiez http://localhost:8080/actuator/prometheus

---

## 📚 Documentation

- **Backend** : [README.md](./crudApp/README.md)
- **Frontend** : [README.md](./crudapp-frontend/README.md)
- **Prometheus** : [PROMETHEUS_GUIDE.md](./crudApp/PROMETHEUS_GUIDE.md)
- **Frontend Setup** : [FRONTEND_SETUP_SUMMARY.md](./crudapp-frontend/FRONTEND_SETUP_SUMMARY.md)

---

## ✅ Checklist de vérification

- [ ] Docker et Docker Compose installés
- [ ] Tous les services démarrés avec `docker-compose up -d`
- [ ] Backend accessible sur http://localhost:8080
- [ ] Frontend accessible sur http://localhost:3001
- [ ] Compte utilisateur créé
- [ ] Connexion réussie
- [ ] Dashboard affiché
- [ ] Prometheus collecte les métriques
- [ ] Grafana configuré
- [ ] MailHog capture les emails

---

## 🎉 Félicitations !

Votre application Full Stack est maintenant opérationnelle !

**Prochaines étapes** :
1. Explorez les fonctionnalités
2. Créez des patients et des rendez-vous
3. Consultez les métriques dans Grafana
4. Personnalisez l'application selon vos besoins

**Bon développement ! 🚀**
