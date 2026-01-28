package com.example.crudApp.dto;

import com.example.crudApp.model.MedicalSpecialty;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PatientSearchCriteriaTest {

    @Test
    void testNoArgsConstructor() {
        // When
        PatientSearchCriteria criteria = new PatientSearchCriteria();

        // Then
        assertThat(criteria).isNotNull();
        assertThat(criteria.getName()).isNull();
        assertThat(criteria.getEmail()).isNull();
        assertThat(criteria.getMinAge()).isNull();
        assertThat(criteria.getMaxAge()).isNull();
        assertThat(criteria.getAddress()).isNull();
        assertThat(criteria.getBloodType()).isNull();
        assertThat(criteria.getAllergy()).isNull();
        assertThat(criteria.getLastVisitAfter()).isNull();
        assertThat(criteria.getLastVisitBefore()).isNull();
        assertThat(criteria.getIsActive()).isNull();
        assertThat(criteria.getInsuranceNumber()).isNull();
        assertThat(criteria.getPhoneNumber()).isNull();
        assertThat(criteria.getEmergencyContact()).isNull();
        assertThat(criteria.getDoctorSpecialty()).isNull();
    }

    @Test
    void testAllArgsConstructor() {
        // Given
        LocalDate lastVisitAfter = LocalDate.of(2024, 1, 1);
        LocalDate lastVisitBefore = LocalDate.of(2024, 12, 31);

        // When
        PatientSearchCriteria criteria = new PatientSearchCriteria(
                "Jean Dupont",
                "jean@example.com",
                30,
                50,
                "Paris",
                "A+",
                "Pénicilline",
                lastVisitAfter,
                lastVisitBefore,
                true,
                "INS123456",
                "+33612345678",
                "Marie Dupont",
                MedicalSpecialty.CARDIOLOGIE
        );

        // Then
        assertThat(criteria.getName()).isEqualTo("Jean Dupont");
        assertThat(criteria.getEmail()).isEqualTo("jean@example.com");
        assertThat(criteria.getMinAge()).isEqualTo(30);
        assertThat(criteria.getMaxAge()).isEqualTo(50);
        assertThat(criteria.getAddress()).isEqualTo("Paris");
        assertThat(criteria.getBloodType()).isEqualTo("A+");
        assertThat(criteria.getAllergy()).isEqualTo("Pénicilline");
        assertThat(criteria.getLastVisitAfter()).isEqualTo(lastVisitAfter);
        assertThat(criteria.getLastVisitBefore()).isEqualTo(lastVisitBefore);
        assertThat(criteria.getIsActive()).isTrue();
        assertThat(criteria.getInsuranceNumber()).isEqualTo("INS123456");
        assertThat(criteria.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(criteria.getEmergencyContact()).isEqualTo("Marie Dupont");
        assertThat(criteria.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
    }

    @Test
    void testSettersAndGetters() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        LocalDate lastVisitAfter = LocalDate.of(2024, 1, 1);
        LocalDate lastVisitBefore = LocalDate.of(2024, 12, 31);

        // When
        criteria.setName("Jean Dupont");
        criteria.setEmail("jean@example.com");
        criteria.setMinAge(30);
        criteria.setMaxAge(50);
        criteria.setAddress("Paris");
        criteria.setBloodType("A+");
        criteria.setAllergy("Pénicilline");
        criteria.setLastVisitAfter(lastVisitAfter);
        criteria.setLastVisitBefore(lastVisitBefore);
        criteria.setIsActive(true);
        criteria.setInsuranceNumber("INS123456");
        criteria.setPhoneNumber("+33612345678");
        criteria.setEmergencyContact("Marie Dupont");
        criteria.setDoctorSpecialty(MedicalSpecialty.CARDIOLOGIE);

        // Then
        assertThat(criteria.getName()).isEqualTo("Jean Dupont");
        assertThat(criteria.getEmail()).isEqualTo("jean@example.com");
        assertThat(criteria.getMinAge()).isEqualTo(30);
        assertThat(criteria.getMaxAge()).isEqualTo(50);
        assertThat(criteria.getAddress()).isEqualTo("Paris");
        assertThat(criteria.getBloodType()).isEqualTo("A+");
        assertThat(criteria.getAllergy()).isEqualTo("Pénicilline");
        assertThat(criteria.getLastVisitAfter()).isEqualTo(lastVisitAfter);
        assertThat(criteria.getLastVisitBefore()).isEqualTo(lastVisitBefore);
        assertThat(criteria.getIsActive()).isTrue();
        assertThat(criteria.getInsuranceNumber()).isEqualTo("INS123456");
        assertThat(criteria.getPhoneNumber()).isEqualTo("+33612345678");
        assertThat(criteria.getEmergencyContact()).isEqualTo("Marie Dupont");
        assertThat(criteria.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.CARDIOLOGIE);
    }

    @Test
    void testPartialCriteria() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();

        // When - Only set some criteria
        criteria.setName("Jean");
        criteria.setMinAge(30);
        criteria.setIsActive(true);

        // Then
        assertThat(criteria.getName()).isEqualTo("Jean");
        assertThat(criteria.getMinAge()).isEqualTo(30);
        assertThat(criteria.getIsActive()).isTrue();
        assertThat(criteria.getEmail()).isNull();
        assertThat(criteria.getMaxAge()).isNull();
        assertThat(criteria.getBloodType()).isNull();
    }

    @Test
    void testSearchByAgeRange() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();

        // When
        criteria.setMinAge(30);
        criteria.setMaxAge(50);

        // Then
        assertThat(criteria.getMinAge()).isEqualTo(30);
        assertThat(criteria.getMaxAge()).isEqualTo(50);
    }

    @Test
    void testSearchByDateRange() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        LocalDate after = LocalDate.of(2024, 1, 1);
        LocalDate before = LocalDate.of(2024, 12, 31);

        // When
        criteria.setLastVisitAfter(after);
        criteria.setLastVisitBefore(before);

        // Then
        assertThat(criteria.getLastVisitAfter()).isEqualTo(after);
        assertThat(criteria.getLastVisitBefore()).isEqualTo(before);
    }

    @Test
    void testSearchByDoctorSpecialty() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();

        // When
        criteria.setDoctorSpecialty(MedicalSpecialty.DERMATOLOGIE);

        // Then
        assertThat(criteria.getDoctorSpecialty()).isEqualTo(MedicalSpecialty.DERMATOLOGIE);
    }

    @Test
    void testSearchByActiveStatus() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();

        // When - Search for active patients
        criteria.setIsActive(true);

        // Then
        assertThat(criteria.getIsActive()).isTrue();

        // When - Search for inactive patients
        criteria.setIsActive(false);

        // Then
        assertThat(criteria.getIsActive()).isFalse();
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        PatientSearchCriteria criteria1 = new PatientSearchCriteria();
        criteria1.setName("Jean Dupont");
        criteria1.setEmail("jean@example.com");
        criteria1.setMinAge(30);

        PatientSearchCriteria criteria2 = new PatientSearchCriteria();
        criteria2.setName("Jean Dupont");
        criteria2.setEmail("jean@example.com");
        criteria2.setMinAge(30);

        // Then
        assertThat(criteria1).isEqualTo(criteria2);
        assertThat(criteria1.hashCode()).isEqualTo(criteria2.hashCode());
    }

    @Test
    void testToString() {
        // Given
        PatientSearchCriteria criteria = new PatientSearchCriteria();
        criteria.setName("Jean Dupont");
        criteria.setEmail("jean@example.com");

        // When
        String result = criteria.toString();

        // Then
        assertThat(result).contains("Jean Dupont");
        assertThat(result).contains("jean@example.com");
    }
}
