package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AppointmentStatusTest {

    @Test
    void testPendingStatus() {
        // When
        AppointmentStatus status = AppointmentStatus.PENDING;

        // Then
        assertThat(status).isNotNull();
        assertThat(status.getDisplayName()).isEqualTo("En attente");
        assertThat(status.toString()).isEqualTo("En attente");
    }

    @Test
    void testConfirmedStatus() {
        // When
        AppointmentStatus status = AppointmentStatus.CONFIRMED;

        // Then
        assertThat(status).isNotNull();
        assertThat(status.getDisplayName()).isEqualTo("Confirmé");
        assertThat(status.toString()).isEqualTo("Confirmé");
    }

    @Test
    void testCancelledStatus() {
        // When
        AppointmentStatus status = AppointmentStatus.CANCELLED;

        // Then
        assertThat(status).isNotNull();
        assertThat(status.getDisplayName()).isEqualTo("Annulé");
        assertThat(status.toString()).isEqualTo("Annulé");
    }

    @Test
    void testCompletedStatus() {
        // When
        AppointmentStatus status = AppointmentStatus.COMPLETED;

        // Then
        assertThat(status).isNotNull();
        assertThat(status.getDisplayName()).isEqualTo("Terminé");
        assertThat(status.toString()).isEqualTo("Terminé");
    }

    @Test
    void testAllValues() {
        // When
        AppointmentStatus[] statuses = AppointmentStatus.values();

        // Then
        assertThat(statuses).hasSize(4);
        assertThat(statuses).contains(
                AppointmentStatus.PENDING,
                AppointmentStatus.CONFIRMED,
                AppointmentStatus.CANCELLED,
                AppointmentStatus.COMPLETED
        );
    }

    @Test
    void testValueOf() {
        // When
        AppointmentStatus pending = AppointmentStatus.valueOf("PENDING");
        AppointmentStatus confirmed = AppointmentStatus.valueOf("CONFIRMED");
        AppointmentStatus cancelled = AppointmentStatus.valueOf("CANCELLED");
        AppointmentStatus completed = AppointmentStatus.valueOf("COMPLETED");

        // Then
        assertThat(pending).isEqualTo(AppointmentStatus.PENDING);
        assertThat(confirmed).isEqualTo(AppointmentStatus.CONFIRMED);
        assertThat(cancelled).isEqualTo(AppointmentStatus.CANCELLED);
        assertThat(completed).isEqualTo(AppointmentStatus.COMPLETED);
    }

    @Test
    void testEnumEquality() {
        // Given
        AppointmentStatus status1 = AppointmentStatus.CONFIRMED;
        AppointmentStatus status2 = AppointmentStatus.CONFIRMED;
        AppointmentStatus status3 = AppointmentStatus.PENDING;

        // Then
        assertThat(status1).isEqualTo(status2);
        assertThat(status1).isNotEqualTo(status3);
    }
}
