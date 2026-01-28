package com.example.crudApp.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration OpenAPI/Swagger pour la documentation interactive de l'API
 *
 * Cette configuration génère automatiquement une documentation Swagger UI accessible à :
 * - Swagger UI : http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON : http://localhost:8080/v3/api-docs
 *
 * @author CrudApp Medical Team
 * @version 1.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    /**
     * Configure l'API OpenAPI avec les informations de l'application médicale
     *
     * @return Configuration OpenAPI complète
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Définir le schéma de sécurité JWT
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("🏥 API de Gestion Médicale - CrudApp")
                        .version("1.0.0")
                        .description("""
                                ## API REST complète pour la gestion de patients et de personnes

                                Cette API offre les fonctionnalités suivantes :

                                ### 🔐 Authentification & Sécurité
                                - Authentification JWT avec access tokens (5h) et refresh tokens (7 jours)
                                - Gestion des rôles (USER, ADMIN)
                                - Endpoints sécurisés avec autorisation basée sur les rôles

                                ### 👥 Gestion des Personnes
                                - CRUD complet (Create, Read, Update, Delete)
                                - Pagination et tri
                                - Recherche multi-critères
                                - Export de données (CSV, Excel, PDF)

                                ### 🏥 Gestion des Patients
                                - Informations médicales complètes (groupe sanguin, allergies, historique)
                                - Contacts d'urgence et assurance maladie
                                - Recherche avancée (par groupe sanguin, allergie, date de visite)
                                - Suivi médical (patients nécessitant un suivi)
                                - Archivage et réactivation
                                - Export professionnel avec mention "Document confidentiel"

                                ### 👨‍⚕️ Gestion Administrative
                                - Gestion des utilisateurs (médecins)
                                - Statistiques système
                                - Mise à jour des rôles

                                ### 📊 Fonctionnalités Avancées
                                - Pagination configurable
                                - Tri multi-colonnes
                                - Filtres dynamiques
                                - Validation des données
                                - Gestion des erreurs standardisée

                                ---

                                **Note** : Pour utiliser les endpoints protégés, vous devez d'abord vous authentifier
                                via `/api/auth/login` ou `/api/auth/register`, puis utiliser le token JWT reçu
                                en cliquant sur le bouton "Authorize" 🔒 en haut de cette page.
                                """)
                        .contact(new Contact()
                                .name("Équipe CrudApp Medical")
                                .email("support@crudapp-medical.com")
                                .url("https://github.com/crudapp-medical"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Serveur de développement local"),
                        new Server()
                                .url("https://api.crudapp-medical.com")
                                .description("Serveur de production (exemple)")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("""
                                                Entrez votre token JWT obtenu via `/api/auth/login` ou `/api/auth/register`

                                                **Format** : Entrez uniquement le token (sans le préfixe "Bearer")

                                                **Exemple** : eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
                                                """)));
    }
}
