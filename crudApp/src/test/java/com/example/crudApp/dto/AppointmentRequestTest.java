package com.example.crudApp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidAppointmentRequestWithDateTime() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        request.setReason("Consultation de routine");

        // When
        Set<ConstraintViolation<AppointmentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testValidAppointmentRequestWithDateAndTime() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setAppointmentDate(LocalDate.now().plusDays(1));
        request.setStartTime("10:00:00");
        request.setEndTime("10:30:00");
        request.setReason("Consultation de routine");

        // When
        Set<ConstraintViolation<AppointmentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testDoctorIdIsRequired() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(1));

        // When
        Set<ConstraintViolation<AppointmentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'ID du médecin est obligatoire")))
                .isTrue();
    }

    @Test
    void testReasonMaxLength() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        request.setReason("a".repeat(501));

        // When
        Set<ConstraintViolation<AppointmentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("500 caractères")))
                .isTrue();
    }

    @Test
    void testNotesMaxLength() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setAppointmentDateTime(LocalDateTime.now().plusDays(1));
        request.setNotes("a".repeat(2001));

        // When
        Set<ConstraintViolation<AppointmentRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("2000 caractères")))
                .isTrue();
    }

    @Test
    void testGetAppointmentDateTimeFromDateTime() {
        // Given
        LocalDateTime expectedDateTime = LocalDateTime.of(2025, 1, 20, 10, 0);
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDateTime(expectedDateTime);

        // When
        LocalDateTime result = request.getAppointmentDateTime();

        // Then
        assertThat(result).isEqualTo(expectedDateTime);
    }

    @Test
    void testGetAppointmentDateTimeFromDateAndTime() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDate(LocalDate.of(2025, 1, 20));
        request.setStartTime("10:00:00");

        // When
        LocalDateTime result = request.getAppointmentDateTime();

        // Then
        assertThat(result).isEqualTo(LocalDateTime.of(2025, 1, 20, 10, 0));
    }

    @Test
    void testGetAppointmentDateTimeReturnsNullWhenNoDataProvided() {
        // Given
        AppointmentRequest request = new AppointmentRequest();

        // When
        LocalDateTime result = request.getAppointmentDateTime();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetAppointmentDateTimeReturnsNullWhenOnlyDateProvided() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setAppointmentDate(LocalDate.of(2025, 1, 20));

        // When
        LocalDateTime result = request.getAppointmentDateTime();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testGetAppointmentDateTimeReturnsNullWhenOnlyTimeProvided() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setStartTime("10:00:00");

        // When
        LocalDateTime result = request.getAppointmentDateTime();

        // Then
        assertThat(result).isNull();
    }

    @Test
    void testSettersAndGetters() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        LocalDateTime dateTime = LocalDateTime.now().plusDays(1);
        LocalDate date = LocalDate.now().plusDays(1);

        // When
        request.setDoctorId(1L);
        request.setAppointmentDateTime(dateTime);
        request.setAppointmentDate(date);
        request.setStartTime("10:00:00");
        request.setEndTime("10:30:00");
        request.setReason("Consultation");
        request.setNotes("Notes importantes");

        // Then
        assertThat(request.getDoctorId()).isEqualTo(1L);
        assertThat(request.getAppointmentDate()).isEqualTo(date);
        assertThat(request.getStartTime()).isEqualTo("10:00:00");
        assertThat(request.getEndTime()).isEqualTo("10:30:00");
        assertThat(request.getReason()).isEqualTo("Consultation");
        assertThat(request.getNotes()).isEqualTo("Notes importantes");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        AppointmentRequest request1 = new AppointmentRequest();
        request1.setDoctorId(1L);
        request1.setAppointmentDate(LocalDate.of(2025, 1, 20));
        request1.setStartTime("10:00:00");

        AppointmentRequest request2 = new AppointmentRequest();
        request2.setDoctorId(1L);
        request2.setAppointmentDate(LocalDate.of(2025, 1, 20));
        request2.setStartTime("10:00:00");

        // Then
        assertThat(request1).isEqualTo(request2);
        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        AppointmentRequest request = new AppointmentRequest();
        request.setDoctorId(1L);
        request.setReason("Consultation");

        // When
        String result = request.toString();

        // Then
        assertThat(result).contains("doctorId");
        assertThat(result).contains("Consultation");
    }
}
