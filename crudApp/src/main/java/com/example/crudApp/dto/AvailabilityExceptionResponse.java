package com.example.crudApp.dto;

import com.example.crudApp.model.DoctorAvailabilityException;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Réponse contenant les informations d'une exception de disponibilité")
public class AvailabilityExceptionResponse {

    @Schema(description = "ID de l'exception")
    private Long id;

    @Schema(description = "ID du médecin")
    private Long doctorId;

    @Schema(description = "Nom d'utilisateur du médecin")
    private String doctorUsername;

    @Schema(description = "Email du médecin")
    private String doctorEmail;

    @Schema(description = "Date de l'exception")
    private LocalDate exceptionDate;

    @Schema(description = "Raison de l'exception")
    private String reason;

    @Schema(description = "Indique si le médecin est disponible ce jour-là")
    private Boolean isAvailable;

    @Schema(description = "Heure de début spécifique")
    private String startTime;

    @Schema(description = "Heure de fin spécifique")
    private String endTime;

    @Schema(description = "Indique si l'exception est active")
    private Boolean isActive;

    @Schema(description = "Date de création")
    private LocalDate createdAt;

    public static AvailabilityExceptionResponse fromException(DoctorAvailabilityException exception) {
        return AvailabilityExceptionResponse.builder()
                .id(exception.getId())
                .doctorId(exception.getDoctor() != null ? exception.getDoctor().getId() : null)
                .doctorUsername(exception.getDoctor() != null ? exception.getDoctor().getUsername() : null)
                .doctorEmail(exception.getDoctor() != null ? exception.getDoctor().getEmail() : null)
                .exceptionDate(exception.getExceptionDate())
                .reason(exception.getReason())
                .isAvailable(exception.getIsAvailable())
                .startTime(exception.getStartTime())
                .endTime(exception.getEndTime())
                .isActive(exception.getIsActive())
                .createdAt(exception.getCreatedAt())
                .build();
    }
}
