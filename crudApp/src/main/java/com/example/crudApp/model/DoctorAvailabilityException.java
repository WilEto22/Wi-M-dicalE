package com.example.crudApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entité représentant les exceptions aux disponibilités d'un médecin
 * Permet de désactiver ou modifier des disponibilités pour des dates spécifiques
 */
@Entity
@Table(name = "doctor_availability_exceptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAvailabilityException {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Médecin concerné
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    // Date de l'exception
    @Column(name = "exception_date", nullable = false)
    private LocalDate exceptionDate;

    // Raison de l'exception (vacances, congé maladie, formation, etc.)
    @Column(name = "reason", length = 255)
    private String reason;

    // Indique si le médecin est disponible ce jour-là
    // false = pas disponible (vacances, congé)
    // true = disponible malgré les horaires récurrents (jour supplémentaire)
    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = false;

    // Heure de début spécifique (optionnel, pour modifier les horaires ce jour-là)
    @Column(name = "start_time")
    private String startTime;

    // Heure de fin spécifique (optionnel, pour modifier les horaires ce jour-là)
    @Column(name = "end_time")
    private String endTime;

    // Indique si cette exception est active
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Date de création
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDate createdAt = LocalDate.now();
}
