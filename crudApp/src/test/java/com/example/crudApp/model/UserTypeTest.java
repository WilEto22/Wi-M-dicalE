package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTypeTest {

    @Test
    void testDoctorType() {
        // When
        UserType type = UserType.DOCTOR;

        // Then
        assertThat(type).isNotNull();
        assertThat(type.getDisplayName()).isEqualTo("Médecin");
        assertThat(type.toString()).isEqualTo("Médecin");
    }

    @Test
    void testPatientType() {
        // When
        UserType type = UserType.PATIENT;

        // Then
        assertThat(type).isNotNull();
        assertThat(type.getDisplayName()).isEqualTo("Patient");
        assertThat(type.toString()).isEqualTo("Patient");
    }

    @Test
    void testAllValues() {
        // When
        UserType[] types = UserType.values();

        // Then
        assertThat(types).hasSize(2);
        assertThat(types).contains(UserType.DOCTOR, UserType.PATIENT);
    }

    @Test
    void testValueOf() {
        // When
        UserType doctor = UserType.valueOf("DOCTOR");
        UserType patient = UserType.valueOf("PATIENT");

        // Then
        assertThat(doctor).isEqualTo(UserType.DOCTOR);
        assertThat(patient).isEqualTo(UserType.PATIENT);
    }

    @Test
    void testEnumEquality() {
        // Given
        UserType type1 = UserType.DOCTOR;
        UserType type2 = UserType.DOCTOR;
        UserType type3 = UserType.PATIENT;

        // Then
        assertThat(type1).isEqualTo(type2);
        assertThat(type1).isNotEqualTo(type3);
    }
}
