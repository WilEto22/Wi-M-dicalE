package com.example.crudApp.dto;

import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.UserType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Requête d'inscription d'un nouveau utilisateur (médecin ou patient)")
public class RegisterRequest {

    @NotBlank(message = "Le nom d'utilisateur est obligatoire")
    @Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
    @Schema(description = "Nom d'utilisateur", example = "dr.martin")
    private String username;

    @NotBlank(message = "L'email est obligatoire")
    @Schema(description = "Adresse email", example = "dr.martin@hospital.com")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    @Schema(description = "Mot de passe", example = "password123")
    private String password;

    @NotNull(message = "Le type d'utilisateur est obligatoire")
    @Schema(description = "Type d'utilisateur (DOCTOR ou PATIENT)", example = "DOCTOR")
    private UserType userType;

    // Champs pour les médecins uniquement
    @JsonProperty("medicalSpecialty")
    @Schema(description = "Spécialité médicale (obligatoire pour les médecins)", example = "CARDIOLOGIE")
    private MedicalSpecialty specialty;

    // Champs pour les patients uniquement
    @Schema(description = "Nom complet (obligatoire pour les patients)", example = "Jean Dupont")
    private String fullName;

    @Schema(description = "Numéro de téléphone (obligatoire pour les patients)", example = "+33612345678")
    private String phoneNumber;

    @Schema(description = "Adresse", example = "123 Rue de la Santé, 75013 Paris")
    private String address;

    @Past(message = "La date de naissance doit être dans le passé")
    @Schema(description = "Date de naissance (obligatoire pour les patients)", example = "1990-05-15")
    private LocalDate dateOfBirth;
}
