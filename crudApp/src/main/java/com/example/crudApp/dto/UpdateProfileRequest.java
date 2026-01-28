package com.example.crudApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
@Schema(description = "Requête de mise à jour du profil utilisateur")
public class UpdateProfileRequest {

    @Schema(description = "Nom complet", example = "Jean Dupont")
    private String fullName;

    @Schema(description = "Numéro de téléphone", example = "+33612345678")
    private String phoneNumber;

    @Schema(description = "Adresse", example = "123 Rue de la Santé, 75013 Paris")
    private String address;

    @Past(message = "La date de naissance doit être dans le passé")
    @Schema(description = "Date de naissance", example = "1990-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "URL de la photo de profil", example = "https://example.com/photo.jpg")
    private String profilePhoto;
}
