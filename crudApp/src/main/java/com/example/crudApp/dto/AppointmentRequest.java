package com.example.crudApp.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Schema(description = "Requête de création de rendez-vous")
public class AppointmentRequest {

    @NotNull(message = "L'ID du médecin est obligatoire")
    @Schema(description = "ID du médecin", example = "1")
    private Long doctorId;

    @Schema(description = "Date et heure du rendez-vous", example = "2024-12-30T10:00:00")
    private LocalDateTime appointmentDateTime;

    // Champs alternatifs pour accepter date et heure séparément
    @Schema(description = "Date du rendez-vous (format: yyyy-MM-dd)", example = "2025-01-20")
    private LocalDate appointmentDate;

    @Schema(description = "Heure de début (format: HH:mm:ss)", example = "09:00:00")
    private String startTime;

    @Schema(description = "Heure de fin (format: HH:mm:ss)", example = "09:45:00")
    private String endTime;

    @Size(max = 500, message = "Le motif ne peut pas dépasser 500 caractères")
    @Schema(description = "Motif de consultation", example = "Consultation de routine")
    private String reason;

    @Size(max = 2000, message = "Les notes ne peuvent pas dépasser 2000 caractères")
    @Schema(description = "Notes additionnelles", example = "Première visite")
    private String notes;

    // Getter qui combine appointmentDate et startTime si appointmentDateTime n'est pas fourni
    public LocalDateTime getAppointmentDateTime() {
        if (appointmentDateTime != null) {
            return appointmentDateTime;
        }
        if (appointmentDate != null && startTime != null) {
            return LocalDateTime.of(appointmentDate, LocalTime.parse(startTime));
        }
        return null;
    }

    // Setter pour accepter appointmentDate et startTime séparément
    @JsonSetter("appointmentDate")
    public void setAppointmentDate(LocalDate date) {
        this.appointmentDate = date;
    }

    @JsonSetter("startTime")
    public void setStartTime(String time) {
        this.startTime = time;
    }

    @JsonSetter("endTime")
    public void setEndTime(String time) {
        this.endTime = time;
    }
}
