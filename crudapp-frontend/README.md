# 🎨 Wi-M-dicalE - Frontend

Application React pour la gestion médicale avec authentification JWT, gestion des patients et rendez-vous.

## 🚀 Technologies

- **React 18** - Bibliothèque UI
- **Vite** - Build tool ultra-rapide
- **Redux Toolkit** - State management
- **React Router** - Routing
- **Material-UI (MUI)** - Composants UI
- **Axios** - Client HTTP
- **React Hook Form** - Gestion des formulaires
- **Yup** - Validation de schémas
- **date-fns** - Manipulation de dates
- **React Toastify** - Notifications

## 📦 Installation

### Prérequis

- Node.js 18+
- npm ou yarn

### Installation des dépendances

```bash
npm install
```

## 🏃 Démarrage

### Mode développement

```bash
npm run dev
```

L'application sera accessible sur **http://localhost:5173**

### Build de production

```bash
npm run build
```

### Prévisualiser le build

```bash
npm run preview
```

## 🐳 Docker

### Build de l'image Docker

```bash
docker build -t crudapp-frontend:latest .
```

### Exécuter le conteneur

```bash
docker run -p 3001:80 crudapp-frontend:latest
```

L'application sera accessible sur **http://localhost:3001**

## 📁 Structure du projet

```
src/
├── api/                    # Services API
│   ├── axios.config.js
│   ├── authService.js
│   ├── patientService.js
│   ├── appointmentService.js
│   └── doctorService.js
├── components/             # Composants réutilisables
│   ├── auth/
│   │   ├── LoginForm.jsx
│   │   ├── RegisterForm.jsx
│   │   └── ProtectedRoute.jsx
│   └── common/
│       ├── Navbar.jsx
│       ├── Sidebar.jsx
│       ├── Footer.jsx
│       ├── Loading.jsx
│       └── ErrorBoundary.jsx
├── pages/                  # Pages
│   ├── Home.jsx
│   ├── Login.jsx
│   ├── Register.jsx
│   ├── Dashboard.jsx
│   ├── Patients.jsx
│   ├── Appointments.jsx
│   ├── Profile.jsx
│   └── NotFound.jsx
├── store/                  # Redux store
│   ├── store.js
│   └── slices/
│       ├── authSlice.js
│       ├── patientSlice.js
│       └── appointmentSlice.js
├── utils/                  # Utilitaires
│   ├── constants.js
│   ├── tokenManager.js
│   └── formatters.js
├── App.jsx                 # Composant principal
├── main.jsx               # Point d'entrée
└── index.css              # Styles globaux
```

## 🔑 Variables d'environnement

Créez un fichier `.env` à la racine :

```env
VITE_API_URL=http://localhost:8080/api
VITE_APP_NAME=Wi-M-dicalE
VITE_APP_VERSION=1.0.0
```

## 🎨 Fonctionnalités

### Authentification
- ✅ Connexion avec JWT
- ✅ Inscription (Patient/Médecin)
- ✅ Refresh token automatique
- ✅ Routes protégées
- ✅ Gestion des rôles

### Dashboard
- ✅ Statistiques en temps réel
- ✅ Patients récents
- ✅ Rendez-vous récents
- ✅ Graphiques

### Gestion des patients (à venir)
- 🔄 Liste paginée
- 🔄 Recherche et filtres
- 🔄 CRUD complet
- 🔄 Export Excel/PDF

### Gestion des rendez-vous (à venir)
- 🔄 Calendrier interactif
- 🔄 Création de rendez-vous
- 🔄 Notifications

## 🧪 Tests

```bash
# Exécuter les tests
npm run test

# Avec couverture
npm run test:coverage
```

## 📝 Scripts disponibles

- `npm run dev` - Démarrer en mode développement
- `npm run build` - Build de production
- `npm run preview` - Prévisualiser le build
- `npm run lint` - Linter le code
- `npm run format` - Formater le code

## 🔗 API Backend

L'application communique avec le backend Spring Boot sur `http://localhost:8080/api`

### Endpoints principaux

- `POST /api/auth/login` - Connexion
- `POST /api/auth/register` - Inscription
- `POST /api/auth/refresh` - Refresh token
- `GET /api/patients` - Liste des patients
- `GET /api/appointments` - Liste des rendez-vous

## 🚢 Déploiement

### Avec Docker Compose

Le frontend est inclus dans le `docker-compose.yml` du projet principal :

```yaml
frontend:
  build:
    context: ./crudapp-frontend
  ports:
    - "3001:80"
  depends_on:
    - app
```

### Build et déploiement

```bash
# Depuis la racine du projet
docker-compose up -d frontend
```

## 🎯 Prochaines étapes

- [ ] Implémenter la gestion complète des patients
- [ ] Ajouter le calendrier des rendez-vous
- [ ] Implémenter les notifications en temps réel
- [ ] Ajouter les tests unitaires
- [ ] Améliorer le responsive design
- [ ] Ajouter le mode sombre
- [ ] Internationalisation (i18n)

## 📚 Documentation

- [React Documentation](https://react.dev/)
- [Vite Documentation](https://vitejs.dev/)
- [Material-UI](https://mui.com/)
- [Redux Toolkit](https://redux-toolkit.js.org/)

## 🤝 Contribution

Les contributions sont les bienvenues ! Consultez le guide de contribution du projet principal.

## 📄 Licence

MIT

---

**Développé avec ❤️ pour Wi-M-dicalE**
