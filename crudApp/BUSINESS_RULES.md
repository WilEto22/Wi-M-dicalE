# 📋 Règles Métier - Système de Rendez-vous Médicaux

## 🚫 Règles d'Annulation de Rendez-vous

### Pour les Patients

#### Rendez-vous en Attente (PENDING)
- **Délai minimum:** 24 heures avant le rendez-vous
- **Calcul:** Heures calendaires (incluant week-ends et jours fériés)
- **Exemple:**
  - Rendez-vous le lundi 25/12 à 10h
  - Peut annuler jusqu'au dimanche 24/12 à 10h

#### Rendez-vous Confirmés (CONFIRMED)
- **Délai minimum:** 1 jour ouvrable complet avant le rendez-vous
- **Calcul:** Jours ouvrables uniquement (lundi-vendredi, hors jours fériés)
- **Exemples:**

  **Exemple 1 - Annulation autorisée:**
  - Rendez-vous: Mercredi 27/12 à 14h
  - Tentative d'annulation: Lundi 25/12 à 10h
  - Résultat: ✅ AUTORISÉ (1 jour ouvrable: mardi 26/12)

  **Exemple 2 - Annulation refusée:**
  - Rendez-vous: Lundi 25/12 à 14h
  - Tentative d'annulation: Vendredi 22/12 à 16h
  - Résultat: ❌ REFUSÉ (week-end ne compte pas comme jour ouvrable)

  **Exemple 3 - Avec jour férié:**
  - Rendez-vous: Jeudi 26/12 à 10h
  - Jour férié: Lundi 25/12 (Noël)
  - Tentative d'annulation: Vendredi 22/12 à 15h
  - Résultat: ❌ REFUSÉ (lundi férié ne compte pas)

### Pour les Médecins
- **Aucune restriction de délai**
- Peuvent annuler à tout moment
- Email de notification envoyé automatiquement au patient

---

## 📅 Jours Ouvrables

### Définition
Un jour ouvrable est un jour:
- Du lundi au vendredi
- Qui n'est pas un jour férié

### Jours Fériés Français Pris en Compte
- 🎆 1er janvier - Jour de l'an
- 🌹 1er mai - Fête du travail
- 🎖️ 8 mai - Victoire 1945
- 🇫🇷 14 juillet - Fête nationale
- ⛪ 15 août - Assomption
- 🕯️ 1er novembre - Toussaint
- 🎖️ 11 novembre - Armistice 1918
- 🎄 25 décembre - Noël

**Note:** Les jours fériés mobiles (Pâques, Ascension, Pentecôte) peuvent être ajoutés via configuration.

---

## 📧 Notifications Automatiques

### Email de Confirmation
- **Déclencheur:** Création d'un nouveau rendez-vous
- **Destinataires:** Patient ET Médecin
- **Contenu:**
  - Date et heure du rendez-vous
  - Nom du médecin / patient
  - Spécialité médicale
  - Motif de consultation
  - Statut: PENDING

### Email de Rappel (24h avant)
- **Déclencheur:** Tâche planifiée (toutes les heures)
- **Condition:** Rendez-vous CONFIRMED dans 23-25h
- **Destinataire:** Patient uniquement
- **Contenu:**
  - Rappel du rendez-vous imminent
  - Date, heure, lieu
  - Nom du médecin
  - Instructions de préparation (si applicable)

### Email de Changement de Statut
- **Déclencheurs:**
  - Confirmation par le médecin (PENDING → CONFIRMED)
  - Annulation (→ CANCELLED)
  - Complétion (CONFIRMED → COMPLETED)
- **Destinataire:** Patient
- **Contenu:**
  - Nouveau statut
  - Informations du rendez-vous
  - Notes du médecin (si complétion)

---

## 🤖 Tâches Automatiques Planifiées

### 1. Envoi de Rappels
- **Fréquence:** Toutes les heures (à la minute 0)
- **Cron:** `0 0 * * * *`
- **Action:**
  - Recherche les rendez-vous CONFIRMED dans 23-25h
  - Envoie un email de rappel au patient
  - Log les succès et échecs

### 2. Auto-complétion des Rendez-vous Passés
- **Fréquence:** Toutes les 6 heures
- **Cron:** `0 0 */6 * * *`
- **Action:**
  - Recherche les rendez-vous CONFIRMED passés
  - Change le statut en COMPLETED
  - Ajoute une note automatique: "Rendez-vous marqué automatiquement comme terminé"

### 3. Nettoyage des Anciens Rendez-vous
- **Fréquence:** Tous les jours à 2h du matin
- **Cron:** `0 0 2 * * *`
- **Action:**
  - Supprime les rendez-vous CANCELLED de plus de 6 mois
  - Libère de l'espace en base de données
  - Log le nombre de rendez-vous supprimés

---

## 🔐 Règles de Sécurité

### Création de Rendez-vous
- ✅ Seuls les PATIENTS peuvent créer des rendez-vous
- ✅ Le patient ne peut prendre rendez-vous qu'avec un DOCTOR
- ✅ Vérification de la disponibilité du créneau
- ✅ Pas de double réservation possible

### Confirmation de Rendez-vous
- ✅ Seul le MÉDECIN concerné peut confirmer
- ✅ Seuls les rendez-vous PENDING peuvent être confirmés
- ✅ Email automatique au patient

### Annulation de Rendez-vous
- ✅ Patient OU Médecin peuvent annuler
- ✅ Respect des délais (24h ou 1 jour ouvrable)
- ✅ Email automatique à l'autre partie

### Complétion de Rendez-vous
- ✅ Seul le MÉDECIN concerné peut terminer
- ✅ Seuls les rendez-vous CONFIRMED peuvent être terminés
- ✅ Notes médicales obligatoires
- ✅ Email automatique au patient

---

## 📊 Statuts de Rendez-vous

### PENDING (En attente)
- **État initial** après création par le patient
- **Actions possibles:**
  - Médecin: Confirmer ou Annuler
  - Patient: Annuler (24h minimum)

### CONFIRMED (Confirmé)
- **Après confirmation** par le médecin
- **Actions possibles:**
  - Médecin: Annuler ou Terminer
  - Patient: Annuler (1 jour ouvrable minimum)
- **Notifications:** Rappel 24h avant

### CANCELLED (Annulé)
- **État final** après annulation
- **Aucune action possible**
- **Nettoyage:** Suppression après 6 mois

### COMPLETED (Terminé)
- **État final** après la consultation
- **Contient:** Notes médicales du médecin
- **Aucune action possible**

---

## ⚠️ Messages d'Erreur

### Annulation Tardive (PENDING)
```json
{
  "status": 400,
  "message": "Les rendez-vous ne peuvent être annulés que 24 heures à l'avance. Il ne reste que 12 heures avant votre rendez-vous.",
  "timestamp": "2024-12-25T17:00:00"
}
```

### Annulation Tardive (CONFIRMED)
```json
{
  "status": 400,
  "message": "Les rendez-vous confirmés ne peuvent être annulés qu'avec au moins 1 jour ouvrable d'avance. Il ne reste que 0 jour(s) ouvrable(s) avant votre rendez-vous.",
  "timestamp": "2024-12-25T17:00:00"
}
```

### Créneau Non Disponible
```json
{
  "status": 400,
  "message": "Ce créneau n'est pas disponible. Veuillez choisir un autre horaire.",
  "timestamp": "2024-12-25T17:00:00"
}
```

### Action Non Autorisée
```json
{
  "status": 403,
  "message": "Vous n'êtes pas autorisé à effectuer cette action sur ce rendez-vous.",
  "timestamp": "2024-12-25T17:00:00"
}
```

---

## 🧪 Tests et Validation

### Tests Unitaires
- ✅ Calcul des jours ouvrables
- ✅ Validation des délais d'annulation
- ✅ Vérification des disponibilités
- ✅ Envoi des emails (mocks)

### Tests d'Intégration
- ✅ Workflow complet de rendez-vous
- ✅ Tâches planifiées
- ✅ Gestion des erreurs

### Tests Manuels Recommandés
1. Créer un rendez-vous pour demain
2. Tenter d'annuler (devrait échouer)
3. Créer un rendez-vous dans 3 jours
4. Annuler (devrait réussir)
5. Vérifier la réception des emails

---

## 📝 Configuration

### Variables d'Environnement
```properties
# Email
EMAIL_USERNAME=your-email@gmail.com
EMAIL_PASSWORD=your-app-password

# Scheduler (optionnel)
scheduler.enabled=true
scheduler.reminder.cron=0 0 * * * *
scheduler.cleanup.cron=0 0 2 * * *
```

### Personnalisation des Jours Fériés
Les jours fériés peuvent être ajoutés/supprimés via:
```java
BusinessDayCalculator.addHoliday(LocalDate.of(2024, 4, 1)); // Lundi de Pâques
BusinessDayCalculator.removeHoliday(LocalDate.of(2024, 8, 15)); // Retirer Assomption
```

---

## 🔄 Workflow Complet

```
1. Patient crée rendez-vous
   ↓
   Status: PENDING
   📧 Email → Patient + Médecin

2. Médecin confirme
   ↓
   Status: CONFIRMED
   📧 Email → Patient

3. 24h avant
   ↓
   🤖 Tâche planifiée
   📧 Email rappel → Patient

4. Consultation
   ↓
   Médecin termine avec notes
   ↓
   Status: COMPLETED
   📧 Email → Patient
```

---

## 📞 Support

Pour toute question sur les règles métier:
- Consulter la documentation Swagger: `/swagger-ui.html`
- Vérifier les logs de l'application
- Contacter l'équipe de développement
