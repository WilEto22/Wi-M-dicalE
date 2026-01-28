package com.example.crudApp.controller;

import com.example.crudApp.dto.AuthRequest;
import com.example.crudApp.dto.AuthResponse;
import com.example.crudApp.dto.RefreshTokenRequest;
import com.example.crudApp.dto.RefreshTokenResponse;
import com.example.crudApp.dto.RegisterRequest;
import com.example.crudApp.dto.UpdateProfileRequest;
import com.example.crudApp.exception.DuplicateResourceException;
import com.example.crudApp.model.RefreshToken;
import com.example.crudApp.security.JwtUtil;
import com.example.crudApp.service.EmailService;
import com.example.crudApp.service.RefreshTokenService;
import com.example.crudApp.service.UserService;
import com.example.crudApp.model.User;
import com.example.crudApp.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import com.example.crudApp.exception.ResourceNotFoundException;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "🔐 Authentification", description = "Endpoints d'authentification et gestion des tokens JWT")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authManager, JwtUtil jwtUtil, UserService userService, RefreshTokenService refreshTokenService, EmailService emailService, UserRepository userRepository) {
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.emailService = emailService;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Connexion",
            description = "Authentifie un utilisateur et retourne un access token (5h) et un refresh token (7 jours)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie",
                    content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides")
    })
    @PostMapping("/login")
    public ResponseEntity<RefreshTokenResponse> login(
            @Parameter(description = "Identifiants de connexion", required = true)
            @Valid @RequestBody AuthRequest req) {
        logger.info("Tentative de connexion pour l'utilisateur: {}", req.getUsername());

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        String accessToken = jwtUtil.generateToken(req.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(req.getUsername());

        // Récupérer les informations de l'utilisateur
        User user = userService.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        logger.info("Connexion réussie pour l'utilisateur: {}", req.getUsername());

        return ResponseEntity.ok(RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .username(user.getUsername())
                .userType(user.getUserType())
                .build());
    }

    @Operation(
            summary = "Inscription",
            description = "Crée un nouveau compte utilisateur (médecin ou patient) et retourne les tokens JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie",
                    content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Nom d'utilisateur déjà utilisé")
    })
    @PostMapping("/register")
    public ResponseEntity<RefreshTokenResponse> register(
            @Parameter(description = "Informations d'inscription de l'utilisateur", required = true)
            @Valid @RequestBody RegisterRequest req) {
        logger.info("Tentative d'inscription pour l'utilisateur: {} - Type: {}",
                req.getUsername(), req.getUserType());

        // Vérifier si l'utilisateur existe déjà
        if (userService.findByUsername(req.getUsername()).isPresent()) {
            logger.warn("Tentative d'inscription avec un nom d'utilisateur existant: {}", req.getUsername());
            throw new DuplicateResourceException("Le nom d'utilisateur '" + req.getUsername() + "' est déjà utilisé");
        }

        // Validation selon le type d'utilisateur
        if (req.getUserType() == com.example.crudApp.model.UserType.DOCTOR) {
            if (req.getSpecialty() == null) {
                throw new IllegalArgumentException("La spécialité médicale est obligatoire pour les médecins");
            }
        } else if (req.getUserType() == com.example.crudApp.model.UserType.PATIENT) {
            if (req.getFullName() == null || req.getFullName().isBlank()) {
                throw new IllegalArgumentException("Le nom complet est obligatoire pour les patients");
            }
            if (req.getPhoneNumber() == null || req.getPhoneNumber().isBlank()) {
                throw new IllegalArgumentException("Le numéro de téléphone est obligatoire pour les patients");
            }
            if (req.getDateOfBirth() == null) {
                throw new IllegalArgumentException("La date de naissance est obligatoire pour les patients");
            }
        }

        User u = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(req.getPassword())
                .roles("ROLE_USER")
                .userType(req.getUserType())
                .specialty(req.getSpecialty())
                .fullName(req.getFullName())
                .phoneNumber(req.getPhoneNumber() != null && !req.getPhoneNumber().isBlank() ? req.getPhoneNumber() : null)
                .address(req.getAddress())
                .dateOfBirth(req.getDateOfBirth())
                .build();
        User saved = userService.saveNewUser(u);

        String accessToken = jwtUtil.generateToken(saved.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshTokenForUser(saved);

        // Envoyer l'email de bienvenue
        emailService.sendWelcomeEmail(saved);

        logger.info("Inscription réussie pour l'utilisateur: {} - Type: {}",
                saved.getUsername(), saved.getUserType());

        return ResponseEntity.ok(RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .username(saved.getUsername())
                .userType(saved.getUserType())
                .build());
    }

    @Operation(
            summary = "Rafraîchir le token",
            description = "Génère un nouveau access token à partir d'un refresh token valide"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token rafraîchi avec succès",
                    content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class))),
            @ApiResponse(responseCode = "403", description = "Refresh token invalide ou expiré")
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @Parameter(description = "Refresh token", required = true)
            @Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        logger.info("Tentative de rafraîchissement du token");

        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken);
        refreshToken = refreshTokenService.verifyExpiration(refreshToken);

        String newAccessToken = jwtUtil.generateToken(refreshToken.getUser().getUsername());

        logger.info("Token rafraîchi avec succès pour l'utilisateur: {}", refreshToken.getUser().getUsername());

        return ResponseEntity.ok(RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(requestRefreshToken)
                .tokenType("Bearer")
                .build());
    }

    @Operation(
            summary = "Déconnexion",
            description = "Supprime le refresh token pour déconnecter l'utilisateur"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "404", description = "Refresh token non trouvé")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @Parameter(description = "Refresh token à supprimer", required = true)
            @Valid @RequestBody RefreshTokenRequest request) {
        logger.info("Tentative de déconnexion");
        refreshTokenService.deleteByToken(request.getRefreshToken());
        logger.info("Déconnexion réussie");
        return ResponseEntity.ok().body("Déconnexion réussie");
    }

    @Operation(
            summary = "Obtenir l'utilisateur actuel",
            description = "Retourne les informations de l'utilisateur connecté"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations utilisateur récupérées"),
            @ApiResponse(responseCode = "401", description = "Non authentifié")
    })
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        logger.info("Récupération des informations pour l'utilisateur: {}", username);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("userType", user.getUserType().toString());
        userInfo.put("roles", user.getRoles());
        userInfo.put("fullName", user.getFullName());
        userInfo.put("phoneNumber", user.getPhoneNumber());
        userInfo.put("address", user.getAddress());
        userInfo.put("dateOfBirth", user.getDateOfBirth());
        userInfo.put("specialty", user.getSpecialty() != null ? user.getSpecialty().toString() : null);
        userInfo.put("profilePhoto", user.getProfilePhoto());

        return ResponseEntity.ok(userInfo);
    }

    @Operation(
            summary = "Mettre à jour le profil utilisateur",
            description = "Permet à un utilisateur connecté de mettre à jour ses informations de profil"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profil mis à jour avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "404", description = "Utilisateur non trouvé")
    })
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @Parameter(description = "Informations de profil à mettre à jour", required = true)
            @Valid @RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        logger.info("Mise à jour du profil pour l'utilisateur: {}", username);

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        // Mettre à jour les champs si fournis
        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName());
        }
        if (request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank()) {
            // Ne mettre à jour que si le numéro est différent
            if (!request.getPhoneNumber().equals(user.getPhoneNumber())) {
                user.setPhoneNumber(request.getPhoneNumber());
            }
        }
        if (request.getAddress() != null && !request.getAddress().isBlank()) {
            user.setAddress(request.getAddress());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getProfilePhoto() != null && !request.getProfilePhoto().isBlank()) {
            user.setProfilePhoto(request.getProfilePhoto());
        }

        User updatedUser = userRepository.save(user);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", updatedUser.getId());
        userInfo.put("username", updatedUser.getUsername());
        userInfo.put("email", updatedUser.getEmail());
        userInfo.put("userType", updatedUser.getUserType().toString());
        userInfo.put("roles", updatedUser.getRoles());
        userInfo.put("fullName", updatedUser.getFullName());
        userInfo.put("phoneNumber", updatedUser.getPhoneNumber());
        userInfo.put("address", updatedUser.getAddress());
        userInfo.put("dateOfBirth", updatedUser.getDateOfBirth());
        userInfo.put("specialty", updatedUser.getSpecialty() != null ? updatedUser.getSpecialty().toString() : null);
        userInfo.put("profilePhoto", updatedUser.getProfilePhoto());

        logger.info("Profil mis à jour avec succès pour l'utilisateur: {}", username);

        return ResponseEntity.ok(userInfo);
    }
}
