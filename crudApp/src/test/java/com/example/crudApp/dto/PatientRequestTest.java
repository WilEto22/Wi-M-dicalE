package com.example.crudApp.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class PatientRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidPatientRequest() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("jean.dupont@example.com");
        request.setAge(45);
        request.setAddress("123 Rue de la Santé, 75014 Paris");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void testNameIsRequired() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setEmail("test@example.com");
        request.setAge(30);
        request.setAddress("123 Rue Test");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("Le nom est obligatoire")))
                .isTrue();
    }

    @Test
    void testEmailIsRequired() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setAge(30);
        request.setAddress("123 Rue Test");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'email est obligatoire")))
                .isTrue();
    }

    @Test
    void testEmailMustBeValid() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("invalid-email");
        request.setAge(30);
        request.setAddress("123 Rue Test");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("valide")))
                .isTrue();
    }

    @Test
    void testAgeIsRequired() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("test@example.com");
        request.setAddress("123 Rue Test");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'âge est obligatoire")))
                .isTrue();
    }

    @Test
    void testAgeMinValue() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("test@example.com");
        request.setAge(-1);
        request.setAddress("123 Rue Test");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("positif")))
                .isTrue();
    }

    @Test
    void testAgeMaxValue() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("test@example.com");
        request.setAge(151);
        request.setAddress("123 Rue Test");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("150")))
                .isTrue();
    }

    @Test
    void testAddressIsRequired() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("test@example.com");
        request.setAge(30);

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().equals("L'adresse est obligatoire")))
                .isTrue();
    }

    @Test
    void testBloodTypeValidation() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("test@example.com");
        request.setAge(30);
        request.setAddress("123 Rue Test");
        request.setBloodType("INVALID");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("groupe sanguin")))
                .isTrue();
    }

    @Test
    void testValidBloodTypes() {
        // Given
        List<String> validBloodTypes = Arrays.asList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

        for (String bloodType : validBloodTypes) {
            PatientRequest request = new PatientRequest();
            request.setName("Jean Dupont");
            request.setEmail("test@example.com");
            request.setAge(30);
            request.setAddress("123 Rue Test");
            request.setBloodType(bloodType);

            // When
            Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

            // Then
            assertThat(violations).isEmpty();
        }
    }

    @Test
    void testLastVisitCannotBeInFuture() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("test@example.com");
        request.setAge(30);
        request.setAddress("123 Rue Test");
        request.setLastVisit(LocalDate.now().plusDays(1));

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("futur")))
                .isTrue();
    }

    @Test
    void testGetAllergiesFromList() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setAllergiesList(Arrays.asList("Pénicilline", "Arachides"));

        // When
        String allergies = request.getAllergies();

        // Then
        assertThat(allergies).isEqualTo("Pénicilline, Arachides");
    }

    @Test
    void testGetAllergiesWhenListIsEmpty() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setAllergiesList(Arrays.asList());

        // When
        String allergies = request.getAllergies();

        // Then
        assertThat(allergies).isNull();
    }

    @Test
    void testGetMedicalHistoryFromLists() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setChronicDiseasesList(Arrays.asList("Hypertension", "Diabète"));
        request.setMedicationsList(Arrays.asList("Lisinopril 10mg", "Metformin 500mg"));
        request.setNotes("Patient à surveiller");

        // When
        String history = request.getMedicalHistory();

        // Then
        assertThat(history).contains("Maladies chroniques: Hypertension, Diabète");
        assertThat(history).contains("Médicaments: Lisinopril 10mg, Metformin 500mg");
        assertThat(history).contains("Notes: Patient à surveiller");
    }

    @Test
    void testSetAllergiesFromString() {
        // Given
        PatientRequest request = new PatientRequest();

        // When
        request.setAllergiesFromString("Pénicilline, Arachides, Lactose");

        // Then
        assertThat(request.getAllergiesList()).hasSize(3);
        assertThat(request.getAllergiesList()).contains("Pénicilline", "Arachides", "Lactose");
    }

    @Test
    void testPhoneNumberValidation() {
        // Given
        PatientRequest request = new PatientRequest();
        request.setName("Jean Dupont");
        request.setEmail("test@example.com");
        request.setAge(30);
        request.setAddress("123 Rue Test");
        request.setPhoneNumber("invalid");

        // When
        Set<ConstraintViolation<PatientRequest>> violations = validator.validate(request);

        // Then
        assertThat(violations).isNotEmpty();
        assertThat(violations.stream()
                .anyMatch(v -> v.getMessage().contains("téléphone")))
                .isTrue();
    }

    @Test
    void testConstructorWithAllArgs() {
        // Given & When
        PatientRequest request = new PatientRequest(
                "Jean Dupont",
                "jean@example.com",
                45,
                "123 Rue Test",
                "A+",
                Arrays.asList("Pénicilline"),
                Arrays.asList("Diabète"),
                Arrays.asList("Metformin"),
                "Historique médical",
                LocalDate.now(),
                "+33612345678",
                "Marie Dupont",
                "+33698765432",
                "INS123456",
                "Notes",
                true
        );

        // Then
        assertThat(request.getName()).isEqualTo("Jean Dupont");
        assertThat(request.getEmail()).isEqualTo("jean@example.com");
        assertThat(request.getAge()).isEqualTo(45);
        assertThat(request.getIsActive()).isTrue();
    }

    @Test
    void testNoArgsConstructor() {
        // When
        PatientRequest request = new PatientRequest();

        // Then
        assertThat(request).isNotNull();
        assertThat(request.getIsActive()).isTrue();
    }
}
