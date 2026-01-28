package com.example.crudApp.dto;

import com.example.crudApp.model.Appointment;
import com.example.crudApp.model.AppointmentStatus;
import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentResponseTest {

    @Test
    void testBuilder() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        AppointmentResponse response = AppointmentResponse.builder()
                .id(1L)
                .doctorId(1L)
                .doctorUsername("dr.martin")
                .doctorFullName("Dr. Martin")
                .doctorSpecialty(MedicalSpecialty.CARDIOLOGIE)
                .patientId(2L)
                .patientUsername("patient123")
                .patientFullName("Jean Dupont")
                .appointmentDateTime(now)
                .status(AppointmentStatus.CONFIRMED)
                .reason("Consultation de routine")
                .doctorNotes("RAS")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isEqualTo(1L);
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorFullName()).isEqualTo("Dr. Martin");
        assertThat(response.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getPatientId()).isEqualTo(2L);
        assertThat(response.getPatientUsername()).isEqualTo("patient123");
        assertThat(response.getPatientFullName()).isEqualTo("Jean Dupont");
        assertThat(response.getAppointmentDateTime()).isEqualTo(now);
        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(response.getReason()).isEqualTo("Consultation de routine");
        assertThat(response.getDoctorNotes()).isEqualTo("RAS");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testFromAppointment() {
        // Given
        User doctor = User.builder()
                .id(1L)
                .username("dr.martin")
                .fullName("Dr. Martin")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

        User patient = User.builder()
                .id(2L)
                .username("patient123")
                .fullName("Jean Dupont")
                .userType(UserType.PATIENT)
                .build();

        LocalDateTime now = LocalDateTime.now();

        Appointment appointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(now)
                .status(AppointmentStatus.CONFIRMED)
                .reason("Consultation de routine")
                .doctorNotes("RAS")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // When
        AppointmentResponse response = AppointmentResponse.fromAppointment(appointment);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isEqualTo(1L);
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorFullName()).isEqualTo("Dr. Martin");
        assertThat(response.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getPatientId()).isEqualTo(2L);
        assertThat(response.getPatientUsername()).isEqualTo("patient123");
        assertThat(response.getPatientFullName()).isEqualTo("Jean Dupont");
        assertThat(response.getAppointmentDateTime()).isEqualTo(now);
        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(response.getReason()).isEqualTo("Consultation de routine");
        assertThat(response.getDoctorNotes()).isEqualTo("RAS");
    }

    @Test
    void testNoArgsConstructor() {
        // When
        AppointmentResponse response = new AppointmentResponse();

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        // When
        AppointmentResponse response = new AppointmentResponse(
                1L,
                1L,
                "dr.martin",
                "Dr. Martin",
                MedicalSpecialty.CARDIOLOGIE,
                2L,
                "patient123",
                "Jean Dupont",
                now,
                AppointmentStatus.CONFIRMED,
                "Consultation",
                "RAS",
                now,
                now
        );

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isEqualTo(1L);
        assertThat(response.getPatientId()).isEqualTo(2L);
    }

    @Test
    void testSettersAndGetters() {
        // Given
        AppointmentResponse response = new AppointmentResponse();
        LocalDateTime now = LocalDateTime.now();

        // When
        response.setId(1L);
        response.setDoctorId(1L);
        response.setDoctorUsername("dr.martin");
        response.setDoctorFullName("Dr. Martin");
        response.setDoctorSpecialty(MedicalSpecialty.CARDIOLOGIE);
        response.setPatientId(2L);
        response.setPatientUsername("patient123");
        response.setPatientFullName("Jean Dupont");
        response.setAppointmentDateTime(now);
        response.setStatus(AppointmentStatus.CONFIRMED);
        response.setReason("Consultation");
        response.setDoctorNotes("RAS");
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isEqualTo(1L);
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorFullName()).isEqualTo("Dr. Martin");
        assertThat(response.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getPatientId()).isEqualTo(2L);
        assertThat(response.getPatientUsername()).isEqualTo("patient123");
        assertThat(response.getPatientFullName()).isEqualTo("Jean Dupont");
        assertThat(response.getAppointmentDateTime()).isEqualTo(now);
        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(response.getReason()).isEqualTo("Consultation");
        assertThat(response.getDoctorNotes()).isEqualTo("RAS");
        assertThat(response.getCreatedAt()).isEqualTo(now);
        assertThat(response.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        AppointmentResponse response1 = AppointmentResponse.builder()
                .id(1L)
                .doctorId(1L)
                .patientId(2L)
                .appointmentDateTime(now)
                .build();

        AppointmentResponse response2 = AppointmentResponse.builder()
                .id(1L)
                .doctorId(1L)
                .patientId(2L)
                .appointmentDateTime(now)
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        AppointmentResponse response = AppointmentResponse.builder()
                .id(1L)
                .doctorUsername("dr.martin")
                .patientUsername("patient123")
                .build();

        // When
        String result = response.toString();

        // Then
        assertThat(result).contains("dr.martin");
        assertThat(result).contains("patient123");
    }
}
