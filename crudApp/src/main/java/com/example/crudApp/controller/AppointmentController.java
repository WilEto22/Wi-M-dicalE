package com.example.crudApp.controller;

import com.example.crudApp.dto.AppointmentRequest;
import com.example.crudApp.dto.AppointmentResponse;
import com.example.crudApp.model.Appointment;
import com.example.crudApp.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "📅 Rendez-vous", description = "Gestion des rendez-vous médicaux")
public class AppointmentController {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    private final AppointmentService appointmentService;

    @Operation(
            summary = "Créer un rendez-vous",
            description = "Permet à un patient de prendre un rendez-vous avec un médecin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Rendez-vous créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides ou créneau non disponible"),
            @ApiResponse(responseCode = "404", description = "Médecin non trouvé")
    })
    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(
            @Valid @RequestBody AppointmentRequest request,
            Authentication authentication) {
        logger.info("Création d'un rendez-vous par: {}", authentication.getName());

        Appointment appointment = appointmentService.createAppointment(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AppointmentResponse.fromAppointment(appointment));
    }

    @Operation(
            summary = "Obtenir mes rendez-vous",
            description = "Récupère tous les rendez-vous de l'utilisateur connecté (patient ou médecin)"
    )
    @GetMapping("/my-appointments")
    public ResponseEntity<List<AppointmentResponse>> getMyAppointments(Authentication authentication) {
        logger.info("Récupération des rendez-vous pour: {}", authentication.getName());

        List<Appointment> appointments = appointmentService.getPatientAppointments(authentication.getName());

        // Si l'utilisateur est un médecin, récupérer ses rendez-vous en tant que médecin
        try {
            List<Appointment> doctorAppointments = appointmentService.getDoctorAppointments(authentication.getName());
            appointments.addAll(doctorAppointments);
        } catch (Exception e) {
            // L'utilisateur n'est pas un médecin, ignorer
        }

        List<AppointmentResponse> responses = appointments.stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Obtenir mes prochains rendez-vous",
            description = "Récupère les prochains rendez-vous de l'utilisateur connecté"
    )
    @GetMapping("/upcoming")
    public ResponseEntity<List<AppointmentResponse>> getUpcomingAppointments(Authentication authentication) {
        logger.info("Récupération des prochains rendez-vous pour: {}", authentication.getName());

        List<Appointment> appointments = appointmentService.getUpcomingPatientAppointments(authentication.getName());

        // Si l'utilisateur est un médecin, récupérer ses prochains rendez-vous en tant que médecin
        try {
            List<Appointment> doctorAppointments = appointmentService.getUpcomingDoctorAppointments(authentication.getName());
            appointments.addAll(doctorAppointments);
        } catch (Exception e) {
            // L'utilisateur n'est pas un médecin, ignorer
        }

        List<AppointmentResponse> responses = appointments.stream()
                .map(AppointmentResponse::fromAppointment)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Obtenir un rendez-vous par ID",
            description = "Récupère les détails d'un rendez-vous spécifique"
    )
    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(
            @Parameter(description = "ID du rendez-vous") @PathVariable Long id) {
        logger.info("Récupération du rendez-vous ID: {}", id);

        Appointment appointment = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(AppointmentResponse.fromAppointment(appointment));
    }

    @Operation(
            summary = "Confirmer un rendez-vous",
            description = "Permet à un médecin de confirmer un rendez-vous en attente"
    )
    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirmAppointment(
            @Parameter(description = "ID du rendez-vous") @PathVariable Long id,
            Authentication authentication) {
        logger.info("Confirmation du rendez-vous ID: {} par: {}", id, authentication.getName());

        Appointment appointment = appointmentService.confirmAppointment(id, authentication.getName());
        return ResponseEntity.ok(AppointmentResponse.fromAppointment(appointment));
    }

    @Operation(
            summary = "Annuler un rendez-vous",
            description = "Permet au patient ou au médecin d'annuler un rendez-vous"
    )
    @PutMapping("/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancelAppointment(
            @Parameter(description = "ID du rendez-vous") @PathVariable Long id,
            Authentication authentication) {
        logger.info("Annulation du rendez-vous ID: {} par: {}", id, authentication.getName());

        Appointment appointment = appointmentService.cancelAppointment(id, authentication.getName());
        return ResponseEntity.ok(AppointmentResponse.fromAppointment(appointment));
    }

    @Operation(
            summary = "Terminer un rendez-vous",
            description = "Permet à un médecin de marquer un rendez-vous comme terminé et d'ajouter des notes"
    )
    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> completeAppointment(
            @Parameter(description = "ID du rendez-vous") @PathVariable Long id,
            @Parameter(description = "Notes du médecin") @RequestParam(required = false) String notes,
            Authentication authentication) {
        logger.info("Finalisation du rendez-vous ID: {} par: {}", id, authentication.getName());

        Appointment appointment = appointmentService.completeAppointment(id, authentication.getName(), notes);
        return ResponseEntity.ok(AppointmentResponse.fromAppointment(appointment));
    }
}
