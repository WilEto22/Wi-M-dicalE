package com.example.crudApp.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Données pour créer ou modifier un patient")
public class PatientRequest {

    @Schema(description = "Nom complet du patient", example = "Jean Dupont", required = true)
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    private String name;

    @Schema(description = "Adresse email du patient", example = "jean.dupont@example.com", required = true)
    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    private String email;

    @Schema(description = "Âge du patient", example = "45", required = true)
    @NotNull(message = "L'âge est obligatoire")
    @Min(value = 0, message = "L'âge doit être positif")
    @Max(value = 150, message = "L'âge doit être inférieur à 150")
    private Integer age;

    @Schema(description = "Adresse complète du patient", example = "123 Rue de la Santé, 75014 Paris", required = true)
    @NotBlank(message = "L'adresse est obligatoire")
    @Size(max = 255, message = "L'adresse ne doit pas dépasser 255 caractères")
    private String address;

    // Champs médicaux spécifiques
    @Schema(description = "Groupe sanguin", example = "A+", allowableValues = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"})
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Le groupe sanguin doit être valide (A+, A-, B+, B-, AB+, AB-, O+, O-)")
    private String bloodType;

    @Schema(description = "Liste des allergies connues (tableau ou chaîne)", example = "[\"Pénicilline\", \"Arachides\"]")
    private List<String> allergiesList;

    @Schema(description = "Liste des maladies chroniques (tableau ou chaîne)", example = "[\"Hypertension\", \"Diabète\"]")
    private List<String> chronicDiseasesList;

    @Schema(description = "Liste des médicaments (tableau ou chaîne)", example = "[\"Lisinopril 10mg\", \"Metformin 500mg\"]")
    private List<String> medicationsList;

    @Schema(description = "Historique médical du patient", example = "Diabète de type 2 diagnostiqué en 2015, Hypertension")
    @Size(max = 5000, message = "L'historique médical ne doit pas dépasser 5000 caractères")
    private String medicalHistory;

    @Schema(description = "Date de la dernière visite médicale", example = "2024-12-15")
    @PastOrPresent(message = "La date de dernière visite ne peut pas être dans le futur")
    private LocalDate lastVisit;

    @Pattern(regexp = "^[+]?[0-9]{10,20}$", message = "Le numéro de téléphone doit être valide")
    private String phoneNumber;

    @Size(max = 100, message = "Le nom du contact d'urgence ne doit pas dépasser 100 caractères")
    private String emergencyContact;

    @Pattern(regexp = "^[+]?[0-9]{10,20}$", message = "Le téléphone d'urgence doit être valide")
    private String emergencyPhone;

    @Size(max = 50, message = "Le numéro d'assurance ne doit pas dépasser 50 caractères")
    private String insuranceNumber;

    @Schema(description = "Notes additionnelles", example = "Patient à surveiller régulièrement")
    @Size(max = 2000, message = "Les notes ne doivent pas dépasser 2000 caractères")
    private String notes;

    private Boolean isActive = true;

    // Getters pour convertir les listes en chaînes pour le stockage
    public String getAllergies() {
        if (allergiesList != null && !allergiesList.isEmpty()) {
            return String.join(", ", allergiesList);
        }
        return null;
    }

    public String getMedicalHistory() {
        StringBuilder history = new StringBuilder();

        if (chronicDiseasesList != null && !chronicDiseasesList.isEmpty()) {
            history.append("Maladies chroniques: ").append(String.join(", ", chronicDiseasesList));
        }

        if (medicationsList != null && !medicationsList.isEmpty()) {
            if (history.length() > 0) {
                history.append(". ");
            }
            history.append("Médicaments: ").append(String.join(", ", medicationsList));
        }

        if (notes != null && !notes.isBlank()) {
            if (history.length() > 0) {
                history.append(". ");
            }
            history.append("Notes: ").append(notes);
        }

        return history.length() > 0 ? history.toString() : null;
    }

    // Setters pour accepter des chaînes (compatibilité)
    @JsonSetter("allergies")
    public void setAllergiesFromString(String allergies) {
        if (allergies != null && !allergies.isBlank()) {
            this.allergiesList = List.of(allergies.split(",\\s*"));
        }
    }
}
