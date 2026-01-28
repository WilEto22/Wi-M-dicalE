package com.example.crudApp.controller;

import com.example.crudApp.dto.AvailableSlotResponse;
import com.example.crudApp.dto.AvailabilityExceptionRequest;
import com.example.crudApp.dto.AvailabilityExceptionResponse;
import com.example.crudApp.dto.DoctorResponse;
import com.example.crudApp.exception.ResourceNotFoundException;
import com.example.crudApp.model.DoctorAvailability;
import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import com.example.crudApp.repository.UserRepository;
import com.example.crudApp.service.AppointmentService;
import com.example.crudApp.service.DoctorAvailabilityExceptionService;
import com.example.crudApp.service.DoctorAvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Tag(name = "👨‍⚕️ Médecins", description = "Gestion des médecins et de leurs disponibilités")
public class DoctorController {

    private static final Logger logger = LoggerFactory.getLogger(DoctorController.class);
    private final UserRepository userRepository;
    private final AppointmentService appointmentService;
    private final DoctorAvailabilityService availabilityService;
    private final DoctorAvailabilityExceptionService exceptionService;

    @Operation(
            summary = "Lister tous les médecins",
            description = "Récupère la liste de tous les médecins disponibles dans le système"
    )
    @GetMapping
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        logger.info("Récupération de la liste de tous les médecins");

        List<User> doctors = userRepository.findAll().stream()
                .filter(user -> user.getUserType() == UserType.DOCTOR)
                .collect(Collectors.toList());

        List<DoctorResponse> responses = doctors.stream()
                .map(DoctorResponse::fromUser)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Rechercher des médecins par spécialité",
            description = "Récupère la liste des médecins d'une spécialité donnée"
    )
    @GetMapping("/specialty/{specialty}")
    public ResponseEntity<List<DoctorResponse>> getDoctorsBySpecialty(
            @Parameter(description = "Spécialité médicale") @PathVariable MedicalSpecialty specialty) {
        logger.info("Recherche de médecins avec la spécialité: {}", specialty);

        List<User> doctors = userRepository.findAll().stream()
                .filter(user -> user.getUserType() == UserType.DOCTOR && user.getSpecialty() == specialty)
                .collect(Collectors.toList());

        List<DoctorResponse> responses = doctors.stream()
                .map(DoctorResponse::fromUser)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Obtenir les détails d'un médecin",
            description = "Récupère les informations détaillées d'un médecin spécifique"
    )
    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> getDoctorById(
            @Parameter(description = "ID du médecin") @PathVariable Long id) {
        logger.info("Récupération des détails du médecin ID: {}", id);

        User doctor = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé avec l'ID: " + id));

        if (doctor.getUserType() != UserType.DOCTOR) {
            throw new IllegalArgumentException("L'utilisateur spécifié n'est pas un médecin");
        }

        return ResponseEntity.ok(DoctorResponse.fromUser(doctor));
    }

    @Operation(
            summary = "Obtenir les créneaux disponibles d'un médecin",
            description = "Récupère les créneaux horaires disponibles pour un médecin à une date donnée"
    )
    @GetMapping("/{id}/available-slots")
    public ResponseEntity<List<AvailableSlotResponse>> getAvailableSlots(
            @Parameter(description = "ID du médecin") @PathVariable Long id,
            @Parameter(description = "Date (format: yyyy-MM-dd)")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        logger.info("Récupération des créneaux disponibles pour le médecin ID: {} à la date: {}", id, date);

        List<AvailableSlotResponse> slots = appointmentService.getAvailableSlots(id, date);
        return ResponseEntity.ok(slots);
    }

    @Operation(
            summary = "Obtenir les disponibilités d'un médecin",
            description = "Récupère les disponibilités actives d'un médecin spécifique"
    )
    @GetMapping("/{id}/availability")
    public ResponseEntity<List<DoctorAvailability>> getDoctorAvailabilities(
            @Parameter(description = "ID du médecin") @PathVariable Long id) {
        logger.info("Récupération des disponibilités du médecin ID: {}", id);

        List<DoctorAvailability> availabilities = availabilityService.getActiveDoctorAvailabilities(id);
        return ResponseEntity.ok(availabilities);
    }
}
