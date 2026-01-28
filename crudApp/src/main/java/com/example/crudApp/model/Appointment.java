package com.example.crudApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entité représentant un rendez-vous médical
 */
@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Médecin concerné par le rendez-vous
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    // Patient concerné par le rendez-vous
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Date et heure du rendez-vous
    @Column(name = "appointment_date_time", nullable = false)
    private LocalDateTime appointmentDateTime;

    // Statut du rendez-vous
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    // Motif de consultation
    @Column(name = "reason", length = 500)
    private String reason;

    // Notes du médecin (après consultation)
    @Column(name = "doctor_notes", length = 2000)
    private String doctorNotes;

    // Date de création du rendez-vous
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Date de dernière modification
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
