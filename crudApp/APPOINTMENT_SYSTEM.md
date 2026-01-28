# 📅 Système de Rendez-vous Médicaux - Documentation

## 🎯 Vue d'ensemble

L'application a été transformée en un **système complet de gestion de rendez-vous médicaux** permettant à deux types d'utilisateurs d'interagir :

### Types d'utilisateurs

1. **👨‍⚕️ DOCTOR (Médecin)**
   - Peut définir ses disponibilités
   - Reçoit et gère les demandes de rendez-vous
   - Peut confirmer, annuler ou terminer des rendez-vous
   - Ajoute des notes médicales après consultation

2. **🧑‍⚕️ PATIENT (Patient)**
   - Peut rechercher des médecins par spécialité
   - Consulte les créneaux disponibles
   - Prend des rendez-vous
   - Peut annuler ses rendez-vous

---

## 📊 Architecture du Système

### Nouvelles Entités

#### 1. **User** (Modifié)
```java
- userType: UserType (DOCTOR/PATIENT) - OBLIGATOIRE
- specialty: MedicalSpecialty (pour les médecins uniquement)
- fullName: String (pour les patients)
- phoneNumber: String (pour les patients)
- dateOfBirth: LocalDate (pour les patients)
```

#### 2. **Appointment**
```java
- doctor: User (médecin)
- patient: User (patient)
- appointmentDateTime: LocalDateTime
- status: AppointmentStatus (PENDING, CONFIRMED, CANCELLED, COMPLETED)
- reason: String (motif de consultation)
- doctorNotes: String (notes du médecin)
```

#### 3. **DoctorAvailability**
```java
- doctor: User
- dayOfWeek: DayOfWeek (MONDAY, TUESDAY, etc.)
- startTime: LocalTime
- endTime: LocalTime
- slotDurationMinutes: Integer (durée d'un créneau, défaut: 30 min)
- isActive: Boolean
```

---

## 🔐 Inscription des Utilisateurs

### Inscription d'un Médecin

**Endpoint:** `POST /api/auth/register`

```json
{
  "username": "dr.martin",
  "password": "password123",
  "userType": "DOCTOR",
  "specialty": "CARDIOLOGIE"
}
```

**Spécialités disponibles:**
- MEDECINE_GENERALE
- CARDIOLOGIE
- PNEUMOLOGIE
- GASTRO_ENTEROLOGIE
- NEPHROLOGIE
- ENDOCRINOLOGIE
- RHUMATOLOGIE
- NEUROLOGIE
- DERMATOLOGIE
- HEMATOLOGIE
- ONCOLOGIE
- INFECTIOLOGIE
- ALLERGOLOGIE
- GERIATRIE
- PEDIATRIE
- PSYCHIATRIE
- CHIRURGIE_GENERALE
- CHIRURGIE_ORTHOPEDIQUE
- NEUROCHIRURGIE
- CHIRURGIE_CARDIAQUE
- CHIRURGIE_PLASTIQUE
- CHIRURGIE_VISCERALE
- CHIRURGIE_PEDIATRIQUE
- OPHTALMOLOGIE
- ORL
- AUDIOLOGIE
- RADIOLOGIE
- MEDECINE_NUCLEAIRE
- ANATOMOPATHOLOGIE
- BIOLOGIE_MEDICALE
- MEDECINE_URGENCE
- MEDECINE_TRAVAIL
- MEDECINE_LEGALE
- MEDECINE_SPORT
- MEDECINE_PHYSIQUE_READAPTATION
- ANESTHESIE_REANIMATION
- SANTE_PUBLIQUE

### Inscription d'un Patient

**Endpoint:** `POST /api/auth/register`

```json
{
  "username": "jean.dupont",
  "password": "password123",
  "userType": "PATIENT",
  "fullName": "Jean Dupont",
  "phoneNumber": "+33612345678",
  "dateOfBirth": "1990-05-15"
}
```

---

## 👨‍⚕️ Endpoints pour les Médecins

### 1. Définir ses disponibilités

**POST** `/api/doctors/my-availability`

```json
{
  "dayOfWeek": "MONDAY",
  "startTime": "09:00:00",
  "endTime": "17:00:00",
  "slotDurationMinutes": 30,
  "isActive": true
}
```

### 2. Consulter ses disponibilités

**GET** `/api/doctors/my-availability`

### 3. Modifier une disponibilité

**PUT** `/api/doctors/my-availability/{id}`

### 4. Supprimer une disponibilité

**DELETE** `/api/doctors/my-availability/{id}`

### 5. Voir ses rendez-vous

**GET** `/api/appointments/my-appointments`

**GET** `/api/appointments/upcoming` (prochains rendez-vous)

### 6. Confirmer un rendez-vous

**PUT** `/api/appointments/{id}/confirm`

### 7. Annuler un rendez-vous

**PUT** `/api/appointments/{id}/cancel`

**⚠️ Règle importante:** Les patients ne peuvent annuler un rendez-vous que **24 heures à l'avance minimum**. Les médecins peuvent annuler à tout moment.

**Réponse en cas d'annulation trop tardive (patient):**
```json
{
  "status": 400,
  "message": "Les rendez-vous ne peuvent être annulés que 24 heures à l'avance. Il ne reste que 12 heures avant votre rendez-vous.",
  "errors": null,
  "timestamp": "2024-12-25T17:00:00"
}
```

### 8. Terminer un rendez-vous avec notes

**PUT** `/api/appointments/{id}/complete?notes=Consultation%20normale`

---

## 🧑‍⚕️ Endpoints pour les Patients

### 1. Lister tous les médecins

**GET** `/api/doctors`

**Réponse:**
```json
[
  {
    "id": 1,
    "username": "dr.martin",
    "fullName": null,
    "specialty": "CARDIOLOGIE",
    "specialtyDisplay": "Cardiologie"
  }
]
```

### 2. Rechercher des médecins par spécialité

**GET** `/api/doctors/specialty/CARDIOLOGIE`

### 3. Voir les détails d'un médecin

**GET** `/api/doctors/{id}`

### 4. Consulter les créneaux disponibles

**GET** `/api/doctors/{id}/available-slots?date=2024-12-30`

**Réponse:**
```json
[
  {
    "dateTime": "2024-12-30T09:00:00",
    "available": true,
    "displayTime": "09:00"
  },
  {
    "dateTime": "2024-12-30T09:30:00",
    "available": false,
    "displayTime": "09:30"
  }
]
```

### 5. Prendre un rendez-vous

**POST** `/api/appointments`

```json
{
  "doctorId": 1,
  "appointmentDateTime": "2024-12-30T10:00:00",
  "reason": "Consultation de routine"
}
```

### 6. Voir mes rendez-vous

**GET** `/api/appointments/my-appointments`

**GET** `/api/appointments/upcoming`

### 7. Annuler un rendez-vous

**PUT** `/api/appointments/{id}/cancel`

**⚠️ Règle importante:** Les patients ne peuvent annuler un rendez-vous que **24 heures à l'avance minimum**. Les médecins peuvent annuler à tout moment.

---

## 🔄 Flux de Travail Typique

### Scénario 1: Patient prend un rendez-vous

1. **Patient s'inscrit**
   ```
   POST /api/auth/register
   {
     "username": "patient1",
     "password": "pass123",
     "userType": "PATIENT",
     "fullName": "Marie Dubois",
     "phoneNumber": "+33612345678",
     "dateOfBirth": "1985-03-20"
   }
   ```

2. **Patient se connecte**
   ```
   POST /api/auth/login
   {
     "username": "patient1",
     "password": "pass123"
   }
   ```

3. **Patient recherche un cardiologue**
   ```
   GET /api/doctors/specialty/CARDIOLOGIE
   ```

4. **Patient consulte les créneaux disponibles**
   ```
   GET /api/doctors/1/available-slots?date=2024-12-30
   ```

5. **Patient prend rendez-vous**
   ```
   POST /api/appointments
   {
     "doctorId": 1,
     "appointmentDateTime": "2024-12-30T10:00:00",
     "reason": "Douleurs thoraciques"
   }
   ```

### Scénario 2: Médecin gère ses rendez-vous

1. **Médecin s'inscrit**
   ```
   POST /api/auth/register
   {
     "username": "dr.martin",
     "password": "pass123",
     "userType": "DOCTOR",
     "specialty": "CARDIOLOGIE"
   }
   ```

2. **Médecin définit ses disponibilités**
   ```
   POST /api/doctors/my-availability
   {
     "dayOfWeek": "MONDAY",
     "startTime": "09:00:00",
     "endTime": "17:00:00",
     "slotDurationMinutes": 30
   }
   ```

3. **Médecin consulte ses rendez-vous en attente**
   ```
   GET /api/appointments/upcoming
   ```

4. **Médecin confirme un rendez-vous**
   ```
   PUT /api/appointments/1/confirm
   ```

5. **Après consultation, médecin termine le rendez-vous**
   ```
   PUT /api/appointments/1/complete?notes=Patient%20en%20bonne%20santé
   ```

---

## 📋 Statuts des Rendez-vous

| Statut | Description | Qui peut le définir |
|--------|-------------|---------------------|
| **PENDING** | En attente de confirmation | Automatique à la création |
| **CONFIRMED** | Confirmé par le médecin | Médecin uniquement |
| **CANCELLED** | Annulé | Patient ou Médecin |
| **COMPLETED** | Terminé avec notes | Médecin uniquement |

---

## 🔒 Sécurité et Règles Métier

### Règles d'Accès
- Tous les endpoints nécessitent une authentification JWT
- Les patients ne peuvent voir que leurs propres rendez-vous
- Les médecins ne peuvent voir que leurs propres rendez-vous
- Seul le médecin concerné peut confirmer ou terminer un rendez-vous
- Patient et médecin peuvent annuler un rendez-vous

### Règles d'Annulation
- **⚠️ Rendez-vous PENDING:** Les patients doivent annuler au moins **24 heures à l'avance**
- **⚠️ Rendez-vous CONFIRMED:** Les patients doivent annuler au moins **1 jour ouvrable à l'avance** (du lundi au vendredi, hors jours fériés)
- **✅ Médecins:** Peuvent annuler à tout moment sans restriction

### Notifications Automatiques
- **📧 Confirmation:** Email envoyé immédiatement après la prise de rendez-vous
- **📧 Rappel 24h:** Email de rappel envoyé automatiquement 24 heures avant le rendez-vous
- **📧 Changement de statut:** Email envoyé lors de la confirmation, annulation ou complétion
- **🤖 Auto-complétion:** Les rendez-vous confirmés passés sont automatiquement marqués comme terminés

---

## 🧪 Tests

Les tests existants ont été mis à jour pour inclure le champ `userType` obligatoire.

Pour exécuter les tests:
```bash
mvn test
```

---

## 📚 Documentation Swagger

Accédez à la documentation interactive:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

Les nouveaux tags Swagger:
- 📅 **Rendez-vous** - Gestion des rendez-vous médicaux
- 👨‍⚕️ **Médecins** - Gestion des médecins et disponibilités

---

## 🚀 Prochaines Étapes Possibles

1. **Notifications**
   - Email de confirmation de rendez-vous
   - Rappels automatiques 24h avant

2. **Historique médical**
   - Lier les rendez-vous aux dossiers patients
   - Historique des consultations

3. **Paiements**
   - Intégration de paiement en ligne
   - Gestion des tarifs par spécialité

4. **Statistiques**
   - Dashboard pour les médecins
   - Taux de présence des patients

5. **Calendrier visuel**
   - Interface calendrier pour les médecins
   - Vue mensuelle des disponibilités

---

## 📞 Support

Pour toute question ou problème, consultez:
- README.md - Documentation générale
- QUICK_START.md - Guide de démarrage rapide
- Swagger UI - Documentation API interactive
