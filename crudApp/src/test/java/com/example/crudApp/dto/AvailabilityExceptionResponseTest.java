package com.example.crudApp.dto;

import com.example.crudApp.model.DoctorAvailabilityException;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class AvailabilityExceptionResponseTest {

    @Test
    void testBuilder() {
        // Given
        LocalDate date = LocalDate.of(2025, 1, 20);
        LocalDate createdAt = LocalDate.now();

        // When
        AvailabilityExceptionResponse response = AvailabilityExceptionResponse.builder()
                .id(1L)
                .doctorId(1L)
                .doctorUsername("dr.martin")
                .doctorEmail("dr.martin@hospital.com")
                .exceptionDate(date)
                .reason("Vacances")
                .isAvailable(false)
                .startTime("09:00:00")
                .endTime("17:00:00")
                .isActive(true)
                .createdAt(createdAt)
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isEqualTo(1L);
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorEmail()).isEqualTo("dr.martin@hospital.com");
        assertThat(response.getExceptionDate()).isEqualTo(date);
        assertThat(response.getReason()).isEqualTo("Vacances");
        assertThat(response.getIsAvailable()).isFalse();
        assertThat(response.getStartTime()).isEqualTo("09:00:00");
        assertThat(response.getEndTime()).isEqualTo("17:00:00");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testFromException() {
        // Given
        User doctor = User.builder()
                .id(1L)
                .username("dr.martin")
                .email("dr.martin@hospital.com")
                .userType(UserType.DOCTOR)
                .build();

        LocalDate date = LocalDate.of(2025, 1, 20);
        LocalDate createdAt = LocalDate.now();

        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .id(1L)
                .doctor(doctor)
                .exceptionDate(date)
                .reason("Vacances")
                .isAvailable(false)
                .startTime("09:00:00")
                .endTime("17:00:00")
                .isActive(true)
                .createdAt(createdAt)
                .build();

        // When
        AvailabilityExceptionResponse response = AvailabilityExceptionResponse.fromException(exception);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isEqualTo(1L);
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorEmail()).isEqualTo("dr.martin@hospital.com");
        assertThat(response.getExceptionDate()).isEqualTo(date);
        assertThat(response.getReason()).isEqualTo("Vacances");
        assertThat(response.getIsAvailable()).isFalse();
        assertThat(response.getStartTime()).isEqualTo("09:00:00");
        assertThat(response.getEndTime()).isEqualTo("17:00:00");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testFromExceptionWithNullDoctor() {
        // Given
        LocalDate date = LocalDate.of(2025, 1, 20);

        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .id(1L)
                .doctor(null)
                .exceptionDate(date)
                .reason("Vacances")
                .isAvailable(false)
                .build();

        // When
        AvailabilityExceptionResponse response = AvailabilityExceptionResponse.fromException(exception);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isNull();
        assertThat(response.getDoctorUsername()).isNull();
        assertThat(response.getDoctorEmail()).isNull();
        assertThat(response.getExceptionDate()).isEqualTo(date);
        assertThat(response.getReason()).isEqualTo("Vacances");
    }

    @Test
    void testNoArgsConstructor() {
        // When
        AvailabilityExceptionResponse response = new AvailabilityExceptionResponse();

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        LocalDate date = LocalDate.of(2025, 1, 20);
        LocalDate createdAt = LocalDate.now();

        // When
        AvailabilityExceptionResponse response = new AvailabilityExceptionResponse(
                1L,
                1L,
                "dr.martin",
                "dr.martin@hospital.com",
                date,
                "Vacances",
                false,
                "09:00:00",
                "17:00:00",
                true,
                createdAt
        );

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isEqualTo(1L);
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorEmail()).isEqualTo("dr.martin@hospital.com");
        assertThat(response.getExceptionDate()).isEqualTo(date);
        assertThat(response.getReason()).isEqualTo("Vacances");
        assertThat(response.getIsAvailable()).isFalse();
        assertThat(response.getStartTime()).isEqualTo("09:00:00");
        assertThat(response.getEndTime()).isEqualTo("17:00:00");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testSettersAndGetters() {
        // Given
        AvailabilityExceptionResponse response = new AvailabilityExceptionResponse();
        LocalDate date = LocalDate.of(2025, 1, 20);
        LocalDate createdAt = LocalDate.now();

        // When
        response.setId(1L);
        response.setDoctorId(1L);
        response.setDoctorUsername("dr.martin");
        response.setDoctorEmail("dr.martin@hospital.com");
        response.setExceptionDate(date);
        response.setReason("Vacances");
        response.setIsAvailable(false);
        response.setStartTime("09:00:00");
        response.setEndTime("17:00:00");
        response.setIsActive(true);
        response.setCreatedAt(createdAt);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDoctorId()).isEqualTo(1L);
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorEmail()).isEqualTo("dr.martin@hospital.com");
        assertThat(response.getExceptionDate()).isEqualTo(date);
        assertThat(response.getReason()).isEqualTo("Vacances");
        assertThat(response.getIsAvailable()).isFalse();
        assertThat(response.getStartTime()).isEqualTo("09:00:00");
        assertThat(response.getEndTime()).isEqualTo("17:00:00");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDate date = LocalDate.of(2025, 1, 20);

        AvailabilityExceptionResponse response1 = AvailabilityExceptionResponse.builder()
                .id(1L)
                .doctorId(1L)
                .exceptionDate(date)
                .reason("Vacances")
                .build();

        AvailabilityExceptionResponse response2 = AvailabilityExceptionResponse.builder()
                .id(1L)
                .doctorId(1L)
                .exceptionDate(date)
                .reason("Vacances")
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        AvailabilityExceptionResponse response = AvailabilityExceptionResponse.builder()
                .id(1L)
                .doctorUsername("dr.martin")
                .reason("Vacances")
                .build();

        // When
        String result = response.toString();

        // Then
        assertThat(result).contains("dr.martin");
        assertThat(result).contains("Vacances");
    }
}
