package com.example.crudApp.dto;

import com.example.crudApp.model.Appointment;
import com.example.crudApp.model.AppointmentStatus;
import com.example.crudApp.model.MedicalSpecialty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {

    private Long id;
    private Long doctorId;
    private String doctorUsername;
    private String doctorFullName;
    private MedicalSpecialty doctorSpecialty;
    private Long patientId;
    private String patientUsername;
    private String patientFullName;
    private LocalDateTime appointmentDateTime;
    private AppointmentStatus status;
    private String reason;
    private String doctorNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AppointmentResponse fromAppointment(Appointment appointment) {
        return AppointmentResponse.builder()
                .id(appointment.getId())
                .doctorId(appointment.getDoctor().getId())
                .doctorUsername(appointment.getDoctor().getUsername())
                .doctorFullName(appointment.getDoctor().getFullName())
                .doctorSpecialty(appointment.getDoctor().getSpecialty())
                .patientId(appointment.getPatient().getId())
                .patientUsername(appointment.getPatient().getUsername())
                .patientFullName(appointment.getPatient().getFullName())
                .appointmentDateTime(appointment.getAppointmentDateTime())
                .status(appointment.getStatus())
                .reason(appointment.getReason())
                .doctorNotes(appointment.getDoctorNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}
