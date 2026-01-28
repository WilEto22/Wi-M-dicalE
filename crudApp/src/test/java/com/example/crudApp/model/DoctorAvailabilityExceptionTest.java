package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorAvailabilityExceptionTest {

    @Test
    void testBuilder() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);
        LocalDate createdAt = LocalDate.now();

        // When
        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .id(1L)
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Vacances")
                .isAvailable(false)
                .startTime("09:00:00")
                .endTime("17:00:00")
                .isActive(true)
                .createdAt(createdAt)
                .build();

        // Then
        assertThat(exception.getId()).isEqualTo(1L);
        assertThat(exception.getDoctor()).isEqualTo(doctor);
        assertThat(exception.getExceptionDate()).isEqualTo(exceptionDate);
        assertThat(exception.getReason()).isEqualTo("Vacances");
        assertThat(exception.getIsAvailable()).isFalse();
        assertThat(exception.getStartTime()).isEqualTo("09:00:00");
        assertThat(exception.getEndTime()).isEqualTo("17:00:00");
        assertThat(exception.getIsActive()).isTrue();
        assertThat(exception.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testBuilderWithDefaultValues() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);

        // When
        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Vacances")
                .build();

        // Then
        assertThat(exception.getIsAvailable()).isFalse();
        assertThat(exception.getIsActive()).isTrue();
        assertThat(exception.getCreatedAt()).isEqualTo(LocalDate.now());
    }

    @Test
    void testUnavailableException() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);

        // When - Doctor is unavailable (vacation)
        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Vacances")
                .isAvailable(false)
                .build();

        // Then
        assertThat(exception.getIsAvailable()).isFalse();
        assertThat(exception.getReason()).isEqualTo("Vacances");
    }

    @Test
    void testAvailableException() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);

        // When - Doctor is available on a normally closed day
        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Jour supplémentaire")
                .isAvailable(true)
                .startTime("10:00:00")
                .endTime("14:00:00")
                .build();

        // Then
        assertThat(exception.getIsAvailable()).isTrue();
        assertThat(exception.getStartTime()).isEqualTo("10:00:00");
        assertThat(exception.getEndTime()).isEqualTo("14:00:00");
    }

    @Test
    void testNoArgsConstructor() {
        // When
        DoctorAvailabilityException exception = new DoctorAvailabilityException();

        // Then
        assertThat(exception).isNotNull();
        assertThat(exception.getId()).isNull();
        assertThat(exception.getDoctor()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);
        LocalDate createdAt = LocalDate.now();

        // When
        DoctorAvailabilityException exception = new DoctorAvailabilityException(
                1L,
                doctor,
                exceptionDate,
                "Vacances",
                false,
                "09:00:00",
                "17:00:00",
                true,
                createdAt
        );

        // Then
        assertThat(exception.getId()).isEqualTo(1L);
        assertThat(exception.getDoctor()).isEqualTo(doctor);
        assertThat(exception.getExceptionDate()).isEqualTo(exceptionDate);
        assertThat(exception.getReason()).isEqualTo("Vacances");
        assertThat(exception.getIsAvailable()).isFalse();
        assertThat(exception.getStartTime()).isEqualTo("09:00:00");
        assertThat(exception.getEndTime()).isEqualTo("17:00:00");
        assertThat(exception.getIsActive()).isTrue();
        assertThat(exception.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testSettersAndGetters() {
        // Given
        DoctorAvailabilityException exception = new DoctorAvailabilityException();
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);
        LocalDate createdAt = LocalDate.now();

        // When
        exception.setId(1L);
        exception.setDoctor(doctor);
        exception.setExceptionDate(exceptionDate);
        exception.setReason("Vacances");
        exception.setIsAvailable(false);
        exception.setStartTime("09:00:00");
        exception.setEndTime("17:00:00");
        exception.setIsActive(true);
        exception.setCreatedAt(createdAt);

        // Then
        assertThat(exception.getId()).isEqualTo(1L);
        assertThat(exception.getDoctor()).isEqualTo(doctor);
        assertThat(exception.getExceptionDate()).isEqualTo(exceptionDate);
        assertThat(exception.getReason()).isEqualTo("Vacances");
        assertThat(exception.getIsAvailable()).isFalse();
        assertThat(exception.getStartTime()).isEqualTo("09:00:00");
        assertThat(exception.getEndTime()).isEqualTo("17:00:00");
        assertThat(exception.getIsActive()).isTrue();
        assertThat(exception.getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    void testInactiveException() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);

        // When
        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Vacances annulées")
                .isActive(false)
                .build();

        // Then
        assertThat(exception.getIsActive()).isFalse();
    }

    @Test
    void testExceptionWithoutSpecificTimes() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);

        // When - Exception without specific start/end times
        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Congé maladie")
                .isAvailable(false)
                .build();

        // Then
        assertThat(exception.getStartTime()).isNull();
        assertThat(exception.getEndTime()).isNull();
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);

        DoctorAvailabilityException exception1 = DoctorAvailabilityException.builder()
                .id(1L)
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Vacances")
                .build();

        DoctorAvailabilityException exception2 = DoctorAvailabilityException.builder()
                .id(1L)
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Vacances")
                .build();

        // Then
        assertThat(exception1).isEqualTo(exception2);
        assertThat(exception1.hashCode()).isEqualTo(exception2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();
        LocalDate exceptionDate = LocalDate.of(2025, 1, 20);

        DoctorAvailabilityException exception = DoctorAvailabilityException.builder()
                .id(1L)
                .doctor(doctor)
                .exceptionDate(exceptionDate)
                .reason("Vacances")
                .build();

        // When
        String result = exception.toString();

        // Then
        assertThat(result).contains("Vacances");
    }
}
