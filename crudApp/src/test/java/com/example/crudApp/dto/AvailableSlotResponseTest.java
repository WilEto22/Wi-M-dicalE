package com.example.crudApp.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AvailableSlotResponseTest {

    @Test
    void testBuilder() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 20, 10, 0);

        // When
        AvailableSlotResponse response = AvailableSlotResponse.builder()
                .dateTime(dateTime)
                .available(true)
                .displayTime("10:00 - 10:30")
                .build();

        // Then
        assertThat(response.getDateTime()).isEqualTo(dateTime);
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getDisplayTime()).isEqualTo("10:00 - 10:30");
    }

    @Test
    void testOfFactoryMethod() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 20, 10, 0);

        // When
        AvailableSlotResponse response = AvailableSlotResponse.of(dateTime, true);

        // Then
        assertThat(response.getDateTime()).isEqualTo(dateTime);
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getDisplayTime()).isEqualTo("10:00");
    }

    @Test
    void testOfFactoryMethodWithUnavailableSlot() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 20, 14, 30);

        // When
        AvailableSlotResponse response = AvailableSlotResponse.of(dateTime, false);

        // Then
        assertThat(response.getDateTime()).isEqualTo(dateTime);
        assertThat(response.isAvailable()).isFalse();
        assertThat(response.getDisplayTime()).isEqualTo("14:30");
    }

    @Test
    void testNoArgsConstructor() {
        // When
        AvailableSlotResponse response = new AvailableSlotResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getDateTime()).isNull();
        assertThat(response.isAvailable()).isFalse();
        assertThat(response.getDisplayTime()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 20, 10, 0);

        // When
        AvailableSlotResponse response = new AvailableSlotResponse(
                dateTime,
                true,
                "10:00 - 10:30"
        );

        // Then
        assertThat(response.getDateTime()).isEqualTo(dateTime);
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getDisplayTime()).isEqualTo("10:00 - 10:30");
    }

    @Test
    void testSettersAndGetters() {
        // Given
        AvailableSlotResponse response = new AvailableSlotResponse();
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 20, 10, 0);

        // When
        response.setDateTime(dateTime);
        response.setAvailable(true);
        response.setDisplayTime("10:00 - 10:30");

        // Then
        assertThat(response.getDateTime()).isEqualTo(dateTime);
        assertThat(response.isAvailable()).isTrue();
        assertThat(response.getDisplayTime()).isEqualTo("10:00 - 10:30");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 20, 10, 0);

        AvailableSlotResponse response1 = AvailableSlotResponse.builder()
                .dateTime(dateTime)
                .available(true)
                .displayTime("10:00")
                .build();

        AvailableSlotResponse response2 = AvailableSlotResponse.builder()
                .dateTime(dateTime)
                .available(true)
                .displayTime("10:00")
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 20, 10, 0);
        AvailableSlotResponse response = AvailableSlotResponse.of(dateTime, true);

        // When
        String result = response.toString();

        // Then
        assertThat(result).contains("10:00");
        assertThat(result).contains("true");
    }
}
