package com.example.crudApp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PatientTest {

    @Test
    void testBuilder() {
        // When
        Patient patient = Patient.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .age(45)
                .address("123 Rue Test")
                .bloodType("A+")
                .allergies("Pénicilline")
                .medicalHistory("Diabète")
                .lastVisit(LocalDate.of(2024, 12, 15))
                .phoneNumber("+33612345678")
                .emergencyContact("Marie Dupont")
                .emergencyPhone("+33698765432")
                .insuranceNumber("INS123456")
                .isActive(true)
                .build();

        // Then
        assertThat(patient.getId()).isEqualTo(1L);
        assertThat(patient.getName()).isEqualTo("Jean Dupont");
        assertThat(patient.getEmail()).isEqualTo("jean@example.com");
        assertThat(patient.getAge()).isEqualTo(45);
        assertThat(patient.getAddress()).isEqualTo("123 Rue Test");
        assertThat(patient.getBloodType()).isEqualTo("A+");
        assertThat(patient.getAllergies()).isEqualTo("Pénicilline");
        assertThat(patient.getMedicalHistory()).isEqualTo("Diabète");
        assertThat(patient.getLastVisit()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(patient.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(patient.getEmergencyContact()).isEqualTo("Marie Dupont");
        assertThat(patient.getEmergencyPhone()).isEqualTo("+33698765432");
        assertThat(patient.getInsuranceNumber()).isEqualTo("INS123456");
        assertThat(patient.getIsActive()).isTrue();
    }

    @Test
    void testBuilderWithDefaultIsActive() {
        // When
        Patient patient = Patient.builder()
                .name("Jean Dupont")
                .email("jean@example.com")
                .age(45)
                .address("123 Rue Test")
                .build();

        // Then
        assertThat(patient.getIsActive()).isTrue();
    }

    @Test
    void testPatientWithUser() {
        // Given
        User doctor = User.builder()
                .id(1L)
                .username("dr.martin")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

        // When
        Patient patient = Patient.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .age(45)
                .address("123 Rue Test")
                .user(doctor)
                .build();

        // Then
        assertThat(patient.getUser()).isNotNull();
        assertThat(patient.getUser().getUsername()).isEqualTo("dr.martin");
        assertThat(patient.getUser().getSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
    }

    @Test
    void testNoArgsConstructor() {
        // When
        Patient patient = new Patient();

        // Then
        assertThat(patient).isNotNull();
        assertThat(patient.getId()).isNull();
        assertThat(patient.getName()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // When
        Patient patient = new Patient(
                1L,
                "Jean Dupont",
                "jean@example.com",
                45,
                "123 Rue Test",
                "A+",
                "Pénicilline",
                "Diabète",
                LocalDate.of(2024, 12, 15),
                "+33612345678",
                "Marie Dupont",
                "+33698765432",
                "INS123456",
                true,
                null
        );

        // Then
        assertThat(patient.getId()).isEqualTo(1L);
        assertThat(patient.getName()).isEqualTo("Jean Dupont");
        assertThat(patient.getEmail()).isEqualTo("jean@example.com");
    }

    @Test
    void testSettersAndGetters() {
        // Given
        Patient patient = new Patient();
        User doctor = User.builder().id(1L).username("dr.martin").build();

        // When
        patient.setId(1L);
        patient.setName("Jean Dupont");
        patient.setEmail("jean@example.com");
        patient.setAge(45);
        patient.setAddress("123 Rue Test");
        patient.setBloodType("A+");
        patient.setAllergies("Pénicilline");
        patient.setMedicalHistory("Diabète");
        patient.setLastVisit(LocalDate.of(2024, 12, 15));
        patient.setPhoneNumber("+33612345678");
        patient.setEmergencyContact("Marie Dupont");
        patient.setEmergencyPhone("+33698765432");
        patient.setInsuranceNumber("INS123456");
        patient.setIsActive(true);
        patient.setUser(doctor);

        // Then
        assertThat(patient.getId()).isEqualTo(1L);
        assertThat(patient.getName()).isEqualTo("Jean Dupont");
        assertThat(patient.getEmail()).isEqualTo("jean@example.com");
        assertThat(patient.getAge()).isEqualTo(45);
        assertThat(patient.getAddress()).isEqualTo("123 Rue Test");
        assertThat(patient.getBloodType()).isEqualTo("A+");
        assertThat(patient.getAllergies()).isEqualTo("Pénicilline");
        assertThat(patient.getMedicalHistory()).isEqualTo("Diabète");
        assertThat(patient.getLastVisit()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(patient.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(patient.getEmergencyContact()).isEqualTo("Marie Dupont");
        assertThat(patient.getEmergencyPhone()).isEqualTo("+33698765432");
        assertThat(patient.getInsuranceNumber()).isEqualTo("INS123456");
        assertThat(patient.getIsActive()).isTrue();
        assertThat(patient.getUser()).isEqualTo(doctor);
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        Patient patient1 = Patient.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .age(45)
                .address("123 Rue Test")
                .build();

        Patient patient2 = Patient.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .age(45)
                .address("123 Rue Test")
                .build();

        // Then
        assertThat(patient1).isEqualTo(patient2);
        assertThat(patient1.hashCode()).isEqualTo(patient2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        Patient patient = Patient.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .build();

        // When
        String result = patient.toString();

        // Then
        assertThat(result).contains("Jean Dupont");
        assertThat(result).contains("jean@example.com");
    }

    @Test
    void testInactivePatient() {
        // When
        Patient patient = Patient.builder()
                .name("Jean Dupont")
                .email("jean@example.com")
                .age(45)
                .address("123 Rue Test")
                .isActive(false)
                .build();

        // Then
        assertThat(patient.getIsActive()).isFalse();
    }
}
