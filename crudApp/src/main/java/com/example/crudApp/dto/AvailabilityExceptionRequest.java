package com.example.crudApp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Données pour créer ou modifier une exception de disponibilité")
public class AvailabilityExceptionRequest {

    @Schema(description = "Date de l'exception (format: yyyy-MM-dd)", example = "2025-01-20", required = true)
    @NotNull(message = "La date est obligatoire")
    @FutureOrPresent(message = "La date doit être aujourd'hui ou dans le futur")
    private LocalDate exceptionDate;

    @Schema(description = "Raison de l'exception", example = "Vacances", required = true)
    @NotBlank(message = "La raison est obligatoire")
    @Size(min = 2, max = 255, message = "La raison doit contenir entre 2 et 255 caractères")
    private String reason;

    @Schema(description = "Indique si le médecin est disponible ce jour-là", example = "false")
    private Boolean isAvailable = false;

    @Schema(description = "Heure de début spécifique (format: HH:mm:ss)", example = "09:00:00")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "L'heure de début doit être au format HH:mm:ss")
    private String startTime;

    @Schema(description = "Heure de fin spécifique (format: HH:mm:ss)", example = "17:00:00")
    @Pattern(regexp = "^([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$", message = "L'heure de fin doit être au format HH:mm:ss")
    private String endTime;
}
