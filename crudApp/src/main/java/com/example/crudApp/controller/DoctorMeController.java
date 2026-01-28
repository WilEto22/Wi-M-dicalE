package com.example.crudApp.controller;

import com.example.crudApp.dto.AvailableSlotResponse;
import com.example.crudApp.dto.AvailabilityExceptionRequest;
import com.example.crudApp.dto.AvailabilityExceptionResponse;
import com.example.crudApp.dto.DoctorResponse;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.DoctorAvailability;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import com.example.crudApp.repository.UserRepository;
import com.example.crudApp.service.AppointmentService;
import com.example.crudApp.service.DoctorAvailabilityExceptionService;
import com.example.crudApp.service.DoctorAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors/me")
@RequiredArgsConstructor
@Tag(name = "👨‍⚕️ Mon Profil Médecin", description = "Endpoints pour le médecin connecté")
public class DoctorMeController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorMeController.class);
    private final DoctorAvailabilityService availabilityService;
    private final DoctorAvailabilityExceptionService exceptionService;
    private final UserRepository userRepository;

    @Operation(
            summary = "Obtenir mon profil",
            description = "Récupère les informations du médecin connecté"
    )
    @GetMapping
    public ResponseEntity<DoctorResponse> getMyProfile(Authentication authentication) {
        logger.info("Récupération du profil pour: {}", authentication.getName());

        User doctor = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé: " + authentication.getName()));

        if (doctor.getUserType() != UserType.DOCTOR) {
            throw new IllegalArgumentException("L'utilisateur connecté n'est pas un médecin");
        }

        return ResponseEntity.ok(DoctorResponse.fromUser(doctor));
    }

    @Operation(
            summary = "Définir mes disponibilités",
            description = "Permet à un médecin de définir ses horaires de travail"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Disponibilité créée avec succès"),
            @ApiResponse(responseCode = "403", description = "Seuls les médecins peuvent définir des disponibilités")
    })
    @PostMapping("/availability")
    public ResponseEntity<DoctorAvailability> createMyAvailability(
            @Valid @RequestBody DoctorAvailability availability,
            Authentication authentication) {
        logger.info("Création d'une disponibilité par: {}", authentication.getName());

        DoctorAvailability created = availabilityService.createAvailability(availability, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(
            summary = "Obtenir mes disponibilités",
            description = "Récupère toutes les disponibilités du médecin connecté"
    )
    @GetMapping("/availability")
    public ResponseEntity<List<DoctorAvailability>> getMyAvailabilities(Authentication authentication) {
        logger.info("Récupération des disponibilités pour: {}", authentication.getName());

        List<DoctorAvailability> availabilities = availabilityService.getDoctorAvailabilities(authentication.getName());
        return ResponseEntity.ok(availabilities);
    }

    @Operation(
            summary = "Mettre à jour une disponibilité",
            description = "Permet à un médecin de modifier une de ses disponibilités"
    )
    @PutMapping("/availability/{id}")
    public ResponseEntity<DoctorAvailability> updateMyAvailability(
            @PathVariable Long id,
            @Valid @RequestBody DoctorAvailability availability,
            Authentication authentication) {
        logger.info("Mise à jour de la disponibilité ID: {} par: {}", id, authentication.getName());

        DoctorAvailability updated = availabilityService.updateAvailability(id, availability, authentication.getName());
        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Supprimer une disponibilité",
            description = "Permet à un médecin de supprimer une de ses disponibilités"
    )
    @DeleteMapping("/availability/{id}")
    public ResponseEntity<Void> deleteMyAvailability(
            @PathVariable Long id,
            Authentication authentication) {
        logger.info("Suppression de la disponibilité ID: {} par: {}", id, authentication.getName());

        availabilityService.deleteAvailability(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // ==================== GESTION DES EXCEPTIONS ====================

    @Operation(
            summary = "Créer une exception de disponibilité",
            description = "Permet à un médecin de créer une exception pour une date spécifique (vacances, congé, etc.)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Exception créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou exception déjà existante pour cette date"),
            @ApiResponse(responseCode = "403", description = "Seuls les médecins peuvent créer des exceptions")
    })
    @PostMapping("/availability/exceptions")
    public ResponseEntity<AvailabilityExceptionResponse> createException(
            @Valid @RequestBody AvailabilityExceptionRequest request,
            Authentication authentication) {
        logger.info("Création d'une exception par: {} pour la date: {}",
                authentication.getName(), request.getExceptionDate());

        AvailabilityExceptionResponse response = exceptionService.createException(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Obtenir mes exceptions",
            description = "Récupère toutes les exceptions de disponibilité du médecin connecté"
    )
    @GetMapping("/availability/exceptions")
    public ResponseEntity<List<AvailabilityExceptionResponse>> getMyExceptions(Authentication authentication) {
        logger.info("Récupération des exceptions pour: {}", authentication.getName());

        List<AvailabilityExceptionResponse> exceptions = exceptionService.getDoctorExceptions(authentication.getName());
        return ResponseEntity.ok(exceptions);
    }

    @Operation(
            summary = "Obtenir une exception par ID",
            description = "Récupère les détails d'une exception spécifique"
    )
    @GetMapping("/availability/exceptions/{id}")
    public ResponseEntity<AvailabilityExceptionResponse> getExceptionById(
            @PathVariable Long id,
            Authentication authentication) {
        logger.info("Récupération de l'exception ID: {} par: {}", id, authentication.getName());

        AvailabilityExceptionResponse response = exceptionService.getExceptionById(id, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Mettre à jour une exception",
            description = "Permet à un médecin de modifier une de ses exceptions"
    )
    @PutMapping("/availability/exceptions/{id}")
    public ResponseEntity<AvailabilityExceptionResponse> updateException(
            @PathVariable Long id,
            @Valid @RequestBody AvailabilityExceptionRequest request,
            Authentication authentication) {
        logger.info("Mise à jour de l'exception ID: {} par: {}", id, authentication.getName());

        AvailabilityExceptionResponse response = exceptionService.updateException(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Supprimer une exception",
            description = "Permet à un médecin de supprimer (désactiver) une de ses exceptions"
    )
    @DeleteMapping("/availability/exceptions/{id}")
    public ResponseEntity<Void> deleteException(
            @PathVariable Long id,
            Authentication authentication) {
        logger.info("Suppression de l'exception ID: {} par: {}", id, authentication.getName());

        exceptionService.deleteException(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
