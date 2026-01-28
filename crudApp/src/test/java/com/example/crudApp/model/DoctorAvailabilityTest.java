package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class DoctorAvailabilityTest {

    @Test
    void testBuilder() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();

        // When
        DoctorAvailability availability = DoctorAvailability.builder()
                .id(1L)
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .slotDurationMinutes(30)
                .isActive(true)
                .build();

        // Then
        assertThat(availability.getId()).isEqualTo(1L);
        assertThat(availability.getDoctor()).isEqualTo(doctor);
        assertThat(availability.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(availability.getStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(availability.getEndTime()).isEqualTo(LocalTime.of(17, 0));
        assertThat(availability.getSlotDurationMinutes()).isEqualTo(30);
        assertThat(availability.getIsActive()).isTrue();
    }

    @Test
    void testBuilderWithDefaultValues() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();

        // When
        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        // Then
        assertThat(availability.getSlotDurationMinutes()).isEqualTo(30);
        assertThat(availability.getIsActive()).isTrue();
    }

    @Test
    void testNoArgsConstructor() {
        // When
        DoctorAvailability availability = new DoctorAvailability();

        // Then
        assertThat(availability).isNotNull();
        assertThat(availability.getId()).isNull();
        assertThat(availability.getDoctor()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();

        // When
        DoctorAvailability availability = new DoctorAvailability(
                1L,
                doctor,
                DayOfWeek.MONDAY,
                LocalTime.of(9, 0),
                LocalTime.of(17, 0),
                30,
                true
        );

        // Then
        assertThat(availability.getId()).isEqualTo(1L);
        assertThat(availability.getDoctor()).isEqualTo(doctor);
        assertThat(availability.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);
        assertThat(availability.getStartTime()).isEqualTo(LocalTime.of(9, 0));
        assertThat(availability.getEndTime()).isEqualTo(LocalTime.of(17, 0));
        assertThat(availability.getSlotDurationMinutes()).isEqualTo(30);
        assertThat(availability.getIsActive()).isTrue();
    }

    @Test
    void testSettersAndGetters() {
        // Given
        DoctorAvailability availability = new DoctorAvailability();
        User doctor = User.builder().id(1L).username("dr.martin").build();

        // When
        availability.setId(1L);
        availability.setDoctor(doctor);
        availability.setDayOfWeek(DayOfWeek.TUESDAY);
        availability.setStartTime(LocalTime.of(10, 0));
        availability.setEndTime(LocalTime.of(18, 0));
        availability.setSlotDurationMinutes(45);
        availability.setIsActive(false);

        // Then
        assertThat(availability.getId()).isEqualTo(1L);
        assertThat(availability.getDoctor()).isEqualTo(doctor);
        assertThat(availability.getDayOfWeek()).isEqualTo(DayOfWeek.TUESDAY);
        assertThat(availability.getStartTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(availability.getEndTime()).isEqualTo(LocalTime.of(18, 0));
        assertThat(availability.getSlotDurationMinutes()).isEqualTo(45);
        assertThat(availability.getIsActive()).isFalse();
    }

    @Test
    void testAllDaysOfWeek() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();

        // Test all days of the week
        for (DayOfWeek day : DayOfWeek.values()) {
            // When
            DoctorAvailability availability = DoctorAvailability.builder()
                    .doctor(doctor)
                    .dayOfWeek(day)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(17, 0))
                    .build();

            // Then
            assertThat(availability.getDayOfWeek()).isEqualTo(day);
        }
    }

    @Test
    void testDifferentSlotDurations() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();

        // Test 15-minute slots
        DoctorAvailability availability15 = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .slotDurationMinutes(15)
                .build();
        assertThat(availability15.getSlotDurationMinutes()).isEqualTo(15);

        // Test 30-minute slots (default)
        DoctorAvailability availability30 = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();
        assertThat(availability30.getSlotDurationMinutes()).isEqualTo(30);

        // Test 60-minute slots
        DoctorAvailability availability60 = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .slotDurationMinutes(60)
                .build();
        assertThat(availability60.getSlotDurationMinutes()).isEqualTo(60);
    }

    @Test
    void testInactiveAvailability() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();

        // When
        DoctorAvailability availability = DoctorAvailability.builder()
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .isActive(false)
                .build();

        // Then
        assertThat(availability.getIsActive()).isFalse();
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();

        DoctorAvailability availability1 = DoctorAvailability.builder()
                .id(1L)
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        DoctorAvailability availability2 = DoctorAvailability.builder()
                .id(1L)
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        // Then
        assertThat(availability1).isEqualTo(availability2);
        assertThat(availability1.hashCode()).isEqualTo(availability2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        User doctor = User.builder().id(1L).username("dr.martin").build();

        DoctorAvailability availability = DoctorAvailability.builder()
                .id(1L)
                .doctor(doctor)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(17, 0))
                .build();

        // When
        String result = availability.toString();

        // Then
        assertThat(result).contains("MONDAY");
    }
}
