# Guide d'intégration Prometheus et Grafana

## 📊 Vue d'ensemble

Ce projet intègre **Prometheus** pour la collecte de métriques et **Grafana** pour la visualisation des données de monitoring de l'application CrudApp Medical.

## 🎯 Composants installés

### 1. **Prometheus** (Port 9090)
- Collecte automatique des métriques de l'application Spring Boot
- Stockage des données de métriques
- Interface web pour requêtes PromQL

### 2. **Grafana** (Port 3000)
- Visualisation des métriques via des dashboards
- Alertes configurables
- Graphiques en temps réel

### 3. **Spring Boot Actuator + Micrometer**
- Exposition des métriques au format Prometheus
- Endpoint `/actuator/prometheus`
- Métriques JVM, HTTP, base de données, etc.

## 🚀 Démarrage rapide

### Démarrer tous les services avec Docker Compose

```bash
docker-compose up -d
```

Cela démarre :
- MySQL (port 3307)
- MailHog (ports 1025, 8025)
- Application Spring Boot (port 8080)
- Prometheus (port 9090)
- Grafana (port 3000)

### Vérifier que les services sont actifs

```bash
docker-compose ps
```

## 📍 URLs d'accès

| Service | URL | Credentials |
|---------|-----|-------------|
| Application | http://localhost:8080 | - |
| Swagger UI | http://localhost:8080/swagger-ui.html | - |
| Actuator Health | http://localhost:8080/actuator/health | - |
| Prometheus Metrics | http://localhost:8080/actuator/prometheus | - |
| Prometheus UI | http://localhost:9090 | - |
| Grafana | http://localhost:3000 | admin / admin |
| MailHog | http://localhost:8025 | - |

## 🔧 Configuration

### Métriques exposées par l'application

L'application expose automatiquement les métriques suivantes :

#### Métriques HTTP
- `http_server_requests_seconds_count` - Nombre de requêtes HTTP
- `http_server_requests_seconds_sum` - Durée totale des requêtes
- `http_server_requests_seconds_max` - Durée maximale d'une requête

#### Métriques JVM
- `jvm_memory_used_bytes` - Mémoire utilisée
- `jvm_memory_max_bytes` - Mémoire maximale
- `jvm_gc_pause_seconds` - Pauses du garbage collector
- `jvm_threads_live_threads` - Nombre de threads actifs

#### Métriques Base de données (HikariCP)
- `hikaricp_connections_active` - Connexions actives
- `hikaricp_connections_idle` - Connexions inactives
- `hikaricp_connections_pending` - Connexions en attente

#### Métriques Système
- `system_cpu_usage` - Utilisation CPU du système
- `process_cpu_usage` - Utilisation CPU du processus
- `system_load_average_1m` - Charge système moyenne

### Configuration Prometheus

Le fichier `prometheus.yml` configure Prometheus pour scraper les métriques :

```yaml
scrape_configs:
  - job_name: 'crudapp-backend'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 10s
    static_configs:
      - targets: ['app:8080']
```

## 📊 Configuration Grafana

### 1. Première connexion

1. Accédez à http://localhost:3000
2. Connectez-vous avec :
   - **Username**: `admin`
   - **Password**: `admin`
3. Changez le mot de passe (ou cliquez sur "Skip")

### 2. Ajouter Prometheus comme source de données

1. Cliquez sur **Configuration** (⚙️) → **Data Sources**
2. Cliquez sur **Add data source**
3. Sélectionnez **Prometheus**
4. Configurez :
   - **Name**: `Prometheus`
   - **URL**: `http://prometheus:9090`
   - **Access**: `Server (default)`
5. Cliquez sur **Save & Test**

### 3. Importer le dashboard pré-configuré

#### Option A : Import manuel du fichier JSON

1. Cliquez sur **+** → **Import**
2. Cliquez sur **Upload JSON file**
3. Sélectionnez le fichier `grafana-dashboard.json`
4. Sélectionnez la source de données **Prometheus**
5. Cliquez sur **Import**

#### Option B : Créer un dashboard personnalisé

1. Cliquez sur **+** → **Dashboard**
2. Cliquez sur **Add new panel**
3. Dans la requête, utilisez des expressions PromQL comme :
   ```promql
   rate(http_server_requests_seconds_count{application="crudApp"}[5m])
   ```

## 📈 Exemples de requêtes PromQL utiles

### Taux de requêtes HTTP par seconde
```promql
rate(http_server_requests_seconds_count{application="crudApp"}[5m])
```

### Temps de réponse moyen (95e percentile)
```promql
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application="crudApp"}[5m]))
```

### Utilisation mémoire JVM
```promql
jvm_memory_used_bytes{application="crudApp", area="heap"}
```

### Connexions base de données actives
```promql
hikaricp_connections_active{application="crudApp"}
```

### Taux d'erreurs HTTP (status 5xx)
```promql
rate(http_server_requests_seconds_count{application="crudApp", status=~"5.."}[5m])
```

### CPU usage
```promql
process_cpu_usage{application="crudApp"}
```

## 🔍 Monitoring en production

### Métriques importantes à surveiller

1. **Performance HTTP**
   - Temps de réponse moyen
   - Taux de requêtes par seconde
   - Taux d'erreurs (4xx, 5xx)

2. **Ressources JVM**
   - Utilisation mémoire heap
   - Fréquence du garbage collection
   - Nombre de threads

3. **Base de données**
   - Connexions actives vs disponibles
   - Temps d'attente pour obtenir une connexion
   - Requêtes lentes

4. **Système**
   - CPU usage
   - Charge système
   - Disponibilité (uptime)

### Alertes recommandées

Vous pouvez configurer des alertes dans Grafana pour :

- Temps de réponse > 1 seconde
- Taux d'erreurs > 5%
- Utilisation mémoire > 80%
- Connexions DB > 80% du pool
- CPU usage > 80%

## 🛠️ Commandes utiles

### Voir les logs Prometheus
```bash
docker logs crudapp-prometheus
```

### Voir les logs Grafana
```bash
docker logs crudapp-grafana
```

### Redémarrer Prometheus après modification de la config
```bash
docker-compose restart prometheus
```

### Vérifier les métriques directement
```bash
curl http://localhost:8080/actuator/prometheus
```

### Vérifier la santé de l'application
```bash
curl http://localhost:8080/actuator/health
```

## 🔒 Sécurité

### En développement
- Les endpoints `/actuator/**` sont accessibles sans authentification
- Grafana utilise admin/admin par défaut

### En production (recommandations)
1. **Sécuriser les endpoints Actuator** :
   ```java
   .requestMatchers("/actuator/**").hasRole("ADMIN")
   ```

2. **Changer les credentials Grafana** :
   ```yaml
   environment:
     - GF_SECURITY_ADMIN_PASSWORD=${GRAFANA_ADMIN_PASSWORD}
   ```

3. **Utiliser HTTPS** pour tous les services

4. **Limiter l'exposition des métriques** :
   ```properties
   management.endpoints.web.exposure.include=health,prometheus
   ```

## 📚 Ressources supplémentaires

- [Documentation Prometheus](https://prometheus.io/docs/)
- [Documentation Grafana](https://grafana.com/docs/)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer Documentation](https://micrometer.io/docs)
- [PromQL Cheat Sheet](https://promlabs.com/promql-cheat-sheet/)

## 🐛 Troubleshooting

### Prometheus ne collecte pas les métriques

1. Vérifiez que l'application est démarrée :
   ```bash
   curl http://localhost:8080/actuator/health
   ```

2. Vérifiez que les métriques sont exposées :
   ```bash
   curl http://localhost:8080/actuator/prometheus
   ```

3. Vérifiez la configuration Prometheus :
   - Accédez à http://localhost:9090/targets
   - Le target `crudapp-backend` doit être "UP"

### Grafana ne se connecte pas à Prometheus

1. Vérifiez que Prometheus est accessible depuis Grafana :
   ```bash
   docker exec crudapp-grafana wget -O- http://prometheus:9090/api/v1/status/config
   ```

2. Vérifiez les logs Grafana :
   ```bash
   docker logs crudapp-grafana
   ```

### Les dashboards sont vides

1. Vérifiez que la source de données est correctement configurée
2. Vérifiez que l'application génère du trafic
3. Ajustez la plage de temps dans Grafana (en haut à droite)

## ✅ Checklist de vérification

- [ ] Docker Compose démarre tous les services sans erreur
- [ ] L'application Spring Boot est accessible sur http://localhost:8080
- [ ] Les métriques Prometheus sont accessibles sur http://localhost:8080/actuator/prometheus
- [ ] Prometheus UI affiche le target "crudapp-backend" comme "UP" sur http://localhost:9090/targets
- [ ] Grafana est accessible sur http://localhost:3000
- [ ] La source de données Prometheus est configurée dans Grafana
- [ ] Le dashboard affiche des métriques en temps réel

## 🎉 Prochaines étapes

1. Personnalisez les dashboards Grafana selon vos besoins
2. Configurez des alertes pour les métriques critiques
3. Ajoutez des métriques métier personnalisées dans votre code
4. Intégrez un système de notification (Slack, email, etc.)
5. Configurez la rétention des données Prometheus
