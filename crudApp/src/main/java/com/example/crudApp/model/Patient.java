package com.example.crudApp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entité Patient - Représente un patient dans le système médical
 * Cette entité est indépendante de User et stocke des informations médicales détaillées
 */
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String address;

    // Champs médicaux spécifiques
    @Column(name = "blood_type", length = 3)
    private String bloodType;

    @Column(length = 1000)
    private String allergies;

    @Column(name = "medical_history", length = 5000)
    private String medicalHistory;

    @Column(name = "last_visit")
    private LocalDate lastVisit;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Column(name = "emergency_phone", length = 20)
    private String emergencyPhone;

    @Column(name = "insurance_number", unique = true, length = 50)
    private String insuranceNumber;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // Relation avec l'utilisateur (User) pour l'authentification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
