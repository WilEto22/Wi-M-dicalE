package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class AppointmentTest {

    @Test
    void testBuilder() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        User patient = User.builder().id(2L).username("patient123").build();
        LocalDateTime now = LocalDateTime.now();

        // When
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

        // Then
        assertThat(appointment.getId()).isEqualTo(1L);
        assertThat(appointment.getDoctor()).isEqualTo(doctor);
        assertThat(appointment.getPatient()).isEqualTo(patient);
        assertThat(appointment.getAppointmentDateTime()).isEqualTo(now);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(appointment.getReason()).isEqualTo("Consultation de routine");
        assertThat(appointment.getDoctorNotes()).isEqualTo("RAS");
        assertThat(appointment.getCreatedAt()).isEqualTo(now);
        assertThat(appointment.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testBuilderWithDefaultStatus() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        User patient = User.builder().id(2L).username("patient123").build();

        // When
        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now())
                .build();

        // Then
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING);
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Appointment appointment = new Appointment();

        // Then
        assertThat(appointment).isNotNull();
        assertThat(appointment.getId()).isNull();
        assertThat(appointment.getDoctor()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        User patient = User.builder().id(2L).username("patient123").build();
        LocalDateTime now = LocalDateTime.now();

        // When
        Appointment appointment = new Appointment(
                1L,
                doctor,
                patient,
                now,
                AppointmentStatus.CONFIRMED,
                "Consultation",
                "RAS",
                now,
                now
        );

        // Then
        assertThat(appointment.getId()).isEqualTo(1L);
        assertThat(appointment.getDoctor()).isEqualTo(doctor);
        assertThat(appointment.getPatient()).isEqualTo(patient);
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Appointment appointment = new Appointment();
        User doctor = User.builder().id(1L).username("dr.martin").build();
        User patient = User.builder().id(2L).username("patient123").build();
        LocalDateTime now = LocalDateTime.now();

        // When
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDateTime(now);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        appointment.setReason("Consultation");
        appointment.setDoctorNotes("RAS");
        appointment.setCreatedAt(now);
        appointment.setUpdatedAt(now);

        // Then
        assertThat(appointment.getId()).isEqualTo(1L);
        assertThat(appointment.getDoctor()).isEqualTo(doctor);
        assertThat(appointment.getPatient()).isEqualTo(patient);
        assertThat(appointment.getAppointmentDateTime()).isEqualTo(now);
        assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(appointment.getReason()).isEqualTo("Consultation");
        assertThat(appointment.getDoctorNotes()).isEqualTo("RAS");
        assertThat(appointment.getCreatedAt()).isEqualTo(now);
        assertThat(appointment.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    void testPrePersist() {
        // Given
        Appointment appointment = new Appointment();
        User doctor = User.builder().id(1L).username("dr.martin").build();
        User patient = User.builder().id(2L).username("patient123").build();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setAppointmentDateTime(LocalDateTime.now());
        LocalDateTime beforeCreate = LocalDateTime.now();

        // When
        appointment.onCreate();

        // Then
        assertThat(appointment.getCreatedAt()).isNotNull();
        assertThat(appointment.getUpdatedAt()).isNotNull();
        // Use time tolerance to avoid flaky tests (nanosecond precision issues)
        assertThat(appointment.getCreatedAt()).isCloseTo(appointment.getUpdatedAt(), within(1, ChronoUnit.MILLIS));
        assertThat(appointment.getCreatedAt()).isCloseTo(beforeCreate, within(100, ChronoUnit.MILLIS));
    }

    @Test
    void testPreUpdate() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Appointment appointment = new Appointment();
        appointment.setCreatedAt(createdAt);
        appointment.setUpdatedAt(createdAt);
        LocalDateTime beforeUpdate = LocalDateTime.now();

        // When
        appointment.onUpdate();

        // Then
        assertThat(appointment.getUpdatedAt()).isNotNull();
        assertThat(appointment.getUpdatedAt()).isAfter(createdAt);
        assertThat(appointment.getCreatedAt()).isEqualTo(createdAt);
        // Use time tolerance to avoid flaky tests
        assertThat(appointment.getUpdatedAt()).isCloseTo(beforeUpdate, within(100, ChronoUnit.MILLIS));
    }

    @Test
    void testAppointmentStatuses() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        User patient = User.builder().id(2L).username("patient123").build();

        // Test PENDING
        Appointment pending = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now())
                .status(AppointmentStatus.PENDING)
                .build();
        assertThat(pending.getStatus()).isEqualTo(AppointmentStatus.PENDING);

        // Test CONFIRMED
        Appointment confirmed = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now())
                .status(AppointmentStatus.CONFIRMED)
                .build();
        assertThat(confirmed.getStatus()).isEqualTo(AppointmentStatus.CONFIRMED);

        // Test CANCELLED
        Appointment cancelled = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now())
                .status(AppointmentStatus.CANCELLED)
                .build();
        assertThat(cancelled.getStatus()).isEqualTo(AppointmentStatus.CANCELLED);

        // Test COMPLETED
        Appointment completed = Appointment.builder()
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now())
                .status(AppointmentStatus.COMPLETED)
                .build();
        assertThat(completed.getStatus()).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        User patient = User.builder().id(2L).username("patient123").build();
        LocalDateTime now = LocalDateTime.now();

        Appointment appointment1 = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(now)
                .build();

        Appointment appointment2 = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(now)
                .build();

        // Then
        assertThat(appointment1).isEqualTo(appointment2);
        assertThat(appointment1.hashCode()).isEqualTo(appointment2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        User patient = User.builder().id(2L).username("patient123").build();

        Appointment appointment = Appointment.builder()
                .id(1L)
                .doctor(doctor)
                .patient(patient)
                .appointmentDateTime(LocalDateTime.now())
                .build();

        // When
        String result = appointment.toString();

        // Then
        assertThat(result).contains("Appointment");
    }
}
