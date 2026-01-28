# Configuration des notifications par email

## 📧 Vue d'ensemble

Le système de notification par email a été implémenté pour envoyer automatiquement des emails aux patients et médecins lors des événements suivants :

### Pour les patients :
- ✅ **Confirmation de demande de rendez-vous** - Envoyé immédiatement après la création d'un rendez-vous
- ✅ **Confirmation par le médecin** - Envoyé quand le médecin confirme le rendez-vous
- ✅ **Annulation de rendez-vous** - Envoyé lors de l'annulation
- ✅ **Fin de consultation** - Envoyé quand le médecin marque le rendez-vous comme terminé
- ⏰ **Rappel de rendez-vous** - Peut être envoyé 24h avant le rendez-vous (à implémenter avec un scheduler)

### Pour les médecins :
- 📩 **Nouvelle demande de rendez-vous** - Envoyé quand un patient demande un rendez-vous

---

## ⚙️ Configuration

### 1. Configuration Gmail (Recommandé pour le développement)

#### Étape 1 : Activer l'authentification à deux facteurs
1. Allez sur votre compte Google : https://myaccount.google.com/
2. Sécurité → Validation en deux étapes → Activer

#### Étape 2 : Générer un mot de passe d'application
1. Allez sur : https://myaccount.google.com/apppasswords
2. Sélectionnez "Autre (nom personnalisé)"
3. Entrez "CrudApp Medical"
4. Cliquez sur "Générer"
5. Copiez le mot de passe de 16 caractères généré

#### Étape 3 : Configurer les variables d'environnement

**Windows (PowerShell) :**
```powershell
$env:EMAIL_USERNAME="votre-email@gmail.com"
$env:EMAIL_PASSWORD="votre-mot-de-passe-application"
```

**Windows (CMD) :**
```cmd
set EMAIL_USERNAME=votre-email@gmail.com
set EMAIL_PASSWORD=votre-mot-de-passe-application
```

**Linux/Mac :**
```bash
export EMAIL_USERNAME="votre-email@gmail.com"
export EMAIL_PASSWORD="votre-mot-de-passe-application"
```

#### Étape 4 : Modifier application.properties (optionnel)

Si vous ne voulez pas utiliser de variables d'environnement, modifiez directement :

```properties
spring.mail.username=votre-email@gmail.com
spring.mail.password=votre-mot-de-passe-application
```

⚠️ **ATTENTION** : Ne commitez JAMAIS vos identifiants dans Git !

---

### 2. Configuration avec d'autres fournisseurs

#### Outlook/Hotmail
```properties
spring.mail.host=smtp-mail.outlook.com
spring.mail.port=587
spring.mail.username=votre-email@outlook.com
spring.mail.password=votre-mot-de-passe
```

#### Yahoo Mail
```properties
spring.mail.host=smtp.mail.yahoo.com
spring.mail.port=587
spring.mail.username=votre-email@yahoo.com
spring.mail.password=votre-mot-de-passe-application
```

#### Serveur SMTP personnalisé
```properties
spring.mail.host=smtp.votre-domaine.com
spring.mail.port=587
spring.mail.username=votre-email@votre-domaine.com
spring.mail.password=votre-mot-de-passe
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

## 🧪 Test de la configuration

### 1. Démarrer l'application
```bash
mvn spring-boot:run
```

### 2. Créer un rendez-vous via l'API

**Endpoint :** `POST /api/appointments`

**Headers :**
```
Authorization: Bearer <votre-jwt-token>
Content-Type: application/json
```

**Body :**
```json
{
  "doctorId": 1,
  "appointmentDateTime": "2025-12-30T10:00:00",
  "reason": "Consultation de routine"
}
```

### 3. Vérifier les logs

Vous devriez voir dans les logs :
```
INFO  c.e.c.service.AppointmentService - Rendez-vous créé avec succès - ID: 1
INFO  c.e.c.service.AppointmentService - Emails de notification envoyés pour le rendez-vous ID: 1
INFO  c.e.c.service.EmailService - Email de confirmation envoyé au patient 2 pour le rendez-vous 1
INFO  c.e.c.service.EmailService - Email de notification envoyé au médecin 1 pour le rendez-vous 1
```

### 4. Vérifier la réception des emails

- Le patient devrait recevoir un email de confirmation
- Le médecin devrait recevoir un email de notification

---

## 🎨 Personnalisation des templates

Les templates d'email sont définis dans `EmailService.java`. Vous pouvez les personnaliser en modifiant les méthodes :

- `buildAppointmentConfirmationEmailBody()` - Email de confirmation patient
- `buildDoctorNotificationEmailBody()` - Email de notification médecin
- `buildStatusUpdateEmailBody()` - Email de mise à jour de statut
- `buildReminderEmailBody()` - Email de rappel

---

## 🔧 Désactiver les emails en développement

Si vous voulez désactiver temporairement l'envoi d'emails, ajoutez dans `application.properties` :

```properties
# Désactiver l'envoi réel d'emails (les emails seront loggés dans la console)
spring.mail.host=localhost
spring.mail.port=1025
```

Ou utilisez un serveur SMTP de test comme [MailHog](https://github.com/mailhog/MailHog) ou [Mailtrap](https://mailtrap.io/).

---

## 📊 Monitoring

Les emails sont envoyés de manière asynchrone pour ne pas bloquer les requêtes. En cas d'erreur d'envoi :

- L'erreur est loggée mais ne bloque pas la création du rendez-vous
- Le rendez-vous est créé même si l'email échoue
- Vérifiez les logs pour diagnostiquer les problèmes

---

## 🚀 Production

Pour la production, utilisez :

1. **Variables d'environnement** pour les credentials
2. **Un service d'email professionnel** (SendGrid, AWS SES, Mailgun)
3. **Un système de retry** en cas d'échec
4. **Un système de queue** (RabbitMQ, Kafka) pour gérer les pics de charge

### Exemple avec SendGrid

```properties
spring.mail.host=smtp.sendgrid.net
spring.mail.port=587
spring.mail.username=apikey
spring.mail.password=${SENDGRID_API_KEY}
```

---

## ❓ Dépannage

### Problème : "Authentication failed"
- Vérifiez que vous utilisez un mot de passe d'application (pas votre mot de passe Gmail)
- Vérifiez que l'authentification à deux facteurs est activée

### Problème : "Connection timeout"
- Vérifiez votre connexion internet
- Vérifiez que le port 587 n'est pas bloqué par votre firewall
- Essayez le port 465 avec SSL

### Problème : Les emails ne sont pas reçus
- Vérifiez les spams/courrier indésirable
- Vérifiez les logs de l'application
- Vérifiez que l'adresse email du patient/médecin est correcte

### Problème : "Could not convert socket to TLS"
- Ajoutez dans application.properties :
```properties
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
```

---

## 📝 Notes importantes

1. **Username comme email** : Actuellement, le système utilise le `username` de l'utilisateur comme adresse email. Assurez-vous que les utilisateurs s'inscrivent avec leur email comme username.

2. **Emails asynchrones** : Les emails sont envoyés en arrière-plan grâce à `@Async`. Cela améliore les performances mais rend le débogage plus difficile.

3. **Templates HTML** : Les emails utilisent des templates HTML pour un meilleur rendu. Ils sont responsive et s'affichent bien sur mobile.

4. **Sécurité** : Ne jamais committer les credentials dans le code. Utilisez toujours des variables d'environnement ou un gestionnaire de secrets.

---

## 📚 Ressources

- [Spring Boot Mail Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/io.html#io.email)
- [JavaMail API](https://javaee.github.io/javamail/)
- [Gmail App Passwords](https://support.google.com/accounts/answer/185833)
