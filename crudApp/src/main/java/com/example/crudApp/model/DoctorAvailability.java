package com.example.crudApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * Entité représentant les disponibilités d'un médecin
 * Définit les horaires de travail par jour de la semaine
 */
@Entity
@Table(name = "doctor_availability")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Médecin concerné
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    // Jour de la semaine
    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    // Heure de début
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    // Heure de fin
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    // Durée d'un rendez-vous en minutes (par défaut 30 minutes)
    @Column(name = "slot_duration_minutes", nullable = false)
    @Builder.Default
    private Integer slotDurationMinutes = 30;

    // Indique si cette disponibilité est active
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}
