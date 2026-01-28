# 🐳 Docker Guide - CrudApp Medical

## 📋 Prérequis

- Docker Desktop installé et en cours d'exécution
- PowerShell (Windows) ou Terminal (Linux/Mac)

## 🚀 Démarrage de l'application

### 1. Arrêter et nettoyer les conteneurs existants

```powershell
docker-compose down -v
```

### 2. Reconstruire et démarrer les conteneurs

```powershell
docker-compose up -d --build
```

### 3. Vérifier l'état des conteneurs

```powershell
docker ps
```

### 4. Voir les logs en temps réel

```powershell
# Logs de l'application Spring Boot
docker logs -f crudapp-backend

# Logs de MySQL
docker logs -f crudapp-mysql
```

## 🔍 Diagnostic des problèmes

### Exécuter le script de diagnostic (Windows)

```powershell
.\docker-diagnose.ps1
```

### Voir les logs d'erreur

```powershell
# Voir les 100 dernières lignes des logs
docker logs crudapp-backend --tail 100

# Voir les logs depuis le début
docker logs crudapp-backend

# Suivre les logs en temps réel
docker logs -f crudapp-backend
```

### Vérifier la santé des conteneurs

```powershell
docker inspect --format='{{.State.Health.Status}}' crudapp-backend
```

### Tester la connexion à l'application

```powershell
# Test du health endpoint
curl http://localhost:8080/actuator/health

# Test avec Invoke-WebRequest (PowerShell)
Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing
```

## 🛠️ Résolution des problèmes courants

### Problème 1 : L'application redémarre en boucle

**Cause :** L'application plante au démarrage

**Solution :**
1. Voir les logs pour identifier l'erreur :
   ```powershell
   docker logs crudapp-backend --tail 100
   ```

2. Vérifier que MySQL est démarré :
   ```powershell
   docker ps | findstr mysql
   ```

3. Vérifier la connexion à MySQL :
   ```powershell
   docker exec crudapp-mysql mysql -u crudapp_user -pcrudapp_password -e "SELECT 1" crudapp_db
   ```

### Problème 2 : Erreur de connexion à la base de données

**Cause :** MySQL n'est pas encore prêt quand l'application démarre

**Solution :**
1. Attendre que MySQL soit complètement démarré (environ 30-60 secondes)
2. Vérifier le health status de MySQL :
   ```powershell
   docker inspect --format='{{.State.Health.Status}}' crudapp-mysql
   ```

3. Si MySQL est healthy, redémarrer l'application :
   ```powershell
   docker restart crudapp-backend
   ```

### Problème 3 : Port 8080 déjà utilisé

**Cause :** Une autre application utilise le port 8080

**Solution :**
1. Identifier l'application qui utilise le port :
   ```powershell
   netstat -ano | findstr :8080
   ```

2. Arrêter l'application ou changer le port dans docker-compose.yml :
   ```yaml
   ports:
     - "8081:8080"  # Utiliser le port 8081 au lieu de 8080
   ```

### Problème 4 : Erreur de build Maven

**Cause :** Problème de dépendances ou de compilation

**Solution :**
1. Nettoyer et reconstruire :
   ```powershell
   docker-compose down
   docker-compose build --no-cache
   docker-compose up -d
   ```

2. Vérifier les logs de build :
   ```powershell
   docker-compose build
   ```

## 📊 Accès aux services

| Service | URL | Description |
|---------|-----|-------------|
| Application API | http://localhost:8080 | API REST de l'application |
| Actuator Health | http://localhost:8080/actuator/health | Health check |
| Swagger UI | http://localhost:8080/swagger-ui.html | Documentation API |
| MailHog Web UI | http://localhost:8025 | Interface email de test |
| MySQL | localhost:3307 | Base de données (port externe) |

## 🧪 Tests Postman

Une fois l'application démarrée, vous pouvez tester avec Postman :

1. **Register Doctor** : `POST http://localhost:8080/api/auth/register`
2. **Login** : `POST http://localhost:8080/api/auth/login`
3. **Create Availability** : `POST http://localhost:8080/api/doctors/my-availability`

Voir `POSTMAN_TESTS.md` pour la liste complète des tests.

## 📝 Logs et monitoring

### Logs de l'application

Les logs sont stockés dans le volume Docker `app_logs` et peuvent être consultés :

```powershell
# Voir les logs dans le conteneur
docker exec crudapp-backend cat /app/logs/crudapp.log

# Copier les logs sur l'hôte
docker cp crudapp-backend:/app/logs/crudapp.log ./logs/
```

### Monitoring avec Actuator

```powershell
# Health check
curl http://localhost:8080/actuator/health

# Info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics
```

## 🛑 Arrêt de l'application

```powershell
# Arrêter les conteneurs
docker-compose down

# Arrêter et supprimer les volumes (supprime aussi les données)
docker-compose down -v
```

## 🔄 Mise à jour de l'application

```powershell
# Arrêter
docker-compose down

# Reconstruire avec les derniers changements
docker-compose build

# Démarrer
docker-compose up -d
```

## 📚 Commandes utiles

```powershell
# Voir tous les conteneurs (y compris arrêtés)
docker ps -a

# Voir les ressources utilisées
docker stats

# Entrer dans un conteneur
docker exec -it crudapp-backend sh

# Exécuter une commande dans un conteneur
docker exec crudapp-backend ls -la

# Voir les logs de tous les services
docker-compose logs

# Voir les logs d'un service spécifique
docker-compose logs app
docker-compose logs mysql
docker-compose logs mailhog
```

## ⚠️ Notes importantes

1. **Premier démarrage** : Le premier démarrage peut prendre 2-3 minutes car Maven doit télécharger les dépendances
2. **Données** : Les données sont persistées dans le volume Docker `mysql_data`. Utilisez `docker-compose down -v` pour supprimer toutes les données
3. **Health checks** : L'application a un health check qui vérifie `/actuator/health` toutes les 30 secondes
4. **Restart policy** : Le conteneur redémarre jusqu'à 5 fois en cas d'échec, puis s'arrête pour éviter une boucle infinie

## 🆘 Support

Si vous rencontrez des problèmes :

1. Exécutez le script de diagnostic : `.\docker-diagnose.ps1`
2. Consultez les logs : `docker logs crudapp-backend --tail 100`
3. Vérifiez la documentation : `POSTMAN_TESTS.md`
