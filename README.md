🏥 Wi-MédicalE — Medical Management Application (Demo)

🚨 Demo & Portfolio Version
This repository is intentionally shared for technical demonstration and evaluation purposes.

🎯 Project Overview

Wi-MédicalE is a full-stack medical management application built with Java 17 / Spring Boot and React.
It demonstrates a production-oriented architecture, focusing on security, scalability, monitoring, and clean code practices.

The project covers real-world concerns such as authentication, role-based access control, observability, CI/CD, and containerization.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.12-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)](https://www.docker.com/)
[![Prometheus](https://img.shields.io/badge/Prometheus-Monitoring-E6522C.svg)](https://prometheus.io/)
[![Grafana](https://img.shields.io/badge/Grafana-Visualization-F46800.svg)](https://grafana.com/)
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Technologies](#-technologies)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Getting Started](#-getting-started)
- [Tests](#-tests)
- [API Documentation](#-api-documentation)
- [Monitoring](#-monitoring)
- [Deployment](#-deployment)
- [CI/CD](#-cicd)
- [Security](#-security)
- [OAuth2 Setup](#-oauth2-setup)
- [Contributing](#-contributing)

---

## 🎯 Features

### User Management
- ✅ Registration and authentication with JWT
- ✅ OAuth2 social login (Google)
- ✅ Role management (PATIENT, DOCTOR, ADMIN)
- ✅ Refresh tokens for extended sessions
- ✅ Custom user profiles with photos
- ✅ Real-time profile editing
- ✅ Profile photo upload and cropping
- ✅ International phone input with country flags (react-phone-input-2)
- ✅ Automatic name capitalization
- ✅ Automatic phone number formatting

### Patient Management
- ✅ Complete CRUD operations for patients
- ✅ Advanced search and filtering
- ✅ Excel and PDF export
- ✅ Medical history tracking

### Appointment Management
- ✅ Appointment scheduling
- ✅ Doctor availability management
- ✅ Automatic email reminders
- ✅ Business days calculation

### User Interface (React Frontend)
- ✅ Role-based customized dashboard (Doctor/Patient)
- ✅ Profile page with information display and editing
- ✅ Profile photo in navbar (Facebook-style 36x36px)
- ✅ Custom backgrounds with images (Wi-MédicalE, medical equipment)
- ✅ Glassmorphism effects on forms
- ✅ Hover animations on feature cards
- ✅ Responsive design with Material-UI
- ✅ State management with Redux Toolkit
- ✅ OAuth2 login button (Google)

### Technical Features
- ✅ JWT authentication with refresh tokens
- ✅ OAuth2 client integration (Google)
- ✅ Data validation with Bean Validation
- ✅ Centralized exception handling
- ✅ File upload (profile photos)
- ✅ Static file server
- ✅ Asynchronous email sending
- ✅ Monitoring with Prometheus and Grafana
- ✅ API documentation with Swagger/OpenAPI
- ✅ Unit and integration tests (62 tests)
- ✅ CI/CD pipeline with GitHub Actions
- ✅ Docker deployment

---

## 🏗️ Architecture

```
crudApp/
├── crudApp/                     # Backend Spring Boot
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/crudApp/
│   │   │   │   ├── config/          # Configuration (Security, JWT, WebConfig)
│   │   │   │   ├── controller/      # REST Controllers
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   ├── exception/       # Gestion des exceptions
│   │   │   │   ├── model/           # Entités JPA
│   │   │   │   ├── repository/      # Repositories Spring Data
│   │   │   │   ├── security/        # JWT, UserDetails
│   │   │   │   ├── service/         # Logique métier
│   │   │   │   ├── specification/   # Spécifications JPA
│   │   │   │   └── util/            # Utilitaires
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       ├── application-docker.properties
│   │   │       └── application-prod.properties
│   │   └── test/                    # Tests unitaires et d'intégration
│   ├── uploads/                     # Fichiers uploadés (photos)
│   ├── docker-compose.yml           # Configuration Docker
│   ├── Dockerfile                   # Image Docker de l'application
│   ├── prometheus.yml               # Configuration Prometheus
│   ├── grafana-dashboard.json       # Dashboard Grafana
│   └── pom.xml                      # Dépendances Maven
│
├── crudapp-frontend/            # Frontend React
│   ├── public/
│   │   └── images/                  # Images (wi-medicale.png, medical-bg.jpg)
│   ├── src/
│   │   ├── api/                     # Services API (authService, etc.)
│   │   ├── components/              # Composants React
│   │   │   └── auth/                # Composants d'authentification
│   │   ├── pages/                   # Pages (Login, Register, Dashboard, Profile, Home)
│   │   ├── store/                   # Redux store
│   │   │   └── slices/              # Redux slices (authSlice)
│   │   ├── App.js                   # Composant principal
│   │   └── index.js                 # Point d'entrée
│   ├── .env.development             # Variables d'environnement (dev)
│   ├── .env.production              # Variables d'environnement (prod)
│   └── package.json                 # Dépendances npm
│
├── .github/workflows/           # CI/CD GitHub Actions
└── README.md                    # Documentation
```

---

## 🛠️ Technologies

### Backend
- **Java 17** - Programming language
- **Spring Boot 3.4.12** - Main framework
- **Spring Security 6** - Authentication and authorization
- **Spring OAuth2 Client** - OAuth2 social login
- **Spring Data JPA** - Data access
- **Hibernate** - ORM

### Frontend
- **React 18** - UI library
- **Redux Toolkit** - State management
- **Material-UI (MUI)** - UI components
- **React Router** - Navigation
- **Axios** - HTTP client
- **react-phone-input-2** - International phone input
- **react-image-crop** - Image cropping
- **react-hook-form** - Form management

### Database
- **MySQL 8.0** - Relational database
- **H2** - In-memory database for tests

### Security
- **JWT (JSON Web Tokens)** - Stateless authentication
- **OAuth2** - Social login (Google)
- **BCrypt** - Password hashing

### Monitoring
- **Prometheus** - Metrics collection
- **Grafana** - Metrics visualization
- **Spring Boot Actuator** - Monitoring endpoints
- **Micrometer** - Application metrics

### Documentation
- **Swagger/OpenAPI 3** - Interactive API documentation

### Email
- **JavaMailSender** - Email sending
- **MailHog** - SMTP test server

### Export
- **Apache POI** - Excel export
- **iText** - PDF export

### Tests
- **JUnit 5** - Testing framework
- **Mockito** - Mocking
- **Spring Boot Test** - Integration tests
- **MockMvc** - Controller tests

### DevOps
- **Docker & Docker Compose** - Containerization
- **GitHub Actions** - CI/CD
- **SonarCloud** - Code analysis
- **Trivy** - Security scanning

---

## 📦 Prerequisites

### For Local Development

#### Backend
- **Java 17** or higher
- **Maven 3.8+**
- **MySQL 8.0** (or use Docker)
- **Git**

#### Frontend
- **Node.js 16+** or higher
- **npm 8+** or **yarn**
- **Git**

### For Docker
- **Docker 20.10+**
- **Docker Compose 2.0+**

---

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/crudApp.git
cd crudApp
```

### 2. Frontend Installation

```bash
cd crudapp-frontend
npm install
# or
yarn install
```

### 3. Database Configuration

#### Option A: Local MySQL

Create a MySQL database:

```sql
CREATE DATABASE crudapp CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'crudapp_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON crudapp.* TO 'crudapp_user'@'localhost';
FLUSH PRIVILEGES;
```

Modify `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/crudapp
spring.datasource.username=crudapp_user
spring.datasource.password=your_password
```

#### Option B: Docker (Recommended)

Use Docker Compose (see [Getting Started with Docker](#getting-started-with-docker) section)

### 4. Install Backend Dependencies

```bash
cd crudApp
mvn clean install
```

---

## ⚙️ Configuration

### Environment Variables

Create a `.env` file at the project root:

```env
# Database
MYSQL_ROOT_PASSWORD=root
MYSQL_DATABASE=crudapp
MYSQL_USER=crudapp_user
MYSQL_PASSWORD=your_password

# JWT
JWT_SECRET=your_very_long_and_secure_jwt_secret
JWT_EXPIRATION_MS=18000000

# OAuth2 (Google)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# Email (MailHog for dev)
MAIL_HOST=mailhog
MAIL_PORT=1025

# Docker Hub (for CI/CD)
DOCKER_USERNAME=your_username
DOCKER_PASSWORD=your_token

# SonarCloud (optional)
SONAR_TOKEN=your_sonarcloud_token
```

### Spring Profiles

The application supports multiple profiles:

- **default** - Local development
- **docker** - Docker deployment
- **prod** - Production
- **test** - Automated tests

---

## 🎬 Getting Started

### Local Startup (without Docker)

#### Backend

```bash
cd crudApp
# Compile and start
mvn spring-boot:run

# Or with a specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=docker
```

The backend will be accessible at **http://localhost:8080**

#### Frontend

```bash
cd crudapp-frontend
# Start development server
npm start
# or
yarn start
```

The frontend will be accessible at **http://localhost:3000**

### Getting Started with Docker

```bash
# Start all services
docker-compose up -d

# Check logs
docker logs crudapp-backend -f

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Available Services

| Service | URL | Credentials |
|---------|-----|-------------|
| **React Frontend** | http://localhost:3000 | - |
| **Backend API** | http://localhost:8080 | - |
| **Swagger UI** | http://localhost:8080/swagger-ui.html | - |
| **Actuator** | http://localhost:8080/actuator | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin/admin |
| **MailHog** | http://localhost:8025 | - |
| **MySQL** | localhost:3307 | root/root |

---

## 🧪 Tests

### Run All Tests

```bash
mvn clean test
```

### Run with Test Profile

```bash
mvn clean test "-Dspring.profiles.active=test"
```

### Run a Specific Test

```bash
mvn test -Dtest=AuthControllerTest
```

### Code Coverage

```bash
mvn clean verify
```

### Test Results

- **62 tests** in total
- Unit and integration tests
- Coverage of controllers, services, and utilities
- H2 in-memory database for tests

---

## 📚 API Documentation

### Swagger UI

Access the interactive API documentation:

**http://localhost:8080/swagger-ui.html**

### Main Endpoints

#### Authentication

```http
POST /api/auth/register - User registration
POST /api/auth/login - User login
POST /api/auth/refresh - Refresh token
POST /api/auth/logout - User logout
PUT  /api/auth/profile - Update user profile
```

#### OAuth2

```http
GET  /oauth2/authorization/google - Login with Google
GET  /login/oauth2/code/google - OAuth2 callback (handled by Spring Security)
```

#### File Upload

```http
POST /api/files/upload-profile-photo - Upload profile photo
GET  /uploads/profile-photos/{filename} - Retrieve a photo
```

#### Patients

```http
GET    /api/patients - List patients
GET    /api/patients/{id} - Get patient details
POST   /api/patients - Create a patient
PUT    /api/patients/{id} - Update a patient
DELETE /api/patients/{id} - Delete a patient
GET    /api/patients/search - Search patients
GET    /api/patients/export/excel - Export to Excel
GET    /api/patients/export/pdf - Export to PDF
```

#### Appointments

```http
GET    /api/appointments - List appointments
GET    /api/appointments/{id} - Get appointment details
POST   /api/appointments - Create an appointment
PUT    /api/appointments/{id} - Update an appointment
DELETE /api/appointments/{id} - Delete an appointment
```

#### Doctors

```http
GET    /api/doctors - List doctors
GET    /api/doctors/{id}/availability - Get availability
POST   /api/doctors/my-availability - Set availability (DOCTOR role)
```

### JWT Authentication

Include the JWT token in your request headers:

```http
Authorization: Bearer <your_jwt_token>
```

---

## 📊 Monitoring

### Prometheus

Access Prometheus: **http://localhost:9090**

#### Available Metrics

- `http_server_requests_seconds_count` - Number of HTTP requests
- `http_server_requests_seconds_sum` - Request duration
- `jvm_memory_used_bytes` - JVM memory usage
- `hikaricp_connections_active` - Active DB connections
- `system_cpu_usage` - CPU usage

#### PromQL Query Examples

```promql
# Request rate per second
rate(http_server_requests_seconds_count{application="crudApp"}[5m])

# 95th percentile response time
histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application="crudApp"}[5m]))

# Heap memory usage
jvm_memory_used_bytes{application="crudApp", area="heap"}
```

### Grafana

Access Grafana: **http://localhost:3000** (admin/admin)

#### Initial Configuration

1. Add Prometheus as a data source:
   - URL: `http://prometheus:9090`
2. Import the pre-configured dashboard:
   - File: `grafana-dashboard.json`

#### Available Dashboards

- HTTP Request Rate
- HTTP Request Duration
- JVM Memory Usage
- CPU Usage
- Database Connection Pool
- HTTP Status Codes
- Garbage Collection
- Thread Count

### Test Script

Quickly test all services:

```powershell
.\scripts\test-prometheus.ps1
```

### Complete Documentation

Consult the detailed guides:
- **[PROMETHEUS_GUIDE.md](./PROMETHEUS_GUIDE.md)** - Complete guide
- **[PROMETHEUS_INTEGRATION_SUMMARY.md](./PROMETHEUS_INTEGRATION_SUMMARY.md)** - Quick summary

---

## 🐳 Deployment

### Build Docker Image

```bash
# Local build
docker build -t crudapp-medical:latest .

# Build with Docker Compose
docker-compose build
```

### Push to Docker Hub

```bash
docker tag crudapp-medical:latest your-username/crudapp-medical:latest
docker push your-username/crudapp-medical:latest
```

### Production Deployment

```bash
# On production server
cd /opt/crudapp
docker compose pull
docker compose up -d --remove-orphans
```

### Production Environment Variables

Make sure to set:

```env
SPRING_PROFILES_ACTIVE=prod
JWT_SECRET=<secure_production_secret>
MYSQL_ROOT_PASSWORD=<strong_password>
GOOGLE_CLIENT_ID=<production_google_client_id>
GOOGLE_CLIENT_SECRET=<production_google_client_secret>
MAIL_HOST=<production_smtp_server>
MAIL_USERNAME=<smtp_username>
MAIL_PASSWORD=<smtp_password>
```

---

## 🔄 CI/CD

### GitHub Actions

The CI/CD pipeline runs automatically on:
- Push to `main` or `develop`
- Pull requests

### Pipeline Jobs

1. **Build and Test** - Compilation and tests
2. **Code Quality** - SonarCloud analysis
3. **Security Scan** - Trivy scan
4. **Docker Build** - Docker image build
5. **Deploy Production** - Automatic deployment (main only)

### GitHub Secrets Configuration

Add these secrets in **Settings → Secrets and variables → Actions**:

```
SONAR_TOKEN - SonarCloud token
DOCKER_USERNAME - Docker Hub username
DOCKER_PASSWORD - Docker Hub token
PRODUCTION_HOST - Production server IP
PRODUCTION_USERNAME - SSH username
PRODUCTION_SSH_KEY - Private SSH key
```

### Status Badges

Add these badges to your README:

```markdown
![Build](https://github.com/your-username/crudApp/workflows/CI-CD/badge.svg)
![Tests](https://img.shields.io/badge/tests-62%20passed-success)
![Coverage](https://img.shields.io/badge/coverage-85%25-green)
```

---

## 🔒 Security

### Authentication

- **JWT** with access and refresh tokens
- **OAuth2** social login (Google)
- **BCrypt** for password hashing
- **Stateless sessions** (no session cookies)

### Authorization

- **Roles**: PATIENT, DOCTOR, ADMIN
- **Role-protected endpoints**
- **Method security** with `@PreAuthorize`

### Best Practices

- ✅ Input validation with Bean Validation
- ✅ Centralized exception handling
- ✅ CSRF protection disabled (stateless REST API)
- ✅ CORS configured
- ✅ HTTP security headers
- ✅ Security scanning with Trivy
- ✅ OAuth2 provider validation

### Public Endpoints

- `/api/auth/**` - Authentication
- `/oauth2/**` - OAuth2 login
- `/v3/api-docs/**` - API documentation
- `/swagger-ui/**` - Swagger interface
- `/actuator/**` - Metrics (secure in production)

---

## 🔐 OAuth2 Setup

### Google OAuth2 Configuration

#### 1. Create a Google Cloud Project

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select an existing one
3. Enable the **Google+ API**

#### 2. Create OAuth 2.0 Credentials

1. Navigate to **APIs & Services → Credentials**
2. Click **Create Credentials → OAuth client ID**
3. Select **Web application**
4. Add authorized redirect URI:
   ```
   http://localhost:8080/login/oauth2/code/google
   ```
5. For production, add:
   ```
   https://yourdomain.com/login/oauth2/code/google
   ```
6. Copy the **Client ID** and **Client Secret**

#### 3. Set Environment Variables

```bash
export GOOGLE_CLIENT_ID="your-google-client-id.apps.googleusercontent.com"
export GOOGLE_CLIENT_SECRET="your-google-client-secret"
```

Or add to your `.env` file:
```env
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret
```

### OAuth2 Flow

1. **User clicks "Continue with Google"** on login page
2. **Frontend redirects to**: `http://localhost:8080/oauth2/authorization/google`
3. **Spring Security redirects** to Google login page
4. **User authenticates** with their Google account
5. **Google redirects back** to: `http://localhost:8080/login/oauth2/code/google`
6. **CustomOAuth2UserService**:
   - Retrieves user information from Google
   - Creates or updates user in database
   - Sets provider field to "google"
7. **OAuth2AuthenticationSuccessHandler**:
   - Generates JWT token
   - Redirects to: `http://localhost:3000/oauth2/redirect?token=...&email=...&name=...&role=...`
8. **OAuth2RedirectHandler (React)**:
   - Extracts token from URL
   - Stores in localStorage
   - Updates Redux state
   - Redirects to dashboard

### Testing OAuth2

1. **Start the backend**:
   ```bash
   mvn spring-boot:run
   ```

2. **Start the frontend**:
   ```bash
   cd crudapp-frontend
   npm start
   ```

3. **Navigate to**: http://localhost:3000/login

4. **Click** "Continue with Google"

5. **Authenticate** with your Google account

6. **You should be redirected** to the dashboard with a valid JWT token

### Troubleshooting

#### Error: "redirect_uri_mismatch"
- Make sure the redirect URI in your Google Cloud Console matches exactly:
  - `http://localhost:8080/login/oauth2/code/google`

#### Error: "Email not found from OAuth2 provider"
- Ensure your Google app has permission to access email
- Check that the user's email is verified on their Google account

#### Error: "Looks like you're signed up with..."
- This occurs when a user tries to login with a different provider than they registered with
- Users must use the same provider they initially registered with

---

## 📖 Additional Documentation

- **[PROMETHEUS_GUIDE.md](./PROMETHEUS_GUIDE.md)** - Complete Prometheus/Grafana guide
- **[PROMETHEUS_INTEGRATION_SUMMARY.md](./PROMETHEUS_INTEGRATION_SUMMARY.md)** - Integration summary
- **[.github/workflows/ci-cd.yml](./.github/workflows/ci-cd.yml)** - CI/CD configuration

---

## 🤝 Contributing

### Contribution Workflow

1. Fork the project
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Standards

- Follow standard Java conventions
- Write tests for new features
- Document public methods
- Use clear commit messages

### Run Checks

```bash
# Tests
mvn clean test

# Code style check
mvn checkstyle:check

# SonarCloud analysis (if configured)
mvn sonar:sonar
```

---

## 📝 License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## 👥 Authors

- **Wilfried Mvomo Eto** - *Initial development* - [YourGitHub](https://github.com/mvom)

---

## 🙏 Acknowledgments

- Spring Boot Team
- Prometheus & Grafana Communities
- Google OAuth2 Team
- All open-source contributors

---

## 📞 Support

For any questions or issues:

- 📧 Email: support@wi-medicale.com
- 🐛 Issues: [GitHub Issues](https://github.com/mvom/Wi-MedicalE/issues)
- 📖 Documentation: [Wiki](https://github.com/mvom/Wi-MedicalE/wiki)

---

## 🗺️ Roadmap

### Version 1.0 (Current) ✅
- ✅ Complete JWT authentication
- ✅ OAuth2 social login (Google)
- ✅ User profile management with photos
- ✅ Role-based customized dashboard
- ✅ Modern React interface with Material-UI
- ✅ Photo upload and cropping
- ✅ International phone input
- ✅ Custom backgrounds (Wi-MédicalE)
- ✅ Prometheus/Grafana monitoring

### Version 1.1 (Coming Soon)
- [ ] Complete appointment management (frontend)
- [ ] Interactive calendar for doctors
- [ ] Real-time push notifications
- [ ] Real-time doctor-patient chat
- [ ] Detailed medical history
- [ ] Medical report export
- [ ] Additional OAuth2 providers (Facebook, GitHub, LinkedIn)

### Version 1.2
- [ ] Video teleconsultation
- [ ] Mobile application (React Native)
- [ ] AI for diagnosis suggestions
- [ ] Integration with external health systems
- [ ] Multi-tenancy for multiple clinics
- [ ] Advanced analytics dashboard

---

**Made with ❤️ by the Wi-MédicalE team - Connected Healthcare**
