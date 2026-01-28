package com.example.crudApp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    // roles stored as comma separated values, ex: "ROLE_USER,ROLE_ADMIN"
    private String roles;

    // Type d'utilisateur (DOCTOR ou PATIENT)
    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    // Spécialité médicale du médecin (seulement pour les DOCTOR)
    @Enumerated(EnumType.STRING)
    @Column(name = "medical_specialty")
    private MedicalSpecialty specialty;

    // Informations supplémentaires pour les patients
    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone_number", unique = true, length = 20, nullable = true)
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "date_of_birth")
    private java.time.LocalDate dateOfBirth;

    @Column(name = "profile_photo", length = 500)
    private String profilePhoto;

    // OAuth2 fields
    @Column(name = "provider")
    private String provider; // "local", "google", "facebook"

    @Column(name = "provider_id")
    private String providerId; // ID from OAuth2 provider

    @Column(name = "email_verified")
    private Boolean emailVerified = false;
}
