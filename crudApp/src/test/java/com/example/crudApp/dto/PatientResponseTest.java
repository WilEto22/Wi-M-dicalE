package com.example.crudApp.dto;

import com.example.crudApp.model.MedicalSpecialty;
import com.example.crudApp.model.Patient;
import com.example.crudApp.model.User;
import com.example.crudApp.model.UserType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PatientResponseTest {

    @Test
    void testBuilder() {
        // When
        PatientResponse response = PatientResponse.builder()
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
                .doctorUsername("dr.martin")
                .doctorSpecialty(MedicalSpecialty.CARDIOLOGIE)
                .doctorSpecialtyDisplay("Cardiologie")
                .build();

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Jean Dupont");
        assertThat(response.getEmail()).isEqualTo("jean@example.com");
        assertThat(response.getAge()).isEqualTo(45);
        assertThat(response.getAddress()).isEqualTo("123 Rue Test");
        assertThat(response.getBloodType()).isEqualTo("A+");
        assertThat(response.getAllergies()).isEqualTo("Pénicilline");
        assertThat(response.getMedicalHistory()).isEqualTo("Diabète");
        assertThat(response.getLastVisit()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(response.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(response.getEmergencyContact()).isEqualTo("Marie Dupont");
        assertThat(response.getEmergencyPhone()).isEqualTo("+33698765432");
        assertThat(response.getInsuranceNumber()).isEqualTo("INS123456");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getDoctorSpecialtyDisplay()).isEqualTo("Cardiologie");
    }

    @Test
    void testFromPatient() {
        // Given
        User doctor = User.builder()
                .id(1L)
                .username("dr.martin")
                .userType(UserType.DOCTOR)
                .specialty(MedicalSpecialty.CARDIOLOGIE)
                .build();

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
                .user(doctor)
                .build();

        // When
        PatientResponse response = PatientResponse.fromPatient(patient);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Jean Dupont");
        assertThat(response.getEmail()).isEqualTo("jean@example.com");
        assertThat(response.getAge()).isEqualTo(45);
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getDoctorSpecialtyDisplay()).isEqualTo("Cardiologie");
    }

    @Test
    void testFromPatientWithNullUser() {
        // Given
        Patient patient = Patient.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .age(45)
                .address("123 Rue Test")
                .isActive(true)
                .user(null)
                .build();

        // When
        PatientResponse response = PatientResponse.fromPatient(patient);

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Jean Dupont");
        assertThat(response.getDoctorUsername()).isNull();
        assertThat(response.getDoctorSpecialty()).isNull();
        assertThat(response.getDoctorSpecialtyDisplay()).isNull();
    }

    @Test
    void testFromPatientWithUserButNullSpecialty() {
        // Given
        User doctor = User.builder()
                .id(1L)
                .username("dr.martin")
                .userType(UserType.DOCTOR)
                .specialty(null)
                .build();

        Patient patient = Patient.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .age(45)
                .address("123 Rue Test")
                .isActive(true)
                .user(doctor)
                .build();

        // When
        PatientResponse response = PatientResponse.fromPatient(patient);

        // Then
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorSpecialty()).isNull();
        assertThat(response.getDoctorSpecialtyDisplay()).isNull();
    }

    @Test
    void testNoArgsConstructor() {
        // When
        PatientResponse response = new PatientResponse();

        // Then
        assertThat(response).isNotNull();
    }

    @Test
    void testAllArgsConstructor() {
        // When
        PatientResponse response = new PatientResponse(
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
                "dr.martin",
                MedicalSpecialty.CARDIOLOGIE,
                "Cardiologie"
        );

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Jean Dupont");
        assertThat(response.getIsActive()).isTrue();
    }

    @Test
    void testSettersAndGetters() {
        // Given
        PatientResponse response = new PatientResponse();

        // When
        response.setId(1L);
        response.setName("Jean Dupont");
        response.setEmail("jean@example.com");
        response.setAge(45);
        response.setAddress("123 Rue Test");
        response.setBloodType("A+");
        response.setAllergies("Pénicilline");
        response.setMedicalHistory("Diabète");
        response.setLastVisit(LocalDate.of(2024, 12, 15));
        response.setPhoneNumber("+33612345678");
        response.setEmergencyContact("Marie Dupont");
        response.setEmergencyPhone("+33698765432");
        response.setInsuranceNumber("INS123456");
        response.setIsActive(true);
        response.setDoctorUsername("dr.martin");
        response.setDoctorSpecialty(MedicalSpecialty.CARDIOLOGIE);
        response.setDoctorSpecialtyDisplay("Cardiologie");

        // Then
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Jean Dupont");
        assertThat(response.getEmail()).isEqualTo("jean@example.com");
        assertThat(response.getAge()).isEqualTo(45);
        assertThat(response.getAddress()).isEqualTo("123 Rue Test");
        assertThat(response.getBloodType()).isEqualTo("A+");
        assertThat(response.getAllergies()).isEqualTo("Pénicilline");
        assertThat(response.getMedicalHistory()).isEqualTo("Diabète");
        assertThat(response.getLastVisit()).isEqualTo(LocalDate.of(2024, 12, 15));
        assertThat(response.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(response.getEmergencyContact()).isEqualTo("Marie Dupont");
        assertThat(response.getEmergencyPhone()).isEqualTo("+33698765432");
        assertThat(response.getInsuranceNumber()).isEqualTo("INS123456");
        assertThat(response.getIsActive()).isTrue();
        assertThat(response.getDoctorUsername()).isEqualTo("dr.martin");
        assertThat(response.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
        assertThat(response.getDoctorSpecialtyDisplay()).isEqualTo("Cardiologie");
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        PatientResponse response1 = PatientResponse.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .build();

        PatientResponse response2 = PatientResponse.builder()
                .id(1L)
                .name("Jean Dupont")
                .email("jean@example.com")
                .build();

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        PatientResponse response = PatientResponse.builder()
                .id(1L)
                .name("Jean Dupont")
                .build();

        // When
        String result = response.toString();

        // Then
        assertThat(result).contains("Jean Dupont");
    }
}
