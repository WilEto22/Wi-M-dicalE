package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void testBuilder() {
        // When
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("password123")
                .roles("ROLE_USER")
                .userType(UserType.PATIENT)
                .fullName("Test User")
                .phoneNumber("+33612345678")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .build();

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRoles()).isEqualTo("ROLE_USER");
        assertThat(user.getUserType()).isEqualTo(UserType.PATIENT);
        assertThat(user.getFullName()).isEqualTo("Test User");
        assertThat(user.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(user.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
    }

    @Test
    void testDoctorUser() {
        // When
        User doctor = User.builder()
                .id(1L)
                .username("dr.martin")
                .email("dr.martin@hospital.com")
                .password("password123")
                .roles("ROLE_DOCTOR")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .fullName("Dr. Martin")
                .build();

        // Then
        assertThat(doctor.getUserType()).isEqualTo(UserType.DOCTOR);
        assertThat(doctor.getSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(doctor.getRoles()).isEqualTo("ROLE_DOCTOR");
    }

    @Test
    void testPatientUser() {
        // When
        User patient = User.builder()
                .id(2L)
                .username("patient123")
                .email("patient@example.com")
                .password("password123")
                .roles("ROLE_PATIENT")
                .userType(UserType.PATIENT)
                .fullName("Jean Dupont")
                .phoneNumber("+33612345678")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .build();

        // Then
        assertThat(patient.getUserType()).isEqualTo(UserType.PATIENT);
        assertThat(patient.getSpecialty()).isNull();
        assertThat(patient.getFullName()).isEqualTo("Jean Dupont");
        assertThat(patient.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(patient.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
    }

    @Test
    void testNoArgsConstructor() {
        // When
        User user = new User();

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // When
        User user = new User(
                1L,
                "testuser",
                "test@example.com",
                "password123",
                "ROLE_USER",
                UserType.PATIENT,
                null,
                "Test User",
                "+33612345678",
                "123 Rue de Test, Paris",
                LocalDate.of(1990, 5, 15),
                null,
                "local",
                null,
                false
        );

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testSettersAndGetters() {
        // Given
        User user = new User();

        // When
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRoles("ROLE_USER");
        user.setUserType(UserType.PATIENT);
        user.setSpecialty(MedicalSpecialty.CARDIOLOGIE);
        user.setFullName("Test User");
        user.setPhoneNumber("+33612345678");
        user.setDateOfBirth(LocalDate.of(1990, 5, 15));

        // Then
        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getEmail()).isEqualTo("test@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRoles()).isEqualTo("ROLE_USER");
        assertThat(user.getUserType()).isEqualTo(UserType.PATIENT);
        assertThat(user.getSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(user.getFullName()).isEqualTo("Test User");
        assertThat(user.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(user.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 5, 15));
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        User user1 = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        User user2 = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        // Then
        assertThat(user1).isEqualTo(user2);
        assertThat(user1.hashCode()).isEqualTo(user2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        User user = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .build();

        // When
        String result = user.toString();

        // Then
        assertThat(result).contains("testuser");
        assertThat(result).contains("test@example.com");
    }

    @Test
    void testMultipleRoles() {
        // When
        User user = User.builder()
                .username("admin")
                .roles("ROLE_USER,ROLE_ADMIN")
                .build();

        // Then
        assertThat(user.getRoles()).isEqualTo("ROLE_USER,ROLE_ADMIN");
        assertThat(user.getRoles()).contains("ROLE_USER");
        assertThat(user.getRoles()).contains("ROLE_ADMIN");
    }
}
